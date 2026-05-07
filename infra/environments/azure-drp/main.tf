locals {
  name_prefix = "${var.project_name}-${var.environment}"

  ingress_rules = {
    ssh = {
      priority = 100
      port     = 22
    }
    core_api = {
      priority = 110
      port     = 8080
    }
    checkout_service = {
      priority = 120
      port     = 8082
    }
    grafana = {
      priority = 130
      port     = 3000
    }
    prometheus = {
      priority = 140
      port     = 9090
    }
    rabbitmq_management = {
      priority = 150
      port     = 15672
    }
  }
}

resource "azurerm_resource_group" "drp" {
  name     = "${local.name_prefix}-rg"
  location = var.location

  tags = {
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "Terraform"
    Purpose     = "DRP designed recovery target"
  }
}

resource "azurerm_virtual_network" "drp" {
  name                = "${local.name_prefix}-vnet"
  address_space       = ["10.42.0.0/16"]
  location            = azurerm_resource_group.drp.location
  resource_group_name = azurerm_resource_group.drp.name
}

resource "azurerm_subnet" "drp" {
  name                 = "${local.name_prefix}-subnet"
  resource_group_name  = azurerm_resource_group.drp.name
  virtual_network_name = azurerm_virtual_network.drp.name
  address_prefixes     = ["10.42.1.0/24"]
}

resource "azurerm_public_ip" "drp" {
  name                = "${local.name_prefix}-pip"
  location            = azurerm_resource_group.drp.location
  resource_group_name = azurerm_resource_group.drp.name
  allocation_method   = "Static"
  sku                 = "Standard"
}

resource "azurerm_network_security_group" "drp" {
  name                = "${local.name_prefix}-nsg"
  location            = azurerm_resource_group.drp.location
  resource_group_name = azurerm_resource_group.drp.name

  dynamic "security_rule" {
    for_each = local.ingress_rules

    content {
      name                       = security_rule.key
      priority                   = security_rule.value.priority
      direction                  = "Inbound"
      access                     = "Allow"
      protocol                   = "Tcp"
      source_port_range          = "*"
      destination_port_range     = tostring(security_rule.value.port)
      source_address_prefix      = var.operator_cidr
      destination_address_prefix = "*"
    }
  }
}

resource "azurerm_network_interface" "drp" {
  name                = "${local.name_prefix}-nic"
  location            = azurerm_resource_group.drp.location
  resource_group_name = azurerm_resource_group.drp.name

  ip_configuration {
    name                          = "primary"
    subnet_id                     = azurerm_subnet.drp.id
    private_ip_address_allocation = "Dynamic"
    public_ip_address_id          = azurerm_public_ip.drp.id
  }
}

resource "azurerm_network_interface_security_group_association" "drp" {
  network_interface_id      = azurerm_network_interface.drp.id
  network_security_group_id = azurerm_network_security_group.drp.id
}

resource "azurerm_linux_virtual_machine" "drp" {
  name                = "${local.name_prefix}-vm"
  location            = azurerm_resource_group.drp.location
  resource_group_name = azurerm_resource_group.drp.name
  size                = var.vm_size
  admin_username      = var.admin_username

  disable_password_authentication = true
  network_interface_ids           = [azurerm_network_interface.drp.id]
  custom_data = base64encode(templatefile("${path.module}/cloud-init.tpl", {
    admin_username = var.admin_username
  }))

  admin_ssh_key {
    username   = var.admin_username
    public_key = var.admin_ssh_public_key
  }

  os_disk {
    caching              = "ReadWrite"
    storage_account_type = "Standard_LRS"
    disk_size_gb         = var.root_disk_size_gb
  }

  source_image_reference {
    publisher = "Canonical"
    offer     = "0001-com-ubuntu-server-jammy"
    sku       = "22_04-lts"
    version   = "latest"
  }

  tags = {
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "Terraform"
    Purpose     = "DRP designed recovery target"
  }
}
