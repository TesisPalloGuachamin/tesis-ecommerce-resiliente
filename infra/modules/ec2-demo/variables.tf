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

variable "key_name" {
  type        = string
  description = "Name of the SSH key pair"
  default     = ""
}

variable "root_volume_size" {
  type        = number
  description = "Size of the root volume in GB"
  default     = 20
}

variable "subnet_id" {
  type        = string
  description = "Target Subnet ID"
}
