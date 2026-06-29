# Evidencia: pago simulado negativo

## Prueba ejecutada

- Comando: `cd apps/checkout-service && mvn -Dtest=ProcessCheckoutUseCaseTest#testProcessCheckoutPublishesFailureWhenPaymentFails test`
- Fecha de ejecucion: 2026-06-29 17:24:20 America/Guayaquil
- Clase: `com.tesis.ecommerce.checkoutservice.application.usecase.ProcessCheckoutUseCaseTest`
- Metodo: `testProcessCheckoutPublishesFailureWhenPaymentFails`
- Resultado: PASS

## Condicion simulada

La prueba usa un mock de `PaymentPort` para retornar:

- `transactionId = null`
- `success = false`
- `errorMessage = "simulated decline"`

Esto simula un rechazo/fallo del pago sin modificar el adaptador productivo `StripeSimulatedPaymentAdapter` y sin depender de AWS, RabbitMQ ni base de datos real.

## Resultado esperado y verificado

- La orden queda en estado `FAILED`.
- Se publica el evento `payment.processed` con resultado `false`.
- Se publica el evento `checkout.failed` con la razon `simulated decline`.
- No se publica `checkout.completed`.

## Archivos de evidencia

- `TEST-com.tesis.ecommerce.checkoutservice.application.usecase.ProcessCheckoutUseCaseTest.xml`: reporte Surefire generado por Maven.
- `mvn-test-pago-simulado-negativo.log`: salida de Maven donde constan `simulated decline`, `Tests run: 1, Failures: 0, Errors: 0, Skipped: 0` y `BUILD SUCCESS`.

## Alcance

Esta evidencia corresponde a una prueba unitaria negativa del caso de uso de checkout. No es una prueba end-to-end del flujo RabbitMQ desplegado; su objetivo es demostrar que la logica de aplicacion maneja correctamente un pago simulado fallido cuando el puerto de pago retorna `success=false`.
