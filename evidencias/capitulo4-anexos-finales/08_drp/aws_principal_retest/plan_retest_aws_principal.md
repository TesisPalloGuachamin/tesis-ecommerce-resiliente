# Plan de retest DRP AWS principal

RUN_ID: `cap4-drp-20260614-185235`
RUN_ID_AWS_PRINCIPAL: `cap4-drp-aws-main-20260614-191520`

## Objetivo

Ejecutar una prueba DRP controlada en AWS principal solo si no existe fuente local versionable para las metricas historicas del escenario principal. La prueba busca generar evidencia trazable de recuperacion ante incidente en sandbox, sin presentar SLA, active-active, alta disponibilidad empresarial ni failover automatico movil.

## Servicios a detener

Se detendra de forma controlada solo el servicio critico `core-api`, manteniendo persistencia y dependencias principales sin destruccion de volumenes:

- `tesis-core-api`

## Servicios a recuperar

Se recuperara el mismo servicio:

- `tesis-core-api`

## Servicios a observar

- `core-api`
- `checkout-service`
- RabbitMQ
- PostgreSQL
- Prometheus
- Grafana

## Comandos previstos

Los comandos se ejecutaran por SSH en AWS principal. Las evidencias finales deben enmascarar el host como `AWS_PRINCIPAL_HOST`.

1. Capturar estado inicial con `docker ps`, health checks HTTP, RabbitMQ, DB y Prometheus.
2. Ejecutar smoke previo: health, catalogo y checkout hasta `COMPLETED` si la API disponible lo permite.
3. Registrar `t_ultimo_respaldo_valido` como timestamp de punto recuperable operativo antes del incidente. No se destruyen volumenes.
4. Registrar `t_inicio_incidente`.
5. Ejecutar `docker stop tesis-core-api`.
6. Confirmar indisponibilidad del endpoint critico.
7. Ejecutar `docker start tesis-core-api`.
8. Esperar health `UP` y registrar `t_servicio_restaurado`.
9. Ejecutar smoke posterior y verificaciones de DB/RabbitMQ/Prometheus/Grafana.
10. Registrar `t_fin_recuperacion`.

## Riesgo

El riesgo principal es indisponibilidad temporal de `core-api` durante la ventana de retest. No se usara `docker compose down -v`, no se eliminaran volumenes y no se detendran bases de datos.

## Rollback

Si `core-api` no vuelve a estado saludable:

1. Ejecutar `docker start tesis-core-api`.
2. Si no arranca, revisar logs con `docker logs --tail`.
3. Si el contenedor no existe, levantar el stack funcional existente desde la ruta de despliegue.
4. Confirmar health antes de terminar.

## Criterios de abortar

- No hay acceso SSH confiable.
- `core-api`, `checkout-service`, RabbitMQ o PostgreSQL ya estan caidos antes del incidente.
- No es posible capturar evidencia minima de estado inicial.
- Se detecta riesgo de modificar volumenes o datos persistentes.

## Confirmacion de alcance

Esta prueba no modifica codigo, no modifica arquitectura, no reabre core4, carga, observabilidad, JaCoCo, mobile, XP ni diagramas. El objetivo es generar evidencia DRP versionable para AWS principal.
