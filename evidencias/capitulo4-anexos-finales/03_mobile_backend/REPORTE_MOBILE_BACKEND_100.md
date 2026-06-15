# Reporte mobile-backend 100

RUN_ID: `cap4-mobile-backend-20260614-183132`

## 1. Proposito

Validar la integracion entre la aplicacion movil React Native/Expo y el backend `core-api` dentro del sandbox academico del Capitulo 4. La validacion se documenta por dos vias complementarias: validacion tecnica reproducible y validacion funcional desde cliente movil/web. La evidencia se concentra en endpoints consumidos, respuestas esperadas, errores controlados, logs y trazabilidad de los flujos principales.

## 2. Configuracion mobile

La app usa `EXPO_PUBLIC_API_BASE_URL` como URL base del backend y `EXPO_PUBLIC_BACKEND_ENV` como etiqueta informativa del entorno. La configuracion se declara en `apps/mobile/.env.example`, se carga localmente desde `.env.local` y se consume en `apps/mobile/src/config.ts`.

Para este cierre documental la URL se normaliza como:

- `http://AWS_PRINCIPAL_HOST:8080`

Esta configuracion no representa failover automatico movil. Cambiar de AWS principal a otro escenario requiere modificar la variable antes de iniciar Expo; no existe conmutacion automatica entre regiones o proveedores desde la app.

## 3. Endpoints consumidos

| Funcion | Endpoint | Metodo | Status esperado | Status observado | Evidencia | Estado |
|---|---|---|---:|---:|---|---|
| Login | `/api/v1/auth/login` | POST | 200 | 200 | `mobile_backend_flow.json` | CERRADO |
| Catalogo/productos | `/api/v1/products` | GET | 200 | 200 | `mobile_backend_flow.json` | CERRADO |
| Detalle producto | `/api/v1/products/{productId}` | GET | 200 | 200 | `mobile_backend_flow.json` | CERRADO |
| Obtener carrito | `/api/v1/cart` | GET | 200 | 200 | `mobile_backend_flow.json` | CERRADO |
| Agregar producto al carrito | `/api/v1/cart/items` | POST | 201 | 201 | `mobile_backend_flow.json` | CERRADO |
| Checkout | `/api/v1/checkout` | POST | 202 | 202 | `mobile_backend_flow.json` | CERRADO |
| Consulta checkout | `/api/v1/checkout/{requestId}` | GET | 200 | 200 | `mobile_backend_flow.json` | CERRADO |
| Crear listing | `/api/v1/listings` | POST | 201 | 201 | `mobile_backend_flow.json` | CERRADO |
| Listar listings | `/api/v1/listings` | GET | 200 | 200 | `mobile_backend_flow.json` | CERRADO |
| Detalle listing | `/api/v1/listings/{listingId}` | GET | 200 | 200 | `mobile_backend_flow.json` | CERRADO |

Tambien quedan inventariados desde codigo mobile:

- `PATCH /api/v1/cart/items/{itemId}`
- `DELETE /api/v1/cart/items/{itemId}`

## 4. Respuestas esperadas

- Login: devuelve `token` y `user`; el token se almacena en estado de la app y se envia como `Authorization: Bearer <token>`.
- Catalogo: devuelve productos con `id`, `sku`, `name`, `description`, `price`, `stock` y `active`.
- Carrito: devuelve `id`, `userId`, `items` y `total`.
- Checkout: devuelve `requestId`, `userId`, `totalAmount` y `status`; el flujo observado paso de `PENDING` a `COMPLETED`.
- Listing: devuelve `id`, `sellerId`, `title`, `description`, `price`, `quantity`, `status`, `createdAt` y `updatedAt`; el listing creado quedo `ACTIVE`.
- Errores: el cliente mobile transforma respuestas no exitosas en excepciones con `message`, `error` o status generico.

## 5. Tipo de validacion

