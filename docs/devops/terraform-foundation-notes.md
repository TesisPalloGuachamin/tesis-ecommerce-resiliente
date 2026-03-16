# Notas de la Fundación Terraform (Microfase Demo)

* **Alcanze:** En esta microfase técnica, la base de Terraform solo aprovisiona de forma automatizada **1 única instancia EC2 demo** junto a su Security Group como host de fundación. Usa configuraciones por defecto de AWS para la red.
* **Componentes Listos:** La instancia generada está preconfigurada con las dependencias necesarias de _Docker_ y el plugin de _Docker Compose_ usando `user_data`, garantizando una reproducción predecible para el servidor de la tesis.
* **Limitaciones Definidas:** Por ahora, Terraform **NO despliega automatizadamente** el backend, los contenedores o el stack aplicativo de Docker de ninguna manera. Además, no se introducen balanceadores de carga (ALB), bases de datos remotas (RDS) ni clusters de Kubernetes (ECS/EKS). 
* **Próximo Paso:** Tu siguiente paso cronológico consistiría en probar el arranque manual del compose sobre este host preparado, logrando un escenario básico de recuperación de desastres (DRP) académico y reproductible, para luego automatizar el delivery vía *GitHub Actions*.
