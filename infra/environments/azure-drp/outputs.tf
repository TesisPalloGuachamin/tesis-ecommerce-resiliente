output "azure_drp_public_ip" {
  description = "Public IP of the Azure DRP recovery VM."
  value       = azurerm_public_ip.drp.ip_address
}

output "azure_drp_resource_group" {
  description = "Azure resource group created for the designed DRP environment."
  value       = azurerm_resource_group.drp.name
}

output "azure_drp_vm_name" {
  description = "Azure recovery VM name."
  value       = azurerm_linux_virtual_machine.drp.name
}
