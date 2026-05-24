environment       = "dev"
service_name      = "tech-challenge-ms"
subnet_ids        = ["subnet-04fa9a82683edf217", "subnet-082466561232b1028"]
vpc_id            = "vpc-05f6d4d40bf2a3f50"
cluster_name      = "ecs-tech-challenge"
container_port    = 8080
nlb_arn           = "arn:aws:elasticloadbalancing:sa-east-1:403339561517:loadbalancer/net/tech-challenge-lb/f602dac972c5f268"
repository_url    = "403339561517.dkr.ecr.sa-east-1.amazonaws.com/grupo52/tech-challenge/ecs-tech-challenge"
cidr_blocks       = ["172.31.0.0/16"]
destroy           = false

desired_count     = 0

task_env_vars = [
  {
    name: "SERVER_PORT"
    value: "8080"
  }
]