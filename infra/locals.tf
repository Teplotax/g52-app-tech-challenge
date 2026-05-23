locals {
  squad    = "grupo-52"

  common_tags = {
    environment = var.environment
    squad       = local.squad
  }

  service_tags = merge(local.common_tags,
    {
      resource = "ecs-service"
      service  = var.service_name
    })
}