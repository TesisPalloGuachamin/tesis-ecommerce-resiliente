# DevOps Foundation - Notas de Implementación

## Propósito

Esta es la base DevOps local mínima y versionable para la tesis de e-commerce. Implementa un entorno local operativo para desarrollo y observabilidad, preparando el terreno para las microfases siguientes (Terraform y GitHub Actions) sin implementarlas aún.

## Estructura

```
deploy/compose/
├── .env.example                    # Variables de configuración local
├── docker-compose.dev.yml          # Orquestación de servicios locales
├── README.md                       # Quick start guide
├── PROMETHEUS_SETUP.md             # Instrucciones para habilitar métricas
├── validate.sh                     # Script de validación pre-levantamiento
├── prometheus/
│   └── prometheus.yml              # Configuración de scraping de métricas
└── grafana/
    ├── provisioning/
    │   ├── datasources/
    │   │   └── prometheus.yml      # Datasource Prometheus para Grafana
    │   └── dashboards/
    │       └── dashboards.yml      # Configuración de dashboards
    └── dashboards/
        └── services-overview.json  # Dashboard de visibilidad de servicios
```

## Componentes Incluidos

- **core-db**: PostgreSQL 15 para dominio Core API
- **checkout-db**: PostgreSQL 15 para dominio Checkout Service
- **rabbitmq**: RabbitMQ 3.12 con Management UI (exchange: `ecommerce.checkout.exchange`)
- **core-api**: Spring Boot 3.2 (puerto 8080, `/actuator/health` y `/actuator/metrics`)
- **checkout-service**: Spring Boot 3.2 (puerto 8082, `/api/actuator/health` y `/api/actuator/metrics`)
- **prometheus**: Recopilador de métricas (puerto 9090, solo auto-monitoring habilitado inicialmente)
- **grafana**: Visualización de métricas (puerto 3000, admin/admin123)

## Decisiones DevOps Local

1. **Dos contenedores PostgreSQL**: Simulan la separación de dominios lógicos (core_db y checkout_db) esperada en producción.
2. **Red bridge personalizada**: `tesis-network` para aislamiento de servicios.
3. **Health checks**: Todos los servicios incluyen verificaciones de salud.
4. **Prometheus + Grafana**: Observabilidad local preparada para monitoreo.

## Convivencia con Compose Previo

- El `docker-compose.yml` de la raíz del monorepo es el compose del backend.
- El `docker-compose.dev.yml` en `deploy/compose/` es el compose DevOps local.
- No se reemplazan mutuamente; son complementarios y pueden ejecutarse en contextos diferentes.

## Uso Local

```bash
# Validar estructura (opcional pero recomendado)
bash deploy/compose/validate.sh

# Crear .env desde la plantilla
cp deploy/compose/.env.example deploy/compose/.env

# Levantar servicios DevOps
docker compose -f deploy/compose/docker-compose.dev.yml up -d

# Verificar estado
docker compose -f deploy/compose/docker-compose.dev.yml ps

# Acceder a servicios
- Core API: http://localhost:8080
- Checkout Service: http://localhost:8082 (endpoints en /api/*)
- RabbitMQ Management: http://localhost:15672 (guest/guest)
- Prometheus: http://localhost:9090 (solo self-monitoring habilitado)
- Grafana: http://localhost:3000 (admin/admin123)

# Ver documentación detallada
cat deploy/compose/README.md
```

## Próximos Pasos

1. **Terraform**: Infraestructura en cloud (1 EC2 para primera iteración demo).
2. **GitHub Actions**: CI/CD y automatización de deploy.
3. **Iteración Demo**: Validar que primera versión despliega correctamente en cloud.

## Alineación con Backend

- Variables de entorno documentadas en `.env.example`.
- Respeta nombres funcionales existentes: `core_db`, `checkout_db`, `ecommerce.checkout.exchange`.
- Si hay ambigüedad en queue names o routing keys, consultar con backend para alineación.
- Core API context-path: `/` (URLs: `http://localhost:8080/...`)
- Checkout Service context-path: `/api` (URLs: `http://localhost:8082/api/...`)

## Observabilidad - Estado Actual

**Prometheus**: Levantado pero con scraping de servicios Java **deshabilitado** por defecto.

**Razón**: Los servicios Java no tienen la dependencia `micrometer-registry-prometheus` en pom.xml, por lo que no pueden exponer métricas en formato Prometheus.

**Para habilitar métricas**:
1. Ver `deploy/compose/PROMETHEUS_SETUP.md` para instrucciones detalladas
2. Agregar `micrometer-registry-prometheus` a ambos pom.xml
3. Recompilar servicios
4. Descomentar jobs en `deploy/compose/prometheus/prometheus.yml`
5. Reiniciar stack

**Grafana**: Pre-configurado con datasource Prometheus y dashboard básico, listo para cuando se habiliten métricas.

## Notas de Seguridad (Desarrollo Local)

- Credenciales por defecto (`guest/guest`, `admin123`) son solo para desarrollo local.
- En producción, todos los secretos deben inyectarse mediante variables de entorno seguras.
- El JWT_SECRET debe ser regenerado para producción.

