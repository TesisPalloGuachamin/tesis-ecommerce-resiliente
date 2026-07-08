# Reporte Terraform: backend local y limitaciones

| Campo | Valor |
|---|---|
| Fecha de revision | 2026-07-08 |
| Rama de trabajo | `cap4-ajustes-final` |
| Commit de referencia | `acf46725bb4dfd49081eb1a18c1a7084e34f520e` |
| Terraform local registrado | `Terraform v1.8.5` en `evidencias/capitulo4-anexos-finales/08_drp/aws_cap4_ajustes_final/ambiente_local_preparacion.txt` |

## Hallazgos

| Aspecto | Implementacion encontrada | Ruta o evidencia | Limitacion reconocida |
|---|---|---|---|
| Version Terraform requerida AWS demo | `required_version = ">= 1.0.0"` | `infra/environments/demo/versions.tf` | La version minima es amplia; la ejecucion registrada uso Terraform v1.8.5. |
| Provider AWS | `hashicorp/aws` con version `~> 5.0` | `infra/environments/demo/versions.tf` | No se fija patch exacto; puede variar dentro de la serie compatible. |
| Provider Azure DRP | `hashicorp/azurerm` con version `~> 3.100` | `infra/environments/azure-drp/versions.tf` | No se fija patch exacto; puede variar dentro de la serie compatible. |
| Backend de estado | No existe bloque `backend` en los archivos `.tf` revisados. Terraform usa backend local por defecto. | `infra/environments/demo/*.tf`; `infra/environments/azure-drp/*.tf`; busqueda de `backend` | No hay estado remoto, locking remoto ni trazabilidad centralizada del estado. |
| Tratamiento de `terraform.tfstate` | `.gitignore` excluye `.terraform/`, `*.tfstate`, `*.tfstate.*` y `infra/environments/demo/terraform.tfvars`. | `.gitignore` | El estado no se versiona, pero queda local en el equipo/entorno de ejecucion. |
| Modulos AWS | `infra/environments/demo/main.tf` consume `../../modules/ec2-demo`. | `infra/environments/demo/main.tf`; `infra/modules/ec2-demo/main.tf` | La composicion es modular para el host demo, pero no implementa multiambiente productivo completo. |
| Recursos AWS | Security group, EC2, AMI AL2023, root block device gp3, user data para Docker y Docker Compose, default VPC/subnet, `null_resource` para copia de assets. | `infra/modules/ec2-demo/main.tf`; `infra/environments/demo/main.tf` | No automatiza despliegue blue/green, failover ni alta disponibilidad. |
| Puertos AWS | 22, 8080, 8082, 3000, 9090 y 15672 restringidos al CIDR del operador calculado por `api.ipify.org`. | `infra/modules/ec2-demo/main.tf` | Es adecuado para sandbox academico; no sustituye controles de red productivos. |
| Variables AWS principales | `aws_region`, `project_name`, `environment`, `instance_type`, `key_name`, `root_volume_size`. | `infra/environments/demo/variables.tf` | `key_name` tiene default vacio y debe alinearse con una llave real para acceso SSH. |
| Recursos Azure DRP | Resource group, VNet, subnet, public IP, NSG, NIC, asociacion NSG/NIC y VM Linux con cloud-init. | `infra/environments/azure-drp/main.tf` | Representa ambiente de recuperacion disenado/probado en evidencia, no failover automatico activo-activo. |
| Variables Azure principales | `project_name`, `environment`, `location`, `admin_username`, `admin_ssh_public_key`, `operator_cidr`, `vm_size`, `root_disk_size_gb`. | `infra/environments/azure-drp/variables.tf` | `admin_ssh_public_key` y `operator_cidr` usan placeholders en ejemplo; deben reemplazarse para ejecucion real. |
| Manejo de secretos | No se evidencian secretos cloud hardcodeados en `.tf`; se usan credenciales externas del proveedor/CLI y llave SSH local `~/.ssh/${var.key_name}.pem`. | `infra/environments/demo/main.tf`; `.gitignore`; evidencias `sensitive-scan.txt` existentes | No hay secret manager, cifrado remoto de state ni gobernanza empresarial del estado. |
| Evidencia `terraform validate` | Existe evidencia previa de validacion. | `evidencias/devops-drp-02/aws-demo-terraform-validate.txt` | Validacion no reemplaza `plan/apply`. |
| Evidencia `terraform plan` AWS alterno | Existe evidencia previa. | `evidencias/drp-aws-alt-01/drp-aws-alt-20260518T231958Z/terraform-plan.txt` | Pertenece a ejecucion historica DRP AWS alterna. |
| Evidencia `terraform apply` AWS alterno | Existe evidencia previa. | `evidencias/drp-aws-alt-01/drp-aws-alt-20260518T231958Z/terraform-apply.txt` | Pertenece a ejecucion historica DRP AWS alterna. |
| Evidencia `terraform plan/apply` AWS ajustes finales | Se genero nueva evidencia de EC2 para pruebas E2E y RabbitMQ. | `evidencias/capitulo4-anexos-finales/08_drp/aws_cap4_ajustes_final/terraform-plan.txt`; `terraform-apply.txt`; `terraform-output.json` | La instancia queda como sandbox de pruebas; no debe presentarse como IaC productivo HA. |
| Evidencia `terraform plan/apply` Azure | Existe evidencia previa. | `evidencias/drp-azure-01/drp-azure-20260518T233644Z/terraform-plan.txt`; `terraform-apply.txt` | No automatiza conmutacion automatica entre nubes. |

## Redaccion sugerida

La infraestructura como codigo del prototipo se implementa con Terraform usando backend local por defecto, proveedores AWS y Azure declarados por version compatible y una estructura modular para el ambiente demo EC2. Esta decision fue suficiente para reproducibilidad academica y ejecucion controlada de pruebas; sin embargo, se reconoce como limitacion que no existe backend remoto, locking distribuido, cifrado/gobernanza centralizada de estado ni automatizacion de failover productivo.

