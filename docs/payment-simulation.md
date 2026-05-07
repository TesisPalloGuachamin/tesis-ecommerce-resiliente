# Pago Simulado Stripe

PAY-SIM-01 formaliza el pago del checkout como un adaptador Stripe simulado.
El servicio no usa SDK de Stripe, no requiere credenciales y no procesa cargos reales.

El flujo vigente es:

1. `core-api` recibe `POST /api/v1/checkout` con el carrito autenticado.
2. `core-api` publica `checkout.requested` mediante RabbitMQ.
3. `checkout-service` consume el evento y crea la orden.
4. `StripeSimulatedPaymentAdapter` aprueba el pago en sandbox academico y genera un identificador `stripe_sim_*`.
5. `checkout-service` publica `payment.processed` y `checkout.completed`.
6. `core-api` actualiza el checkout a `COMPLETED`.

Para la tesis, este bloque debe describirse como integracion simulada estilo Stripe o adaptador Stripe simulado. No debe afirmarse que existen pagos reales, cobros productivos, tarjetas reales ni liquidacion financiera.

En mobile, la URL del backend se cambia con `EXPO_PUBLIC_API_BASE_URL` en `apps/mobile/.env.local`, por ejemplo:

```text
EXPO_PUBLIC_API_BASE_URL=http://44.195.21.215:8080
```
