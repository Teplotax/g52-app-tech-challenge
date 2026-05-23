variable "service_name" {
  description = "Name of the ECS service"
}

variable "aws_region" {
  type    = string
  default = "sa-east-1"
}

variable "environment" {
  type = string
}

variable "vpc_id" {
  default = ""
}

variable "cluster_name" {
  type = string
}

variable "subnet_ids" {
  type = list(string)
}

# variable "security_groups" {
#   type = list(string)
#   default = []
# }

variable "nlb_arn" {
  type = string
}

variable "image_tag" {
  type = string
}

variable "repository_url" {
  type = string
}

variable "container_port" {
  type        = number
  description = "Port exposed by the container"
  default     = 8080
}

variable "desired_count" {
  type        = number
  default     = 1
}

variable "task_env_vars" {
  type = list(any)
}

variable "cidr_blocks" {
  type = list(string)
}

variable "destroy" {
  type    = bool
  default = false
}