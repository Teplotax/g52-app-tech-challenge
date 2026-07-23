data "terraform_remote_state" "ecs_cluster" {
  backend = "s3"
  config = {
    bucket = "teplotax-terraform-state-dev"
    key    = "dev/${var.cluster_name}/terraform.tfstate"
    region = var.aws_region
  }
}

resource "aws_iam_role" "ecs_task_execution_role" {
  name = "${var.service_name}-ecs-task-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_security_group" "ecs_tasks" {
  name        = "${var.service_name}-ecs-tasks-sg"
  description = "Allow inbound traffic on container port from within the VPC"
  vpc_id      = var.vpc_id

  ingress {
    description = "Allow NLB traffic on container port"
    from_port   = var.container_port
    to_port     = var.container_port
    protocol    = "tcp"
    cidr_blocks = var.cidr_blocks
  }

  egress {
    description = "Allow all outbound"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = local.service_tags
}

resource "aws_ecs_task_definition" "app" {
  family                   = var.service_name
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = var.task_cpu
  memory                   = var.task_memory
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn

  container_definitions = jsonencode([
    {
      name        = var.service_name
      image       = "${var.repository_url}:${var.image_tag}"
      essential   = true
      environment = var.task_env_vars
      portMappings = [
        {
          containerPort = var.container_port
          protocol      = "tcp"
        }
      ]
      dependsOn = [
        {
          containerName = "keycloak"
          condition     = "HEALTHY"
        },
        {
          containerName = "mailpit"
          condition     = "HEALTHY"
        }
      ]
      healthCheck = {
        command     = ["CMD-SHELL", "curl -f http://localhost:${var.container_port}/actuator/health || exit 1"]
        interval    = 30
        timeout     = 10
        retries     = 3
        startPeriod = 60
      }
    },
    {
      name      = "keycloak"
      image     = "${var.keycloak_repository_url}:${var.keycloak_image_tag}"
      essential = true
      environment = [
        {
          name  = "KEYCLOAK_ADMIN"
          value = "admin"
        },
        {
          name  = "KEYCLOAK_ADMIN_PASSWORD"
          value = "admin"
        },
        {
          name  = "KC_HTTP_PORT"
          value = tostring(var.keycloak_port)
        }
      ]
      portMappings = [
        {
          containerPort = var.keycloak_port
          protocol      = "tcp"
        }
      ]
      healthCheck = {
        command     = ["CMD-SHELL", "curl -f http://localhost:${var.keycloak_port}/realms/g52 || exit 1"]
        interval    = 10
        timeout     = 5
        retries     = 15
        startPeriod = 30
      }
    },
    {
      name      = "mailpit"
      image     = "axllent/mailpit:latest"
      essential = true
      environment = [
        {
          name  = "MP_SMTP_AUTH_ACCEPT_ANY"
          value = "true"
        },
        {
          name  = "MP_SMTP_AUTH_ALLOW_INSECURE"
          value = "true"
        }
      ]
      portMappings = [
        {
          containerPort = 1025
          protocol      = "tcp"
        },
        {
          containerPort = 8025
          protocol      = "tcp"
        }
      ]
      healthCheck = {
        command     = ["CMD-SHELL", "wget -q -O - http://localhost:8025/readyz || exit 1"]
        interval    = 5
        timeout     = 3
        retries     = 10
        startPeriod = 10
      }
    }
  ])
}

resource "aws_lb_target_group" "app" {
  name               = "${var.service_name}-tg"
  port               = var.container_port
  protocol           = "TCP"
  target_type        = "ip"
  vpc_id             = var.vpc_id
  preserve_client_ip = false

  health_check {
    protocol            = "HTTP"
    path                = "/actuator/health"
    matcher             = "200"
    interval            = 30
    timeout             = 10
    healthy_threshold   = 2
    unhealthy_threshold = 3
  }
}

resource "aws_lb_listener" "app" {
  load_balancer_arn = var.nlb_arn
  port              = var.container_port
  protocol          = "TCP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.app.arn
  }
}

resource "aws_ecs_service" "app" {
  name            = var.service_name
  cluster         = data.terraform_remote_state.ecs_cluster.outputs.cluster_id
  task_definition = aws_ecs_task_definition.app.arn
  desired_count   = var.desired_count
  deployment_minimum_healthy_percent = 100
  deployment_maximum_percent         = 200

  network_configuration {
    subnets          = var.subnet_ids
    security_groups  = [aws_security_group.ecs_tasks.id]
    assign_public_ip = true
  }

  capacity_provider_strategy {
    capacity_provider = "FARGATE_SPOT"
    weight            = 1
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.app.arn
    container_name   = var.service_name
    container_port   = var.container_port
  }

  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  tags = local.service_tags

  lifecycle {
    ignore_changes = [desired_count]
  }

  depends_on = [aws_lb_listener.app]
}

resource "aws_appautoscaling_target" "app" {
  max_capacity       = var.max_capacity
  min_capacity       = var.min_capacity
  resource_id        = "service/${data.terraform_remote_state.ecs_cluster.outputs.cluster_name}/${var.service_name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"

  depends_on = [aws_ecs_service.app]
}

resource "aws_appautoscaling_policy" "cpu" {
  name               = "${var.service_name}-cpu-scaling"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.app.resource_id
  scalable_dimension = aws_appautoscaling_target.app.scalable_dimension
  service_namespace  = aws_appautoscaling_target.app.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageCPUUtilization"
    }
    target_value       = var.cpu_target_value
    scale_in_cooldown  = 120
    scale_out_cooldown = 60
  }
}

resource "aws_appautoscaling_scheduled_action" "scale_down_night" {
  name               = "${var.service_name}-scale-down-20h"
  service_namespace  = aws_appautoscaling_target.app.service_namespace
  resource_id        = aws_appautoscaling_target.app.resource_id
  scalable_dimension = aws_appautoscaling_target.app.scalable_dimension
  schedule           = "cron(0 20 * * ? *)"
  timezone           = var.schedule_timezone

  scalable_target_action {
    min_capacity = 0
    max_capacity = 0
  }
}

resource "aws_appautoscaling_scheduled_action" "scale_up_morning" {
  name               = "${var.service_name}-scale-up-08h"
  service_namespace  = aws_appautoscaling_target.app.service_namespace
  resource_id        = aws_appautoscaling_target.app.resource_id
  scalable_dimension = aws_appautoscaling_target.app.scalable_dimension
  schedule           = "cron(0 8 * * ? *)"
  timezone           = var.schedule_timezone

  scalable_target_action {
    min_capacity = 1
    max_capacity = var.max_capacity
  }
}