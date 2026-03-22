module "ec2_demo" {
  source = "../../modules/ec2-demo"

  project_name     = var.project_name
  environment      = var.environment
  instance_type    = var.instance_type
  allowed_ssh_cidr = var.allowed_ssh_cidr
  allowed_app_cidr = var.allowed_app_cidr
  key_name         = var.key_name
  root_volume_size = var.root_volume_size
  subnet_id        = var.subnet_id
}
