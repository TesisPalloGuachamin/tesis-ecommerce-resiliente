variable "project_name" {
  description = "Project name used for Azure resource names and tags."
  type        = string
  default     = "tesis-ecommerce"
}

variable "environment" {
  description = "Environment name for DRP resources."
  type        = string
  default     = "drp-azure"
}

variable "location" {
  description = "Azure region for the recovery environment."
  type        = string
  default     = "eastus"
}

variable "admin_username" {
  description = "Admin user for the Azure recovery VM."
  type        = string
  default     = "azureuser"
}

variable "admin_ssh_public_key" {
  description = "SSH public key material allowed to access the recovery VM."
  type        = string
  default     = "ssh-rsa REPLACE_WITH_PUBLIC_KEY"
}

variable "operator_cidr" {
  description = "CIDR allowed to access SSH and demo ports. Use a /32 operator IP in academic tests."
  type        = string
  default     = "REPLACE_WITH_OPERATOR_IP/32"
}

variable "vm_size" {
  description = "Azure VM size for the Docker Compose recovery host."
  type        = string
  default     = "Standard_B2s"
}

variable "root_disk_size_gb" {
  description = "OS disk size for the recovery VM."
  type        = number
  default     = 30
}
