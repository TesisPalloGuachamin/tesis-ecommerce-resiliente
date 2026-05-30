Sección

4.9 – Resultados

Título

Evidencia 13 — Bitácora de despliegue automatizado

Archivo

4.9-deploy-bitacora
o simplemente déjalo pegado directo en Word, sin archivo aparte.

Bitácora
Fecha/hora: 22 de marzo de 2026, madrugada (hora local de Ecuador)
Branch: feature/devops-cicd-base
Commit: d2c3431
Workflow build/push: exitoso
Workflow deploy: exitoso
Imagen core-api: christopherpallo2000/tesis-core-api:latest
Imagen checkout-service: christopherpallo2000/tesis-checkout-service:latest
Resultado docker compose ps: exitoso
Resultado health core-api: exitoso
Resultado health checkout-service: exitoso
Observaciones: Se validó el despliegue automatizado hacia la instancia EC2 demo mediante GitHub Actions. El entorno quedó operativo con PostgreSQL, RabbitMQ, Prometheus, Grafana, core-api y checkout-service en ejecución. Los endpoints de health devolvieron estado UP para ambos microservicios.
Qué capturaste exactamente

Registro resumido del despliegue automatizado, incluyendo branch, commit, workflows ejecutados, imágenes usadas y validación operativa posterior al despliegue.

Qué demuestra

Demuestra trazabilidad mínima del proceso de despliegue y evidencia acumulable para el análisis de resultados del capítulo 4.


Sección: 4.7 – Validación operativa
Título: Evidencia 10 — Smoke test post-deploy
Archivo: 4.7-smoke-test-post-deploy.png
o si lo prefieres en texto: 4.7-smoke-test-post-deploy.txt

Qué capturaste exactamente:
Ejecución del procedimiento mínimo de validación posterior al despliegue, compuesto por la verificación del estado de contenedores y la consulta de los endpoints de salud de core-api y checkout-service.

Qué demuestra:
Demuestra que el entorno desplegado puede validarse de forma rápida, repetible y objetiva tras cada despliegue automatizado.

Sección:
4.5.12 – IaC y despliegue

Título:
Evidencia 5B — Identidad válida en AWS para operación IaC

Archivo:
4.5.12-aws-sts-get-caller-identity.txt

Qué capturaste exactamente:
Salida del comando aws sts get-caller-identity, confirmando autenticación válida en la cuenta académica usada para la gestión de infraestructura.

Qué demuestra:
Demuestra que el entorno local contaba con credenciales activas para interactuar con AWS durante la preparación y validación del entorno IaC.

Sección: 4.5.12 – IaC y despliegue
Título: Evidencia 5 — Inicialización de Terraform y verificación del entorno IaC
Archivo: 4.5.12-terraform-init-success.png

Qué capturaste exactamente:
Inicialización satisfactoria de Terraform en el entorno infra/environments/demo, incluyendo módulos y provider AWS requeridos para la gestión de infraestructura.

Qué demuestra:
Demuestra que el entorno local de infraestructura como código quedó correctamente preparado para operar con Terraform sobre AWS.

Sección: 4.5.12 – IaC y despliegue
Título: Evidencia 6 — Instancia EC2 demo operativa
Archivo: 4.5.12-ec2-demo-running.png

Qué capturaste exactamente:
Captura de la consola de AWS EC2 mostrando la instancia demo en estado Running, con su dirección IP pública, tipo de instancia y grupo de seguridad asociado.

Qué demuestra:
Demuestra la disponibilidad del host de despliegue aprovisionado para la prueba y utilizado como entorno demo del sistema.

Sección: 4.5.11 – Registro de imágenes
Título: Evidencia 5 — Imagen checkout-service publicada en Docker Hub
Archivo: 4.5.11-dockerhub-checkout-tags.png

Qué capturaste exactamente:
Captura del repositorio christopherpallo2000/tesis-checkout-service en Docker Hub, mostrando la publicación de la imagen con las etiquetas latest y una etiqueta asociada al SHA corto del commit.

Qué demuestra:
Demuestra que la imagen de checkout-service fue publicada correctamente en el registro y quedó versionada para su uso en despliegues automatizados.
Sección: 4.5.11 – Registro de imágenes
Título: Evidencia 4 — Imagen core-api publicada en Docker Hub
Archivo: 4.5.11-dockerhub-core-tags.png

Qué capturaste exactamente:
Captura del repositorio christopherpallo2000/tesis-core-api en Docker Hub, mostrando la publicación de la imagen con las etiquetas latest y una etiqueta asociada al SHA corto del commit.

Qué demuestra:
Demuestra que la imagen de core-api fue publicada correctamente en el registro y quedó versionada para su uso en despliegues automatizados.

Sección: 4.5.11 – CI/CD
Título: Evidencia 3 — Trazabilidad por commit
Archivo: 4.5.11-commit-trazabilidad.png

Qué capturaste exactamente:
Captura del commit o PR asociado a la ejecución de CI/CD, mostrando la rama de origen, identificador del commit y vínculo con el proceso de integración/despliegue.

Qué demuestra:
Demuestra la trazabilidad entre el cambio fuente, la construcción de imágenes y el despliegue automatizado del entorno demo.

Sección: 4.5.11 – CI/CD
Título: Evidencia 2 — Workflow de deploy exitoso
Archivo: 4.5.11-gha-deploy-success.png

Qué capturaste exactamente:
Ejecución exitosa del workflow de GitHub Actions encargado de conectarse por SSH a la EC2 demo, actualizar imágenes, recrear contenedores y validar el estado de salud de los servicios.

Qué demuestra:
Demuestra que el despliegue al entorno demo se puede ejecutar de forma controlada, repetible y automatizada.

Sección: 4.5.11 – CI/CD
Título: Evidencia 1 — Workflow de build/push exitoso
Archivo: 4.5.11-gha-build-push-success.png

Qué capturaste exactamente:
Ejecución exitosa del workflow de GitHub Actions encargado de compilar los microservicios y publicar sus imágenes Docker en el registro.

Qué demuestra:
Demuestra que existe una tubería automatizada de construcción y publicación de imágenes para los servicios principales del sistema.

Sección: 4.7 – Validación operativa
Título: Evidencia 9 — Health operativo de checkout-service
Archivo: 4.7-checkout-service-health.txt

Qué capturaste exactamente:
Respuesta del endpoint /api/actuator/health de checkout-service, mostrando estado general UP y disponibilidad de base de datos y RabbitMQ.

