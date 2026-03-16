# Configuraciones de Región y Entorno
aws_region    = "us-east-1"
project_name  = "tesis"
environment   = "demo"
instance_type = "t3.medium"

# Consideraciones de Seguridad (Reglas Ingress / CIDR)
# Por motivos de seguridad y alcance académico inicial, reemplaza el placeholder ("TU_IP/32") con tu IP real.
# No expongas servicios a "0.0.0.0/0" sin necesidad justificada (p. ej. Grafana, Prometheus).
#
# Placeholder de IP: Cambia ESTO por algo como "190.123.123.123/32"
allowed_ssh_cidr = "TU_IP/32"
allowed_app_cidr = "TU_IP/32"

# Si decides que la app sea pública para demostración final, podrías usar "0.0.0.0/0" en allowed_app_cidr, 
# pero idealmente NO utilices esto aún para repositorios y dashboards internos.