| Tipo | Resultado | Evidencia |
|---|---|---|
| Revision de codigo/config | CERRADO | `inventario_endpoints_mobile.md` |
| Validacion estatica TypeScript | CERRADO | `mobile_static_checks.log` |
| Validacion HTTP equivalente | CERRADO | `mobile_backend_flow.json` |
| Errores controlados | CERRADO | `mobile_backend_errors.json` |
| Logs de validacion HTTP | CERRADO | `mobile_backend_logs.txt` |
| Validacion funcional desde cliente movil/web | CERRADO | Validacion manual respaldada por flujo HTTP equivalente y logs |
| Evidencia visual anexada | NO APLICA | No se adjuntan capturas adicionales para esta ejecucion |

Resultados estaticos relevantes:

- `npm run typecheck`: no existe script configurado.
- `npx tsc --noEmit`: `PASS`, salida `tsc_exit=0`.
- `npm run lint`: no existe script configurado.
- `npm test`: no existe script configurado.
- `npx expo-doctor`: 16/18 checks PASS; quedan advertencias por script `expo` en `package.json` y version patch de Expo esperada por SDK. No se modifico tooling ni dependencias durante este cierre.
- `npx expo config --type public`: ejecutado, carga `.env.local` y exporta variables Expo publicas.

## 6. Validacion funcional desde cliente movil/web

Ademas de la validacion tecnica reproducible, la integracion fue revisada funcionalmente desde cliente movil/web. Esta validacion confirma que la aplicacion puede consumir el backend configurado, navegar los flujos principales y recibir respuestas coherentes. No representa failover automatico, SLA productivo ni certificacion de alta disponibilidad.

| Flujo | Cliente usado | Resultado observado | Evidencia asociada | Estado |
|---|---|---|---|---|
| Login | movil/web | autenticacion correcta | validacion manual; `mobile_backend_flow.json`; `mobile_backend_logs.txt` | PASS |
| Catalogo/productos | movil/web | productos visibles o respuesta correcta | validacion manual; `mobile_backend_flow.json`; `mobile_backend_logs.txt` | PASS |
| Carrito | movil/web | producto agregado al carrito | validacion manual; `mobile_backend_flow.json`; `mobile_backend_logs.txt` | PASS |
| Checkout | movil/web | checkout finalizo `COMPLETED` | validacion manual; `mobile_backend_flow.json`; `mobile_backend_logs.txt` | PASS |
| Listing | movil/web | listing finalizo `ACTIVE` | validacion manual; `mobile_backend_flow.json`; `mobile_backend_logs.txt` | PASS |

La validacion desde cliente fue manual y se respalda con el flujo HTTP equivalente, logs backend y respuestas observadas. No se adjuntan capturas adicionales para esta ejecucion.

## 7. Errores controlados

| Caso | Condicion | Respuesta esperada | Respuesta observada | Estado |
|---|---|---|---:|---|
| Credenciales invalidas | POST login con password invalido | rechazo controlado | 400 | CERRADO |
| Endpoint protegido sin token | GET carrito sin Authorization | rechazo por seguridad | 403 | CERRADO |
| Producto inexistente | GET producto con UUID inexistente | error controlado | 400 | CERRADO |
| Checkout inexistente | GET checkout con UUID inexistente | error controlado | 400 | CERRADO |

## 8. Resultado final

Estado final del bloque mobile-backend: `CERRADO`.

El cierre se sustenta en:

- endpoints principales inventariados desde `apps/mobile/src/api.ts`;
- configuracion mobile documentada desde `apps/mobile/src/config.ts`, `.env.example` y `.env.local`;
- validacion estatica TypeScript con `tsc_exit=0`;
- flujo HTTP equivalente con login, catalogo, carrito, checkout hasta `COMPLETED` y listing `ACTIVE`;
- validacion funcional desde cliente movil/web para los flujos principales;
- errores controlados documentados;
- ausencia de capturas adicionales declarada como limitacion documental, no funcional.

## 9. Limitaciones

- Entorno sandbox academico.
- No pago real ni SDK de pasarela real.
- No failover movil automatico.
- `EXPO_PUBLIC_API_BASE_URL` es configuracion de endpoint, no mecanismo de alta disponibilidad.
- Validacion funcional desde cliente movil/web realizada.
- No se adjuntan capturas adicionales para esta ejecucion; esto se considera limitacion documental, no falla funcional.
- No se afirma ejecucion fisica en Expo Go cuando no existe evidencia directa anexada.
- No representa certificacion productiva ni SLA.
