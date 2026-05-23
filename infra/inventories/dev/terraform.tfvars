environment       = "dev"
service_name      = "hello-world-ms"
subnet_ids        = ["subnet-04fa9a82683edf217", "subnet-082466561232b1028", "subnet-0011dab9d6d4d1b96"]
vpc_id            = "vpc-05f6d4d40bf2a3f50"
cluster_name      = "ecs-cluster-name"
container_port    = 8080
nlb_arn           = "arn:aws:elasticloadbalancing:sa-east-1:403339561517:loadbalancer/net/application-name-lb/688f86f9aa9c196a"
repository_url    = "403339561517.dkr.ecr.sa-east-1.amazonaws.com/grupo52/tech-challenge/ecs-cluster-name"
cidr_blocks       = ["172.31.0.0/16"]
destroy           = false

desired_count     = 0

task_env_vars = [
  {
    name: "SERVER_PORT"
    value: "8080"
  }
]