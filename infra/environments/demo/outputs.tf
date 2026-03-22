output "demo_instance_public_ip" {
  description = "Public IP of the demo instance"
  value       = module.ec2_demo.public_ip
}

output "demo_instance_id" {
  description = "ID of the demo EC2 instance"
  value       = module.ec2_demo.instance_id
}
