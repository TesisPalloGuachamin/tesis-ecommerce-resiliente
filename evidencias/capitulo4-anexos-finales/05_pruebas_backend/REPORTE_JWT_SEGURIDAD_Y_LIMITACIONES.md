# Reporte JWT: seguridad y limitaciones

| Campo | Valor |
|---|---|
| Fecha de revision | 2026-07-08 |
| Rama de trabajo | `cap4-ajustes-tutor-final` |
| Commit de referencia | `acf46725bb4dfd49081eb1a18c1a7084e34f520e` |
| Proyecto revisado | `apps/core-api` |

## Alcance implementado

| Aspecto | Implementacion encontrada | Ruta o clase | Redaccion sugerida para tesis |
|---|---|---|---|
| Generacion de token | El token se genera al autenticar/registrar usuario mediante el puerto `JwtProvider`. Incluye `sub` con UUID del usuario, claim `email`, fecha de emision y expiracion. | `apps/core-api/src/main/java/com/tesis/ecommerce/coreapi/infrastructure/adapter/out/security/JwtProviderAdapter.java` | El prototipo utiliza JWT stateless para identificar al usuario autenticado en las operaciones protegidas del API. |
| Algoritmo de firma | Firma HMAC con `SignatureAlgorithm.HS512` usando `Keys.hmacShaKeyFor(secret.getBytes())`. | `JwtProviderAdapter` | La firma implementada es HS512; no se evidencian llaves asimetricas ni rotacion de llaves. |
| Expiracion | `jwt.expiration: 86400000`, equivalente a 24 horas. En Docker Compose puede sobrescribirse con `JWT_EXPIRATION` / `CORE_API_JWT_EXPIRATION`. | `apps/core-api/src/main/resources/application.yml`; `deploy/compose/docker-compose.dev.yml`; `docker-compose.yml` | La expiracion configurada para sandbox es de 24 horas y es parametrizable por variable de entorno en Compose. |
| Validacion | El filtro extrae `Authorization: Bearer <token>`, valida firma/expiracion y carga un `UsernamePasswordAuthenticationToken` en el contexto de Spring Security. | `apps/core-api/src/main/java/com/tesis/ecommerce/coreapi/infrastructure/config/JwtAuthenticationFilter.java` | La validacion se realiza por filtro una vez por solicitud antes del filtro de autenticacion de Spring. |
| Endpoints publicos | `POST /api/v1/auth/register`, `POST /api/v1/auth/login`, `/actuator/health`, `/actuator/info`, `/actuator/prometheus`, `/v3/api-docs/**`, `/swagger-ui.html`, `/swagger-ui/**`. | `apps/core-api/src/main/java/com/tesis/ecommerce/coreapi/infrastructure/config/SecurityConfig.java` | Los endpoints de autenticacion, salud, metricas y documentacion quedan publicos para operacion y observabilidad del prototipo. |
| Endpoints protegidos | Cualquier endpoint no listado como publico requiere autenticacion por `.anyRequest().authenticated()`. | `SecurityConfig` | Las operaciones de catalogo, carrito, checkout y ventas quedan protegidas salvo excepciones publicas explicitas. |
| Roles o autoridades | No hay roles diferenciados. El filtro crea la autenticacion con autoridades `null`. | `JwtAuthenticationFilter` | El prototipo autentica identidad de usuario, pero no implementa autorizacion granular por roles. |
| Secret JWT | Valor por defecto en `application.yml` y Compose: `my-super-secret-key-that-should-be-at-least-32-characters-long-in-production`. Puede sobrescribirse por `JWT_SECRET` / `CORE_API_JWT_SECRET`. | `application.yml`; `deploy/compose/docker-compose.dev.yml`; `docker-compose.yml` | En sandbox se usa un secreto por defecto parametrizable. Para produccion se requiere secret manager o variable segura externa. |
| Refresh token | No se encontro implementacion de refresh token. | Busqueda en `apps/core-api/src` | La renovacion de sesion queda fuera del alcance del prototipo. |
| Revocacion | No se encontro lista de revocacion, invalidacion server-side ni logout con blacklist. | Busqueda en `apps/core-api/src` | Al ser stateless, la revocacion anticipada no esta implementada; se depende de la expiracion del token. |
| MFA | No se encontro autenticacion multifactor. | Busqueda en `apps/core-api/src` | MFA no forma parte del alcance funcional evaluado. |

## Limitaciones reconocidas

- El secreto por defecto existe en archivos de configuracion de sandbox y no debe usarse en produccion.
- No hay rotacion de secretos, `kid`, JWKS ni gestion centralizada de llaves.
- No hay refresh token ni revocacion anticipada.
- No hay roles diferenciados ni autorizacion por permisos.
- La exposicion publica de `/actuator/prometheus` responde al objetivo de observabilidad del prototipo; en produccion deberia restringirse por red, autenticacion o gateway.

