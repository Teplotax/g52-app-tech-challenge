# ECS Cluster
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

# Security group for ECS tasks — allows inbound from VPC on container port
resource "aws_security_group" "ecs_tasks" {
  name        = "${var.service_name}-ecs-tasks-sg"
  description = "Allow inbound traffic on container port from within the VPC"
  vpc_id      = var.vpc_id

  ingress {
    description = "Allow NLB and VPC traffic on container port"
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
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn

  container_definitions = jsonencode([
    {
      name         = var.service_name
      image        = "${var.repository_url}:${var.image_tag}"
      environment  = var.task_env_vars
      portMappings = [
        {
          containerPort = var.container_port
          protocol      = "tcp"
        }
      ]
      healthCheck = {
        command     = ["CMD-SHELL", "curl -f http://localhost:${var.container_port}/actuator/health || exit 1"]
        interval    = 30
        timeout     = 10
        retries     = 3
        startPeriod = 60
      }
    }
  ])
}

resource "aws_lb_target_group" "app" {
  name        = "${var.service_name}-tg"
  port        = var.container_port
  protocol    = "TCP"
  target_type = "ip"
  vpc_id      = var.vpc_id

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
    assign_public_ip = false
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

  depends_on = [aws_lb_listener.app]
}