Qué demuestra:
Demuestra que el servicio checkout-service se encuentra operativo y mantiene conectividad correcta con sus dependencias principales.

Sección: 4.7 – Validación operativa
Título: Evidencia 8 — Health operativo de core-api
Archivo: 4.7-core-api-health.txt

Qué capturaste exactamente:
Respuesta del endpoint /actuator/health de core-api, mostrando estado general UP y disponibilidad de base de datos y RabbitMQ.

Qué demuestra:
Demuestra que el servicio core-api se encuentra operativo y mantiene conectividad correcta con sus dependencias principales.
Título de la evidencia

Evidencia 7 — Estado operativo del stack desplegado

Nombre del archivo

4.7-docker-compose-ps.txt
o si usaste captura:
4.7-docker-compose-ps.png

Qué capturaste exactamente

Salida del comando docker compose -f docker-compose.ec2.yml ps ejecutado en la EC2 demo, mostrando los contenedores principales levantados y en estado operativo.

Qué demuestra

Demuestra que el stack desplegado se encuentra operativo en la instancia EC2, incluyendo bases de datos, mensajería, observabilidad y microservicios principales.

4.5.10 – Observabilidad

Título de la evidencia

Evidencia 12 — Grafana operativo

Nombre del archivo

4.5.10-grafana-base.png

Qué capturaste exactamente

Interfaz de Grafana accesible en el entorno desplegado, mostrando la disponibilidad de la capa visual de observabilidad.

Qué demuestra

Demuestra que la plataforma dispone de una capa básica de visualización para monitoreo y consulta operativa.

4.5.10 – Observabilidad

Título de la evidencia

Evidencia 11 — Prometheus Targets en estado UP

Nombre del archivo

4.5.10-prometheus-targets.png

Qué capturaste exactamente

Pantalla de Prometheus Targets mostrando los targets principales en estado UP.

Qué demuestra

Demuestra que la plataforma cuenta con monitoreo operativo básico y que los componentes observados están siendo detectados correctamente por Prometheus.

Sección: 4.7 – Validación operativa
Título de la evidencia: Evidencia — OpenAPI publicado en core-api desplegado
Nombre sugerido del archivo: 4.7-core-api-openapi-publicado.txt
o si prefieres captura: 4.7-core-api-openapi-publicado.png

Qué capturar exactamente

la salida completa de:
curl -i http://54.196.173.31:8080/v3/api-docs
donde se vea:
HTTP/1.1 200
Content-Type: application/json
el bloque JSON OpenAPI
y dentro del JSON las rutas:
/api/v1/auth/register
/api/v1/auth/login
/api/v1/products

Pasos exactos para tomar la evidencia

vuelve a ejecutar:
curl -i http://54.196.173.31:8080/v3/api-docs
guarda la salida en un .txt o toma captura de la terminal
nómbrala como te indiqué arriba

Qué demuestra

demuestra que el entorno real desplegado publica un contrato OpenAPI verificable
demuestra que QA ya puede apoyarse en contrato real del despliegue y no en rutas “probables”

HTTP/1.1 200 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/json
Content-Length: 5250
Date: Sun, 22 Mar 2026 16:24:34 GMT

