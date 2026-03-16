variable "project_name" {
  type        = string
  description = "Name of the project"
}

variable "environment" {
  type        = string
  description = "Environment name (e.g. demo)"
}

variable "instance_type" {
  type        = string
  description = "EC2 instance type"
  default     = "t3.medium"
}

variable "allowed_ssh_cidr" {
  type        = string
  description = "CIDR block allowed for SSH"
}

variable "allowed_app_cidr" {
  type        = string
  description = "CIDR block allowed for app access"
}
