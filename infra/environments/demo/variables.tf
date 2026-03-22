variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Name of the project"
  type        = string
  default     = "tesis"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "demo"
}

variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t3.medium"
}

variable "allowed_ssh_cidr" {
  description = "CIDR block allowed for SSH"
  type        = string
}

variable "allowed_app_cidr" {
  description = "CIDR block allowed for app accesses"
  type        = string
}

variable "key_name" {
  description = "Name of the SSH key pair"
  type        = string
  default     = ""
}

variable "root_volume_size" {
  description = "Size of the root volume in GB"
  type        = number
  default     = 20
}

variable "subnet_id" {
  description = "Target Subnet ID"
  type        = string
}
