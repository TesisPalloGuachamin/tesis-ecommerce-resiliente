Checkout Service
================

Microservicio de Spring Boot 3 con Java 21 que gestiona el proceso de checkout para la plataforma de e-commerce.

Características
- Arquitectura hexagonal
- Consumidor de eventos RabbitMQ (checkout.requested)
- Persistencia con PostgreSQL y Flyway
- Simulador de pago integrado
- Idempotencia basada en eventId
- Publicador de eventos: checkout.accepted, payment.processed, checkout.completed, checkout.failed

Compilar
--------
mvn clean package

Ejecutar localmente
--------------------
Requisitos previos:
- PostgreSQL corriendo en localhost:5432
- RabbitMQ corriendo en localhost:5672
- Base de datos 'checkout_db' creada

mvn spring-boot:run

Ejecutar en Docker
-------------------
Desde el directorio raíz del proyecto:
docker-compose up checkout-service

Rutas disponibles
-----------------
GET /api/health - Health check del servicio
GET /actuator/health - Health detail
GET /actuator/metrics - Métricas

RabbitMQ Exchanges y Routing Keys
-----------------------------------
Exchange: checkout-exchange
- checkout.requested (consumido)
- checkout.accepted (publicado)
- payment.processed (publicado)
- checkout.completed (publicado)
- checkout.failed (publicado)

Estructura de directorios
--------------------------
src/main/java/
  - domain/model/      : Entidades del dominio
  - domain/port/       : Interfaces (puertos)
  - application/       : Casos de uso y DTOs
  - infrastructure/    : Adaptadores y configuraciones

Verificar idempotencia
----------------------
El servicio verifica la tabla inbox_events para evitar procesar dos veces el mismo evento.
Si un evento con el mismo eventId ya existe, se retorna la orden existente sin reprocesar.

