environment       = "dev"
service_name      = "tech-challenge-ms"
subnet_ids        = ["subnet-04fa9a82683edf217", "subnet-082466561232b1028"]
vpc_id            = "vpc-05f6d4d40bf2a3f50"
cluster_name      = "ecs-tech-challenge"
container_port    = 8080
nlb_arn           = "arn:aws:elasticloadbalancing:sa-east-1:403339561517:loadbalancer/net/tech-challenge-lb/ab041078c8d35bb0"
repository_url    = "403339561517.dkr.ecr.sa-east-1.amazonaws.com/grupo52/tech-challenge/ecs-tech-challenge"
keycloak_repository_url = "403339561517.dkr.ecr.sa-east-1.amazonaws.com/grupo52/tech-challenge/ecs-tech-challenge-keycloak"
cidr_blocks       = ["172.31.0.0/16"]
destroy           = false

desired_count     = 1
min_capacity      = 0
max_capacity      = 2
cpu_target_value  = 70

task_env_vars = [
  {
    name: "SERVER_PORT"
    value: "8080"
  },
  {
    name: "SPRING_PROFILES_ACTIVE"
    value: "docker"
  },
  {
    name: "MAIL_HOST"
    value: "localhost"
  },
  {
    name: "MAIL_PORT"
    value: "1025"
  },
  {
    name: "MAIL_USERNAME"
    value: "dev@local"
  },
  {
    name: "MAIL_PASSWORD"
    value: "dev"
  },
  {
    name: "MAIL_SMTP_AUTH"
    value: "false"
  },
  {
    name: "MAIL_SMTP_STARTTLS"
    value: "false"
  },
  {
    name: "APPROVAL_SECRET"
    value: "local-dev-secret-change-me"
  },
  {
    name: "APP_BASE_URL"
    value: "http://tech-challenge-lb-f602dac972c5f268.elb.sa-east-1.amazonaws.com:8080"
  },
  {
    name: "APPROVAL_TTL_MINUTES"
    value: "1440"
  },
  {
    name: "KEYCLOAK_JWK_SET_URI"
    value: "http://localhost:9000/realms/g52/protocol/openid-connect/certs"
  }
]
