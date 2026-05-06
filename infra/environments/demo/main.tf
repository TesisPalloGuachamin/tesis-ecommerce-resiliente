data "http" "myip" {
  url = "https://api.ipify.org"
}

data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

module "ec2_demo" {
  source = "../../modules/ec2-demo"

  project_name     = var.project_name
  environment      = var.environment
  instance_type    = var.instance_type
  allowed_ssh_cidr = "${chomp(data.http.myip.response_body)}/32"
  allowed_app_cidr = "${chomp(data.http.myip.response_body)}/32"
  key_name         = var.key_name
  root_volume_size = var.root_volume_size
  subnet_id        = tolist(data.aws_subnets.default.ids)[0]
}

resource "null_resource" "upload_assets" {
  triggers = {
    instance_id = module.ec2_demo.instance_id
  }

  provisioner "remote-exec" {
    inline = ["mkdir -p /home/ec2-user/tesis-ecommerce/deploy/compose"]
    connection {
      type        = "ssh"
      user        = "ec2-user"
      private_key = file(pathexpand("~/.ssh/${var.key_name}.pem"))
      host        = module.ec2_demo.public_ip
    }
  }

  provisioner "local-exec" {
    command = <<-EOT
      scp -i ~/.ssh/${var.key_name}.pem -o StrictHostKeyChecking=no -r ../../../deploy/compose/* ec2-user@${module.ec2_demo.public_ip}:/home/ec2-user/tesis-ecommerce/deploy/compose/
      scp -i ~/.ssh/${var.key_name}.pem -o StrictHostKeyChecking=no ../../../deploy/compose/.env* ec2-user@${module.ec2_demo.public_ip}:/home/ec2-user/tesis-ecommerce/deploy/compose/
    EOT
  }
}
