# Indice de evidencias finales 4.7

Fecha de normalizacion/captura: 2026-05-30

Endpoint usado:

- `EXPO_PUBLIC_API_BASE_URL=http://3.236.180.30:8080`
- `EXPO_PUBLIC_BACKEND_ENV=AWS principal directo`

## Evidencias recomendadas para cuerpo de tesis

| Codigo | Archivo | Uso sugerido |
| --- | --- | --- |
| EV-4.7-CAP-01 | `EV-4.7-CAP-01-core-health.png` / `.txt` | Figura o fragmento de backend operativo: `core-api` health `UP`, PostgreSQL y RabbitMQ `UP`. |
| EV-4.7-CAP-02 | `EV-4.7-CAP-02b-mobile-catalogo.png` | Figura de app movil/navegador mostrando catalogo con backend AWS principal directo. |
| EV-4.7-CAP-03 | `EV-4.7-CAP-03-mobile-checkout-completed.png` | Figura principal de compra: checkout simulado con estado `COMPLETED`. |
| EV-4.7-CAP-04 | `EV-4.7-CAP-04-mobile-listing-active.png` | Figura principal de venta/publicacion: publicacion creada con estado `ACTIVE`. |
| EV-4.7-CAP-05 | `EV-4.7-CAP-05-prometheus-up.png` / `.txt` | Figura o fragmento de observabilidad: Prometheus ve `core-api` y `checkout-service` en `UP`. |

## Evidencias recomendadas para anexos

| Archivo | Uso sugerido |
| --- | --- |
| `EV-4.7-CAP-mobile-api-flow.json` | Respaldo funcional completo: login, catalogo, carrito, checkout `COMPLETED`, listing `ACTIVE`. |
| `EV-4.7-CAP-mobile-summary.md` | Resumen tecnico de la corrida y de las capturas. |
| `EV-4.7-CAP-mobile-env.txt` | Configuracion efectiva del endpoint movil. |
| `EV-4.7-CAP-mobile-typecheck.txt` | Evidencia de typecheck sin errores. |
| `EV-4.7-CAP-mobile-export.txt` | Evidencia de export web/Expo. |
| `EV-4.7-CAP-compose-ps.txt` | Estado de contenedores en EC2. |
| `EV-4.7-CAP-api-smoke.txt` | Smoke publico basico; endpoints protegidos sin token devuelven `403`, esperado. |
| `EV-4.7-CAP-network-preflight.txt` | Diagnostico inicial antes de levantar stack. |
| `EV-4.7-CAP-ssh-temp-cleanup.txt` | Evidencia de retiro de regla SSH temporal usada para levantar stack. |
| `EV-4.7-CAP-06-grafana-health.txt` | Health de Grafana. |
| `EV-4.7-CAP-06-grafana-dashboard-api.txt` | Dashboard Grafana provisionado confirmado por API. |
| `EV-4.7-CAP-06-grafana-auth-log.txt` | Login Grafana por API `200` y acceso autenticado. |

## Notas metodologicas

- No se ejecuto DRP.
- No se corrio carga de 1000 compras.
- No se cambiaron contratos backend ni codigo funcional.
- La captura visual web uso Chrome headless con CORS deshabilitado solo como herramienta local de captura, porque el navegador aplica CORS entre `localhost` y el host AWS. La validacion funcional real esta respaldada por API en `EV-4.7-CAP-mobile-api-flow.json`; Expo Go nativo consume el mismo endpoint configurado.
- La captura autenticada de Grafana por navegador no se recomienda como figura principal porque el frontend cargo el contenedor del dashboard, pero no renderizo los paneles en la captura automatica. Para tesis se recomienda usar `EV-4.7-CAP-05-prometheus-up.*` en cuerpo y dejar `EV-4.7-CAP-06-grafana-dashboard-api.txt` en anexo.
