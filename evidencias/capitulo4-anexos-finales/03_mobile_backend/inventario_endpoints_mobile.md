# Inventario de endpoints consumidos por la aplicacion movil

RUN_ID: `cap4-mobile-backend-20260614-183132`

## Configuracion de entorno

La aplicacion movil configura el backend mediante variables Expo:

- `EXPO_PUBLIC_API_BASE_URL`: URL base del backend consumido por el cliente HTTP.
- `EXPO_PUBLIC_BACKEND_ENV`: etiqueta informativa del entorno seleccionado.
- Archivo de referencia: `apps/mobile/.env.example`.
- Archivo local observado: `apps/mobile/.env.local`, normalizado documentalmente como `http://AWS_PRINCIPAL_HOST:8080`.
- Archivo de lectura en runtime: `apps/mobile/src/config.ts`.

Estas variables seleccionan un endpoint unico al iniciar la app. No implementan failover automatico, alta disponibilidad ni conmutacion movil entre nubes.

## Cliente HTTP

- Archivo: `apps/mobile/src/api.ts`.
- Implementacion: `fetch`.
- Base URL: `API_BASE_URL` importado desde `apps/mobile/src/config.ts`.
- Headers: `Accept: application/json`, `Content-Type: application/json`.
- Token: cuando existe, se envia como `Authorization: Bearer <token>`.
- Timeout: `12000 ms` con `AbortController`.
- Errores controlados en cliente:
  - respuesta HTTP no exitosa: usa `payload.message`, `payload.error` o mensaje generico con status;
  - timeout: `La API no respondio a tiempo.`;
  - JSON invalido: `La API devolvio una respuesta no valida.`

## Tabla de endpoints

| Funcion movil | Endpoint backend | Metodo | Request esperado | Response esperada | Error controlado | Archivo mobile | Evidencia |
|---|---|---|---|---|---|---|---|
| Login | `/api/v1/auth/login` | POST | `{ email, password }` | `token`, `user` | credenciales invalidas | `apps/mobile/src/api.ts` | `mobile_backend_flow.json`, `mobile_backend_errors.json` |
| Catalogo/productos | `/api/v1/products` | GET | header `Authorization` | lista de productos activos | token ausente/invalido | `apps/mobile/src/api.ts`, `App.tsx` | `mobile_backend_flow.json` |
| Detalle de producto | `/api/v1/products/{productId}` | GET | `productId`, header `Authorization` | producto | producto inexistente | `apps/mobile/src/api.ts`, `App.tsx` | `mobile_backend_flow.json`, `mobile_backend_errors.json` |
| Obtener carrito | `/api/v1/cart` | GET | header `Authorization` | carrito del usuario | token ausente/invalido | `apps/mobile/src/api.ts`, `App.tsx` | `mobile_backend_flow.json`, `mobile_backend_errors.json` |
| Agregar item al carrito | `/api/v1/cart/items` | POST | `{ productId, quantity }` | carrito actualizado | producto no disponible o token invalido | `apps/mobile/src/api.ts`, `App.tsx` | `mobile_backend_flow.json` |
| Actualizar item del carrito | `/api/v1/cart/items/{itemId}` | PATCH | `{ quantity }` | carrito actualizado | cantidad invalida o item no autorizado | `apps/mobile/src/api.ts`, `App.tsx` | inventariado desde codigo mobile |
| Eliminar item del carrito | `/api/v1/cart/items/{itemId}` | DELETE | `itemId`, header `Authorization` | carrito actualizado | item inexistente o no autorizado | `apps/mobile/src/api.ts`, `App.tsx` | inventariado desde codigo mobile |
| Checkout | `/api/v1/checkout` | POST | `{ cartId }` | solicitud `PENDING`/aceptada | carrito vacio o token invalido | `apps/mobile/src/api.ts`, `App.tsx` | `mobile_backend_flow.json` |
| Consulta checkout | `/api/v1/checkout/{requestId}` | GET | `requestId` | estado `PENDING`, `COMPLETED` o `FAILED` | checkout inexistente | `apps/mobile/src/api.ts`, `App.tsx` | `mobile_backend_flow.json`, `mobile_backend_errors.json` |
| Crear listing/venta | `/api/v1/listings` | POST | `{ title, description, price, quantity }` | listing `ACTIVE` | validacion de campos o token invalido | `apps/mobile/src/api.ts`, `App.tsx` | `mobile_backend_flow.json` |
| Listar listings | `/api/v1/listings` | GET | header `Authorization` | lista de listings | token ausente/invalido si politica aplica | `apps/mobile/src/api.ts`, `App.tsx` | `mobile_backend_flow.json` |
| Detalle listing | `/api/v1/listings/{listingId}` | GET | `listingId` | listing | listing inexistente | `apps/mobile/src/api.ts` | `mobile_backend_flow.json` |

## Observacion metodologica

El inventario se obtiene desde el codigo fuente mobile y se valida con un flujo HTTP equivalente contra los endpoints reales del backend. No se afirma ejecucion fisica final en Expo Go.