{"openapi":"3.0.1","info":{"title":"Core API - E-commerce","description":"API del servicio core para e-commerce móvil académico","contact":{"name":"Tesis E-commerce","url":"https://github.com"},"version":"1.0.0"},"servers":[{"url":"http://54.196.173.31:8080","description":"Generated server url"}],"paths":{"/api/v1/checkout":{"post":{"tags":["checkout-controller"],"operationId":"checkout","responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/CheckoutRequestDTO"}}}}}}},"/api/v1/cart/items":{"post":{"tags":["cart-controller"],"operationId":"addItem","requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/AddCartItemRequest"}}},"required":true},"responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/CartDTO"}}}}}}},"/api/v1/auth/register":{"post":{"tags":["auth-controller"],"operationId":"register","requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/AuthRegisterRequest"}}},"required":true},"responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/AuthResponse"}}}}}}},"/api/v1/auth/login":{"post":{"tags":["auth-controller"],"operationId":"login","requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/AuthLoginRequest"}}},"required":true},"responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/AuthResponse"}}}}}}},"/api/v1/cart/items/{itemId}":{"delete":{"tags":["cart-controller"],"operationId":"deleteItem","parameters":[{"name":"itemId","in":"path","required":true,"schema":{"type":"string","format":"uuid"}}],"responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/CartDTO"}}}}}},"patch":{"tags":["cart-controller"],"operationId":"updateItem","parameters":[{"name":"itemId","in":"path","required":true,"schema":{"type":"string","format":"uuid"}}],"requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/UpdateCartItemRequest"}}},"required":true},"responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/CartDTO"}}}}}}},"/api/v1/products":{"get":{"tags":["product-controller"],"operationId":"getAllProducts","responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"type":"array","items":{"$ref":"#/components/schemas/ProductDTO"}}}}}}}},"/api/v1/products/{productId}":{"get":{"tags":["product-controller"],"operationId":"getProductById","parameters":[{"name":"productId","in":"path","required":true,"schema":{"type":"string","format":"uuid"}}],"responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/ProductDTO"}}}}}}},"/api/v1/checkout/{requestId}":{"get":{"tags":["checkout-controller"],"operationId":"getCheckoutStatus","parameters":[{"name":"requestId","in":"path","required":true,"schema":{"type":"string","format":"uuid"}}],"responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/CheckoutRequestDTO"}}}}}}},"/api/v1/cart":{"get":{"tags":["cart-controller"],"operationId":"getCart","responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/CartDTO"}}}}}}},"/api/v1/auth/me":{"get":{"tags":["auth-controller"],"operationId":"getCurrentUser","responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/UserDTO"}}}}}}}},"components":{"schemas":{"CheckoutRequestDTO":{"type":"object","properties":{"requestId":{"type":"string","format":"uuid"},"userId":{"type":"string","format":"uuid"},"totalAmount":{"type":"number"},"status":{"type":"string"}}},"AddCartItemRequest":{"type":"object","properties":{"productId":{"type":"string","format":"uuid"},"quantity":{"type":"integer","format":"int32"}}},"CartDTO":{"type":"object","properties":{"id":{"type":"string","format":"uuid"},"userId":{"type":"string","format":"uuid"},"items":{"type":"array","items":{"$ref":"#/components/schemas/CartItemDTO"}},"total":{"type":"number"}}},"CartItemDTO":{"type":"object","properties":{"id":{"type":"string","format":"uuid"},"productId":{"type":"string","format":"uuid"},"product":{"$ref":"#/components/schemas/ProductDTO"},"quantity":{"type":"integer","format":"int32"},"unitPrice":{"type":"number"},"total":{"type":"number"}}},"ProductDTO":{"type":"object","properties":{"id":{"type":"string","format":"uuid"},"sku":{"type":"string"},"name":{"type":"string"},"description":{"type":"string"},"price":{"type":"number"},"stock":{"type":"integer","format":"int32"},"active":{"type":"boolean"}}},"AuthRegisterRequest":{"type":"object","properties":{"email":{"type":"string"},"password":{"type":"string"},"name":{"type":"string"}}},"AuthResponse":{"type":"object","properties":{"token":{"type":"string"},"user":{"$ref":"#/components/schemas/UserDTO"}}},"UserDTO":{"type":"object","properties":{"id":{"type":"string","format":"uuid"},"email":{"type":"string"},"name":{"type":"string"},"enabled":{"type":"boolean"}}},"AuthLoginRequest":{"type":"object","properties":{"email":{"type":"string"},"password":{"type":"string"}}},"UpdateCartItemRequest":{"type":"object","properties":{"quantity":{"type":"integer","format":"int32"}}}}}}


Evidencias del caso MF-QA-01
Identificación del caso

ID del caso: MF-QA-01
Nombre del caso: Validación funcional mínima por API sobre entorno desplegado
Tipo de prueba: Prueba funcional mínima
Entorno evaluado: Sandbox / EC2 demo
Base URL evaluada: http://54.196.173.31:8080
Contrato publicado: http://54.196.173.31:8080/v3/api-docs
Herramienta utilizada: Postman
Fecha de ejecución: 22/03/2026
Hora de ejecución: 12:55
Objetivo del caso: Validar que el entorno desplegado permite ejecutar correctamente el flujo funcional mínimo por API, comprobando autenticación operativa y consulta de catálogo con evidencia verificable.

Evidencia 1

Nombre sugerido del archivo: EV_MF-QA-01_03_login_exitoso.png
Archivo actual recibido: 7773657b-6162-4234-886a-4be8c2391505.png

Descripción de la evidencia:
Captura de Postman ejecutando POST /api/v1/auth/login contra http://54.196.173.31:8080/api/v1/auth/login.

Qué se observa en la evidencia:

método POST
endpoint correcto /api/v1/auth/login
body JSON con:
email: qa.register.fix

Evidencias del caso MF-QA-04

Identificación del caso

ID del caso: MF-QA-04
Nombre del caso: Actualización de cantidad de ítem del carrito y verificación de recálculo
Tipo de prueba: Prueba funcional mínima
Entorno evaluado: Sandbox / EC2 demo
Base URL evaluada: http://13.221.126.228:8080
Herramienta utilizada: Postman
Fecha de ejecución: [completa la fecha real]
Hora de ejecución: [completa la hora real]
Objetivo del caso: Validar que el entorno desplegado permite actualizar la cantidad de un ítem existente del carrito mediante PATCH y que el sistema recalcula correctamente el total del ítem y el total del carrito.

Evidencia 4

Nombre sugerido del archivo: EV_MF-QA-04_03_patch_update_quantity.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Captura de Postman ejecutando PATCH /api/v1/cart/items/74cdf6a0-7591-452c-98f8-09fbb68472af contra http://13.221.126.228:8080/api/v1/cart/items/74cdf6a0-7591-452c-98f8-09fbb68472af con body JSON {"quantity": 2} en el entorno operativo vigente.

Qué se observa en la evidencia:
- método PATCH
- endpoint correcto /api/v1/cart/items/{itemId}
- URL completa con itemId real:
  74cdf6a0-7591-452c-98f8-09fbb68472af
- autenticación Bearer aplicada
- body JSON enviado:
  {
    "quantity": 2
  }
- respuesta JSON compatible con CartDTO
- carrito devuelto con:
  - id del carrito: e9a55855-c882-423d-96b0-99a637f467bd
  - userId del usuario autenticado: b5a6675b-e6d7-4833-95c6-c77f58876b42
  - ítem actualizado con:
    - productId: 8d2d08fe-3dca-468c-b34c-852a5cf96f6b
    - quantity: 2
    - unitPrice: 1299.99
    - total: 2599.98
  - total global del carrito: 2599.98

Qué demuestra:
Demuestra que el endpoint PATCH /api/v1/cart/items/{itemId} permite actualizar correctamente la cantidad de un ítem existente del carrito y que el sistema recalcula de forma coherente tanto el total del ítem como el total global del carrito en runtime.

Y deja esta nota de entorno por si luego la necesitas junto a las demás evidencias del caso:

Nota de entorno:
La URL/IP pública visible en esta evidencia puede diferir de evidencias anteriores del mismo caso o de microfases previas porque el laboratorio de AWS Academy puede cerrarse por tiempo y ser necesario reabrir el entorno. Dado que la infraestructura demo todavía no cuenta con Elastic IP fija, la recreación del laboratorio puede cambiar la IP pública sin que ello implique un sistema distinto. Se considera el mismo entorno académico redeployado y se mantiene la continuidad funcional del caso sobre la misma plataforma de prueba.

Y esta evidencia va en:

Secciones de tesis:
- 4.8.1 Pruebas funcionales ejecutadas
- 4.8.8 Evidencias recolectadas durante la ejecución de pruebas
- apoyo técnico: 4.7.2 Evidencias del backend y del núcleo principal


{
    "id": "e9a55855-c882-423d-96b0-99a637f467bd",
    "userId": "b5a6675b-e6d7-4833-95c6-c77f58876b42",
    "items": [
        {
            "id": "74cdf6a0-7591-452c-98f8-09fbb68472af",
            "productId": "8d2d08fe-3dca-468c-b34c-852a5cf96f6b",
            "product": {
                "id": "8d2d08fe-3dca-468c-b34c-852a5cf96f6b",
                "sku": "PROD001",
                "name": "Laptop Dell XPS 13",
                "description": "High-performance laptop with Intel i7 processor",
                "price": 1299.99,
                "stock": 10,
                "active": true
            },
            "quantity": 2,
            "unitPrice": 1299.99,
            "total": 2599.98
        }
    ],
    "total": 2599.98
}

Evidencias del caso MF-QA-04

Identificación del caso

ID del caso: MF-QA-04
Nombre del caso: Actualización de cantidad de ítem del carrito y verificación de recálculo
Tipo de prueba: Prueba funcional mínima
Entorno evaluado: Sandbox / EC2 demo
Base URL evaluada: http://13.221.126.228:8080
Herramienta utilizada: Postman
Fecha de ejecución: [completa la fecha real]
Hora de ejecución: [completa la hora real]
Objetivo del caso: Validar que el entorno desplegado permite actualizar la cantidad de un ítem existente del carrito y que, al consultar nuevamente el carrito, el sistema refleja correctamente la cantidad actualizada y el recálculo del total.

Evidencia 5

Nombre sugerido del archivo: EV_MF-QA-04_05_get_cart_recalculo_final.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Captura de Postman ejecutando GET /api/v1/cart contra http://13.221.126.228:8080/api/v1/cart después de actualizar la cantidad del ítem del carrito mediante PATCH en el entorno operativo vigente.

Qué se observa en la evidencia:
- método GET
- endpoint correcto /api/v1/cart
- autenticación Bearer aplicada
- código HTTP 200 OK
- respuesta JSON compatible con CartDTO
- carrito devuelto con:
  - id del carrito: e9a55855-c882-423d-96b0-99a637f467bd
  - userId del usuario autenticado: b5a6675b-e6d7-4833-95c6-c77f58876b42
  - ítem presente con:
    - id: 74cdf6a0-7591-452c-98f8-09fbb68472af
    - productId: 8d2d08fe-3dca-468c-b34c-852a5cf96f6b
    - quantity: 2
    - unitPrice: 1299.99
    - total: 2599.98
  - total global del carrito: 2599.98

Qué demuestra:
Demuestra que, después de ejecutar la actualización de cantidad del ítem del carrito, el sistema mantiene consistencia funcional al consultar nuevamente el carrito, devolviendo el mismo ítem con cantidad actualizada en 2 y con recálculo coherente del total del ítem y del total global del carrito.

Y deja también esta nota de entorno por consistencia con las demás microfases:

Nota de entorno:
La URL/IP pública visible en esta evidencia puede diferir de evidencias anteriores del mismo caso o de microfases previas porque el laboratorio de AWS Academy puede cerrarse por tiempo y ser necesario reabrir el entorno. Dado que la infraestructura demo todavía no cuenta con Elastic IP fija, la recreación del laboratorio puede cambiar la IP pública sin que ello implique un sistema distinto. Se considera el mismo entorno académico redeployado y se mantiene la continuidad funcional del caso sobre la misma plataforma de prueba.

Y esta evidencia va en:

Secciones de tesis:
- 4.8.1 Pruebas funcionales ejecutadas
- 4.8.8 Evidencias recolectadas durante la ejecución de pruebas
- apoyo técnico: 4.7.2 Evidencias del backend y del núcleo principal

Además, ya te dejo la línea de bitácora final lista para copiar:

MF-QA-04 | [fecha] [hora] | http://13.221.126.228:8080 | login=OK | products=OK | addCart=OK | patchCart=OK | getCartFinal=OK | Pass | actualización de cantidad y recálculo del carrito validado


MF-QA-04
Tipo de prueba: Microfase técnica ligera de validación contractual runtime
Entorno evaluado: Sandbox / EC2 demo
Base URL evaluada: http://13.221.126.228:8080
Contrato publicado: http://13.221.126.228:8080/v3/api-docs
Herramienta utilizada: Postman
Fecha de ejecución: [completa la fecha real]
Hora de ejecución: [completa la hora real]
Objetivo del caso: Confirmar en runtime el host vigente real del entorno demo, verificar que el contrato OpenAPI publicado corresponde al entorno operativo actual y determinar cuál es el siguiente flujo publicado realmente utilizable después de MF-QA-04, sin inventar endpoints ni payloads.

Evidencia 1

Nombre sugerido del archivo: EV_MF-QA-05_01_openapi_runtime_host_vigente.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Captura de Postman ejecutando GET /v3/api-docs contra http://13.221.126.228:8080/v3/api-docs sobre el entorno operativo vigente, con respuesta del contrato OpenAPI publicado.

Qué se observa en la evidencia:
- método GET
- endpoint correcto /v3/api-docs
- URL base vigente visible:
  http://13.221.126.228:8080
- código HTTP 200 OK
- respuesta JSON OpenAPI válida
- presencia de rutas de carrito ya confirmadas en runtime:
  - POST /api/v1/cart/items
  - GET /api/v1/cart
  - PATCH /api/v1/cart/items/{itemId}
  - DELETE /api/v1/cart/items/{itemId}
- presencia del siguiente flujo publicado posterior a las microfases de carrito:
  - POST /api/v1/checkout
  - GET /api/v1/checkout/{requestId}
- verificación de que el endpoint de checkout está publicado en runtime, pero sin requestBody utilizable visible en el contrato compartido para construir todavía una microfase funcional exacta de checkout

Qué demuestra:
Demuestra cuál es el host vigente real del entorno académico en el momento de la validación, confirma que el contrato runtime corresponde al entorno correcto y establece que el siguiente flujo posterior publicado existe en runtime. También demuestra que el flujo de checkout aún requiere clarificación contractual adicional antes de abrir una microfase funcional exacta basada en payload verificable.
Nota de entorno que conviene agregar
Nota de entorno:
La URL/IP pública visible en esta evidencia puede diferir de evidencias anteriores porque el laboratorio de AWS Academy puede cerrarse por tiempo y requerir reapertura del entorno. Dado que la infraestructura demo todavía no cuenta con Elastic IP fija, la recreación del laboratorio puede cambiar la IP pública sin que ello implique un sistema distinto. Se considera el mismo entorno académico redeployado y se mantiene la continuidad del contrato runtime sobre la misma plataforma de prueba.
Línea de bitácora final de MF-QA-05
MF-QA-05 | [fecha] [hora] | http://13.221.126.228:8080 | openapi=OK | hostVigente=OK | nextFlow=OK | contratoSuficiente=FAIL/PARCIAL | Pass | confirmación contractual runtime del siguiente flujo posterior a MF-QA-04

Yo te recomendaría dejar contratoSuficiente=FAIL/PARCIAL, porque:

sí hay siguiente flujo publicado (checkout)
pero no con body suficiente para ejecutar todavía una microfase funcional exacta de checkout
Secciones de tesis donde poner esta evidencia
Secciones de tesis:
- 4.8.1 Pruebas funcionales ejecutadas
- 4.8.8 Evidencias recolectadas durante la ejecución de pruebas
- apoyo técnico: 4.7.2 Evidencias del backend y del núcleo principal

Evidencias del caso MF-QA-06

Identificación del caso

ID del caso: MF-QA-06
Nombre del caso: Validación de identidad autenticada con endpoint auth/me
Tipo de prueba: Microfase funcional ligera
Entorno evaluado: Sandbox / EC2 demo
Base URL evaluada: http://13.221.126.228:8080
Herramienta utilizada: Postman
Fecha de ejecución: [completa la fecha real]
Hora de ejecución: [completa la hora real]
Objetivo del caso: Validar que el entorno desplegado permite consultar correctamente la identidad del usuario autenticado mediante GET /api/v1/auth/me usando un JWT válido emitido por el mismo runtime vigente.

Evidencia 1

Nombre sugerido del archivo: EV_MF-QA-06_02_auth_me_exitoso.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Captura de Postman ejecutando GET /api/v1/auth/me contra http://13.221.126.228:8080/api/v1/auth/me utilizando autenticación Bearer con token válido emitido por el entorno operativo vigente.

Qué se observa en la evidencia:
- método GET
- endpoint correcto /api/v1/auth/me
- autenticación Bearer aplicada
- código HTTP 200 OK
- respuesta JSON compatible con UserDTO
- identidad devuelta con:
  - id: b5a6675b-e6d7-4833-95c6-c77f58876b42
  - email: qa.register.fix.run03@example.com
  - name: QA Fix Run 03
  - enabled: true

Qué demuestra:
Demuestra que el runtime vigente permite resolver correctamente la identidad del usuario autenticado a partir del JWT emitido por el propio sistema, confirmando consistencia entre autenticación previa y consulta de usuario actual.

Usa también esta nota de entorno:

Nota de entorno:
La URL/IP pública visible en esta evidencia puede diferir de evidencias anteriores porque el laboratorio de AWS Academy puede cerrarse por tiempo y requerir reapertura del entorno. Dado que la infraestructura demo todavía no cuenta con Elastic IP fija, la recreación del laboratorio puede cambiar la IP pública sin que ello implique un sistema distinto. Se considera el mismo entorno académico redeployado y se mantiene la continuidad funcional del caso sobre la misma plataforma de prueba.

Y esta línea de bitácora final:

MF-QA-06 | [fecha] [hora] | http://13.221.126.228:8080 | authMe=OK | Pass | validación de identidad autenticada con JWT sobre runtime vigente

Y colócalo en estas secciones:

Secciones de tesis:
- 4.8.1 Pruebas funcionales ejecutadas
- 4.8.8 Evidencias recolectadas durante la ejecución de pruebas
- apoyo técnico: 4.7.2 Evidencias del backend y del núcleo principal

Evidencias del caso MF-QA-07

Identificación del caso

ID del caso: MF-QA-07
Nombre del caso: Acceso no autorizado a endpoint protegido auth/me sin token
Tipo de prueba: Microfase técnica ligera
Entorno evaluado: Sandbox / EC2 demo
Base URL evaluada: http://13.221.126.228:8080
Herramienta utilizada: Postman
Fecha de ejecución: [completa la fecha real]
Hora de ejecución: [completa la hora real]
Objetivo del caso: Validar que el endpoint protegido GET /api/v1/auth/me no permite acceso sin token y responde con denegación de acceso sin exponer datos del usuario.

Evidencia 1

Nombre sugerido del archivo: EV_MF-QA-07_01_auth_me_sin_token.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Captura de Postman ejecutando GET /api/v1/auth/me contra http://13.221.126.228:8080/api/v1/auth/me sin header Authorization, sin token Bearer y sin body, sobre el entorno operativo vigente.

Qué se observa en la evidencia:
- método GET
- endpoint correcto /api/v1/auth/me
- request enviada sin autenticación
- ausencia de token en la request
- código HTTP 403 Forbidden
- respuesta sin datos del usuario autenticado
- ausencia de id, email, name o cualquier contenido equivalente de perfil

Qué demuestra:
Demuestra que el endpoint protegido GET /api/v1/auth/me exige autenticación en runtime y que, al ejecutarse sin token, el sistema deniega correctamente el acceso sin exponer información del usuario.

Usa también esta nota de entorno:

Nota de entorno:
La URL/IP pública visible en esta evidencia puede diferir de evidencias anteriores porque el laboratorio de AWS Academy puede cerrarse por tiempo y requerir reapertura del entorno. Dado que la infraestructura demo todavía no cuenta con Elastic IP fija, la recreación del laboratorio puede cambiar la IP pública sin que ello implique un sistema distinto. Se considera el mismo entorno académico redeployado y se mantiene la continuidad funcional del caso sobre la misma plataforma de prueba.

Y esta línea de bitácora final:

MF-QA-07 | [fecha] [hora] | http://13.221.126.228:8080 | GET /api/v1/auth/me | sinToken=OK | http=403 | Pass | acceso no autorizado validado sin exposición de datos

Y ubícalo en:

Secciones de tesis:
- 4.8.1 Pruebas funcionales ejecutadas
- 4.8.8 Evidencias recolectadas durante la ejecución de pruebas
- apoyo técnico: 4.7.2 Evidencias del backend y del núcleo principal


Evidencias del caso MF-QA-08

Identificación del caso

ID del caso: MF-QA-08
Nombre del caso: Integración con checkout y pago simulado sobre runtime vigente
Tipo de prueba: Microfase de integración con pago simulado
Entorno evaluado: Sandbox / EC2 demo
Base URL evaluada: http://54.160.157.38:8080
Contrato publicado: http://54.160.157.38:8080/v3/api-docs
Herramienta utilizada: Postman
Fecha de ejecución: [completa la fecha real]
Hora de ejecución: [completa la hora real]
Objetivo del caso: Validar que el entorno desplegado permite ejecutar el flujo de checkout con requestBody explícito, aceptar una solicitud de checkout asociada a un carrito real y consultar posteriormente su estado mediante requestId, como base de integración con pago simulado.

Evidencia 1

Nombre sugerido del archivo: EV_MF-QA-08_01_get_cart_fuente_checkout.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Captura de Postman ejecutando GET /api/v1/cart contra http://54.160.157.38:8080/api/v1/cart utilizando autenticación Bearer válida, para identificar el cartId real usado en el checkout.

Qué se observa en la evidencia:
- método GET
- endpoint correcto /api/v1/cart
- autenticación Bearer aplicada
- código HTTP 200 OK
- carrito real del usuario autenticado
- cartId visible:
  e9a55855-c882-423d-96b0-99a637f467bd
- carrito con ítems presentes
- total del carrito visible y mayor que cero

Qué demuestra:
Demuestra que el checkout se ejecutó sobre un carrito real del runtime vigente y no sobre un identificador inventado.

Evidencia 2

Nombre sugerido del archivo: EV_MF-QA-08_02_post_checkout_exitoso.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Captura de Postman ejecutando POST /api/v1/checkout contra http://54.160.157.38:8080/api/v1/checkout con body JSON válido basado en CreateCheckoutRequest.

Qué se observa en la evidencia:
- método POST
- endpoint correcto /api/v1/checkout
- autenticación Bearer aplicada
- body JSON enviado con:
  {
    "cartId": "e9a55855-c882-423d-96b0-99a637f467bd"
  }
- código HTTP exitoso
- respuesta JSON compatible con CheckoutRequestDTO
- requestId devuelto:
  675481e0-a8a7-45eb-94c9-b6c5916733cd
- userId:
  b5a6675b-e6d7-4833-95c6-c77f58876b42
- totalAmount:
  2599.98
- status:
  PENDING

Qué demuestra:
Demuestra que el flujo de checkout acepta una solicitud válida asociada a un carrito real y genera correctamente un requestId verificable para seguimiento posterior del proceso.

Evidencia 3

Nombre sugerido del archivo: EV_MF-QA-08_03_get_checkout_status.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Captura de Postman ejecutando GET /api/v1/checkout/{requestId} contra http://54.160.157.38:8080/api/v1/checkout/675481e0-a8a7-45eb-94c9-b6c5916733cd utilizando autenticación Bearer válida para consultar el estado del checkout previamente solicitado.

Qué se observa en la evidencia:
- método GET
- endpoint correcto /api/v1/checkout/{requestId}
- requestId consultado:
  675481e0-a8a7-45eb-94c9-b6c5916733cd
- autenticación Bearer aplicada
- código HTTP 200 OK
- respuesta JSON compatible con CheckoutRequestDTO
- requestId consistente con el devuelto por el POST
- userId:
  b5a6675b-e6d7-4833-95c6-c77f58876b42
- totalAmount:
  2599.98
- status:
  PENDING

Qué demuestra:
Demuestra que el sistema no solo acepta la solicitud de checkout, sino que también permite consultar posteriormente su estado mediante requestId, dejando trazabilidad verificable del flujo de integración con pago simulado.

Usa también esta nota de entorno:

Nota de entorno:
La URL/IP pública visible en esta evidencia puede diferir de evidencias anteriores porque el laboratorio de AWS Academy puede cerrarse por tiempo y requerir reapertura del entorno. Dado que la infraestructura demo todavía no cuenta con Elastic IP fija, la recreación del laboratorio puede cambiar la IP pública sin que ello implique un sistema distinto. Se considera el mismo entorno académico redeployado y se mantiene la continuidad funcional del caso sobre la misma plataforma de prueba.

Y esta línea de bitácora final:

MF-QA-08 | [fecha] [hora] | http://54.160.157.38:8080 | login=OK | getCart=OK | postCheckout=OK | getCheckoutStatus=OK | Pass | integración con checkout/pago simulado validada sobre runtime vigente

Y ubícalo en estas secciones:

Secciones de tesis:
- 4.8.5 Pruebas de integración con pagos simulados
- 4.9.4 Resultados de integración con pagos simulados
- apoyo técnico: 4.7.5 Evidencias de integración con Stripe
- apoyo general: 4.8.8 Evidencias recolectadas durante la ejecución de pruebas

RECORRIDO COMPLETO — BQ-QA-IA-01

Evidencia 1

Nombre sugerido del archivo: EV_BQ-QA-IA-01_01_get_cart_fuente.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Captura de Postman ejecutando GET /api/v1/cart contra http://54.160.157.38:8080/api/v1/cart utilizando autenticación Bearer válida, para identificar el cartId real utilizado como fuente del flujo integrado.

Qué se observa en la evidencia:
- método GET
- endpoint correcto /api/v1/cart
- autenticación Bearer aplicada
- código HTTP 200 OK
- carrito real del usuario autenticado
- cartId visible
- total del carrito visible

Qué demuestra:
Demuestra que la corrida de integración ampliada se ejecutó sobre un carrito real y trazable del entorno vigente.

Evidencia 2

Nombre sugerido del archivo: EV_BQ-QA-IA-01_02_post_checkout_requestid.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Captura de Postman ejecutando POST /api/v1/checkout contra http://54.160.157.38:8080/api/v1/checkout con cartId real, generando un requestId verificable para seguimiento posterior.

Qué se observa en la evidencia:
- método POST
- endpoint correcto /api/v1/checkout
- autenticación Bearer aplicada
- body JSON con cartId real
- código HTTP exitoso
- requestId devuelto
- status inicial PENDING
- totalAmount visible

Qué demuestra:
Demuestra que el flujo integrado acepta una solicitud de checkout válida sobre carrito real y genera un requestId trazable.

Evidencia 3

Nombre sugerido del archivo: EV_BQ-QA-IA-01_03_get_checkout_completed.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Captura de Postman ejecutando GET /api/v1/checkout/{requestId} contra http://54.160.157.38:8080/api/v1/checkout/{requestId} utilizando autenticación Bearer válida, para consultar el estado final del checkout previamente solicitado.

Qué se observa en la evidencia:
- método GET
- endpoint correcto /api/v1/checkout/{requestId}
- requestId consultado
- autenticación Bearer aplicada
- código HTTP 200 OK
- respuesta JSON consistente
- status final COMPLETED

Qué demuestra:
Demuestra que el flujo crítico integrado no solo genera correctamente el requestId, sino que además alcanza un estado final observable y verificable.

Evidencia 4

Nombre sugerido del archivo: EV_BQ-QA-IA-01_04_variante_carrito_actualizado.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Captura de Postman consultando un requestId correspondiente a una corrida previa del flujo con actualización del carrito antes del checkout.

Qué se observa en la evidencia:
- requestId visible:
  f1a974ed-e7c1-40a9-b634-9c305b3a6a3a
- totalAmount:
  7799.94
- status final:
  COMPLETED
- respuesta consistente con una corrida de carrito previamente modificado

Qué demuestra:
Demuestra que la integración ampliada del flujo crítico se mantuvo consistente también en la variante con actualización previa del carrito.

Evidencia 5

Nombre sugerido del archivo: EV_BQ-QA-IA-01_05_trazabilidad_observable.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Fragmento de logs representativo del procesamiento del checkout asociado al flujo integrado evaluado.

Qué se observa en la evidencia:
- recepción del evento de checkout
- procesamiento del flujo
- publicación de checkout.completed
- confirmación de procesamiento exitoso

Qué demuestra:
Demuestra que la integración ampliada deja trazabilidad observable del procesamiento y no se limita a la aceptación del request desde la API pública.

Resumen final del bloque BQ-QA-IA-01

Resultado final: PASS

Qué se validó realmente:
Se validó de forma integrada y repetible el flujo crítico ya estabilizado del prototipo, incluyendo carrito real con cartId, checkout con requestId, consulta posterior con estado final COMPLETED y una variante consistente con actualización previa del carrito.

Por qué queda defendible para tesis:
Queda defendible porque se apoya en corridas reales del flujo crítico sobre entorno sandbox, con evidencia verificable, requestId trazable, consistencia funcional y repetición mínima suficiente para sostener el bloque como campaña de integración ampliada y no como una ejecución aislada.

Conclusión breve del bloque:
BQ-QA-IA-01 queda cerrado en PASS. Se consolidó la validación integrada del flujo crítico checkout/pago simulado, incluyendo estado final observable y una variante con carrito previamente actualizado, lo que fortalece la base empírica del Capítulo 4.

Línea de bitácora final:
BQ-QA-IA-01 | [fecha] [hora] | http://54.160.157.38:8080 | cart=OK | checkout=OK | finalStatus=COMPLETED | variantUpdatedCart=OK | Pass | integración ampliada del flujo crítico validada con estado final observable y variante con carrito actualizado

Evidencias del bloque BQ-QA-MO-01
Evidencia 1

Nombre sugerido del archivo: EV_BQ-QA-MO-01_01_get_cart_fuente.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Captura de Postman ejecutando GET /api/v1/cart contra http://54.160.157.38:8080/api/v1/cart utilizando autenticación Bearer válida, para identificar el cartId real usado como base del checkout observado.

Qué se observa en la evidencia:

método GET
endpoint correcto /api/v1/cart
autenticación Bearer aplicada
código HTTP 200 OK
carrito real del usuario autenticado
cartId visible
ítems presentes en el carrito
total visible y coherente

Qué demuestra:
Demuestra que la validación de mensajería observable parte de un carrito real del runtime vigente y no de un identificador inventado, dejando una base trazable para el flujo posterior de checkout.
Evidencia 2

Nombre sugerido del archivo: EV_BQ-QA-MO-01_02_post_checkout_requestid.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Captura de Postman ejecutando POST /api/v1/checkout contra http://54.160.157.38:8080/api/v1/checkout con cartId real, obteniendo un requestId verificable para el seguimiento del procesamiento asíncrono.

Qué se observa en la evidencia:

método POST
endpoint correcto /api/v1/checkout
autenticación Bearer aplicada
body JSON con cartId real
código HTTP exitoso
respuesta JSON compatible con CheckoutRequestDTO
requestId devuelto
userId visible
totalAmount visible
status inicial PENDING

Qué demuestra:
Demuestra que el sistema acepta correctamente una solicitud de checkout sobre un carrito real y genera un requestId utilizable para observar y correlacionar el flujo asíncrono posterior.

[3:59, 4/4/2026] Christopher: Evidencia 3

Nombre sugerido del archivo: EV_BQ-QA-MO-01_03_get_checkout_completed.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Descripción de la evidencia:
Captura de Postman ejecutando GET /api/v1/checkout/{requestId} contra http://54.160.157.38:8080/api/v1/checkout/{requestId} utilizando autenticación Bearer válida, para consultar el estado posterior del checkout previamente solicitado.

Qué se observa en la evidencia:

método GET
endpoint correcto /api/v1/checkout/{requestId}
requestId consultado visible
autenticación Bearer aplicada
código HTTP 200 OK
respuesta JSON consistente con el checkout creado
requestId consistente con el devuelto en el POST
userId visible
totalAmount visible
status final COMPLETED

Qué demuestra:
Demuestra que el flujo observado no solo genera el requestId, sino que además permite verificar posteriormente un estado final observable del checkout, cerrando la trazabilidad funcional del bloque.
[3:59, 4/4/2026] Christopher: Resumen final del bloque BQ-QA-MO-01

Resultado final: PASS

Conclusión breve del bloque:
BQ-QA-MO-01 queda cerrado en PASS. El bloque permitió validar que el flujo de checkout/pago simulado deja trazabilidad verificable desde un carrito real y un requestId válido hasta un estado final observable del checkout, aportando base empírica suficiente para la validación de mensajería observable del flujo crítico.

Línea de bitácora final:
BQ-QA-MO-01 | [fecha] [hora] | http://54.160.157.38:8080 | getCart=OK | postCheckout=OK | getCheckoutStatus=COMPLETED | Pass | mensajería observable del flujo checkout/pago simulado validada con requestId y estado final observable
Evidencias del bloque BQ-QA-RL-01
Evidencia 1

Nombre sugerido del archivo: EV_BQ-QA-RL-01_01_linea_base_prefallo.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Comandos exactos usados para generar la evidencia:

cd /home/ec2-user/tesis-ecommerce/deploy/compose
docker compose -f docker-compose.ec2.yml ps checkout-service
curl -s http://localhost:8082/api/actuator/health
date "+%Y-%m-%d %H:%M:%S"

Descripción de la evidencia:
Captura de línea base pre-fallo mostrando que checkout-service se encontraba operativo antes de inducir el incidente.

Qué se observa en la evidencia:

servicio checkout-service en estado Up/healthy
health check del servicio en estado UP
timestamp de línea base
entorno estable antes de la intervención

Qué demuestra:
Demuestra que la campaña de resiliencia ligera parte de una base funcional válida y comparable, necesaria para interpretar correctamente la degradación y la recuperación posteriores.

Evidencia 2

Nombre sugerido del archivo: EV_BQ-QA-RL-01_02_stop_controlado.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Comandos exactos usados para generar la evidencia:

docker compose -f docker-compose.ec2.yml stop checkout-service
docker compose -f docker-compose.ec2.yml ps checkout-service
date "+%Y-%m-%d %H:%M:%S"

Descripción de la evidencia:
Captura del stop controlado y deliberado de checkout-service durante la campaña de resiliencia ligera.

Qué se observa en la evidencia:

ejecución del stop controlado
servicio detenido
timestamp del inicio del incidente
ausencia del contenedor activo en la verificación posterior inmediata

Qué demuestra:
Demuestra que el incidente fue inducido de forma controlada, breve y reversible, sin implicar caída total del entorno ni pruebas de DRP pesado.
Evidencia 3

Nombre sugerido del archivo: EV_BQ-QA-RL-01_03_health_caido.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Comandos exactos usados para generar la evidencia:

curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8082/api/actuator/health
date "+%Y-%m-%d %H:%M:%S"

Descripción de la evidencia:
Captura de la degradación observable durante la caída del servicio intervenido.

Qué se observa en la evidencia:

health check fallido durante la ventana del incidente
código observable de indisponibilidad:
000
timestamp de la primera falla observable

Qué demuestra:
Demuestra que el fallo tuvo un impacto real y medible sobre el componente intervenido y que dicho impacto pudo observarse empíricamente desde QA.

Evidencia 4

Nombre sugerido del archivo: EV_BQ-QA-RL-01_04_restablecimiento_tecnico.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Comandos exactos usados para generar la evidencia:

docker compose -f docker-compose.ec2.yml start checkout-service
date "+%Y-%m-%d %H:%M:%S"
docker compose -f docker-compose.ec2.yml ps checkout-service
curl -s http://localhost:8082/api/actuator/health

Descripción de la evidencia:
Captura del restablecimiento técnico de checkout-service después del incidente controlado.

Qué se observa en la evidencia:

ejecución de start del servicio
contenedor nuevamente levantado
timestamp de restablecimiento técnico
health del servicio nuevamente en estado UP

Qué demuestra:
Demuestra que el sistema puede recuperar operatividad técnica básica del componente intervenido dentro de la misma campaña, en línea con recuperación controlada, medible y verificable.

Evidencia 5

Nombre sugerido del archivo: EV_BQ-QA-RL-01_05_recuperacion_funcional_minima.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Request exacto usado para generar la evidencia:

Método: GET

http://54.160.157.38:8080/api/v1/products

Header:

Authorization: Bearer TU_TOKEN_VALIDO

Descripción de la evidencia:
Captura de un request funcional posterior exitoso tras el restablecimiento técnico del servicio.

Qué se observa en la evidencia:

request funcional exitoso posterior al incidente
respuesta válida del sistema
endpoint nuevamente operativo
productos reales visibles en la respuesta

Qué demuestra:
Demuestra que el incidente no solo fue superado técnicamente, sino que además el sistema recuperó capacidad funcional mínima observable dentro de la misma ventana de prueba.
Evidencia 6

Nombre sugerido del archivo: EV_BQ-QA-RL-01_06_repeticion_minima_controlada.png
Archivo actual recibido: [pon aquí el nombre real si lo tienes]

Comandos y requests exactos usados para generar la evidencia de repetición mínima:

Línea base repetida

docker compose -f docker-compose.ec2.yml ps checkout-service
curl -s http://localhost:8082/api/actuator/health
date "+%Y-%m-%d %H:%M:%S"

Stop repetido

docker compose -f docker-compose.ec2.yml stop checkout-service
docker compose -f docker-compose.ec2.yml ps checkout-service
date "+%Y-%m-%d %H:%M:%S"

Degradación repetida

curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8082/api/actuator/health
date "+%Y-%m-%d %H:%M:%S"

Restablecimiento repetido
docker compose -f docker-compose.ec2.yml start checkout-service
date "+%Y-%m-%d %H:%M:%S"
docker compose -f docker-compose.ec2.yml ps checkout-service
curl -s http://localhost:8082/api/actuator/health

Verificación funcional repetida
Resumen final del bloque BQ-QA-RL-01

Resultado final: PASS

Qué se validó realmente:
Se validó una primera campaña de resiliencia ligera sobre el flujo crítico ya estabilizado, mediante un fallo controlado y reversible sobre checkout-service, con línea base pre-fallo, degradación observable, restablecimiento técnico y recuperación funcional mínima posterior. Además, la repetición mínima del mismo fallo ligero volvió a mostrar degradación observable, restablecimiento técnico y recuperación funcional mínima, reforzando la repetibilidad básica del bloque.

Por qué queda defendible para tesis:
Queda defendible porque no se trató de un desastre total ni de una simulación teórica, sino de una campaña acotada, observable y documentable, alineada con la línea oficial del proyecto: recuperación controlada, medible y verificable; no alta disponibilidad enterprise ni failover perfecto.

Conclusión breve del bloque:
BQ-QA-RL-01 queda cerrado en PASS. Se ejecutó una primera campaña de resiliencia ligera con fallo controlado sobre checkout-service, incluyendo línea base pre-fallo, degradación observable con health caído, restablecimiento técnico, recuperación funcional mínima posterior y repetición mínima del mismo incidente con resultado consistente.

Nota de entorno:
La campaña se ejecutó en sandbox, sobre el mismo entorno operativo ya utilizado para base funcional, mensajería observable e integración ampliada. Su interpretación debe mantenerse dentro del alcance oficial del proyecto: recuperación controlada, medible y verificable; no continuidad perfecta ni failover enterprise. Si la IP pública varió entre capturas por recreación del laboratorio AWS Academy, debe documentarse como parte del mismo entorno redeployado mientras no exista Elastic IP fija.

Línea de bitácora final:
BQ-QA-RL-01 | [fecha] [hora] | checkout-service | baseline=OK | stop=OK | degradation=health000 | recoveryTechnical=OK | recoveryFunctional=OK | repetition=OK | Pass | primera campaña de resiliencia ligera validada con fallo controlado reversible