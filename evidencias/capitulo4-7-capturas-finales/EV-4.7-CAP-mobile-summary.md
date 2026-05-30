# EV-4.7-CAP Mobile / Backend Summary

Fecha local: 2026-05-30

## Endpoint usado

- `EXPO_PUBLIC_API_BASE_URL=http://3.236.180.30:8080`
- `EXPO_PUBLIC_BACKEND_ENV=AWS principal directo`

## Estado operativo

- La EC2 AWS estaba encendida, pero el stack Docker no estaba corriendo al iniciar la captura.
- Se levantaron los compose existentes en la EC2:
  - `docker-compose.functional.yml`
  - `docker-compose.observability.yml`
- `core-api` quedo accesible publicamente en `http://3.236.180.30:8080`.
- `Prometheus` y `Grafana` quedaron accesibles en sus puertos de observabilidad.

## Flujo funcional validado

Evidencia principal: `EV-4.7-CAP-mobile-api-flow.json`.

Resultado:

- login: `PASS`
- catalogo: `2` productos
- carrito: creado/consultado correctamente
- checkout: `COMPLETED`
- publicacion/listing: `ACTIVE`

Identificadores:

- runId: `ev47-20260530T071822Z`
- checkoutId: `d1b722bf-8a10-4ddd-8307-ebedd9850bc4`
- listingId: `314fb1db-fe69-4aab-b24b-987a9ef8a255`

## Capturas visuales

Generadas automaticamente:

- `EV-4.7-CAP-01-core-health.png`
- `EV-4.7-CAP-02-mobile-login-catalogo.png`
- `EV-4.7-CAP-05-prometheus-up.png`

No generadas automaticamente de forma confiable:

- `EV-4.7-CAP-03-mobile-checkout-completed.png`
- `EV-4.7-CAP-04-mobile-listing-active.png`
- dashboard Grafana autenticado con paneles cargados

Motivo: la automatizacion headless no pudo completar de forma estable la interaccion visual en React Native Web/Grafana. La validacion funcional equivalente si quedo registrada por API real en `EV-4.7-CAP-mobile-api-flow.json`.

## Pasos manuales recomendados para completar capturas visuales en telefono

1. En `apps/mobile/.env.local` confirmar:

```text
EXPO_PUBLIC_API_BASE_URL=http://3.236.180.30:8080
EXPO_PUBLIC_BACKEND_ENV=AWS principal directo
```

2. Levantar Expo Go:

```bash
cd apps/mobile
npm run expo -- start --lan --port 8083
```

3. Escanear el QR desde Expo Go.
4. Tomar capturas:
   - login/catalogo
   - checkout con estado `COMPLETED`
   - publicacion creada con estado `ACTIVE`

## Nota de alcance

Estas evidencias corresponden a implementacion funcional y observabilidad basica para Capitulo 4.7. No se ejecuto DRP, no se corrio carga de 1000 compras y no se modificaron contratos backend.
