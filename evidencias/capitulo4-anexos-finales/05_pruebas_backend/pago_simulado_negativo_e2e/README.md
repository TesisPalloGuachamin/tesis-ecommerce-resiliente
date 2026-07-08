# Pago simulado negativo E2E

Evidencia generada por `deploy/compose/scripts/cap4_pago_simulado_negativo_e2e.sh`.

- RUN_ID: `cap4-payment-negative-e2e-20260708T020358Z`
- Commit: `90d0b4f549578eb56ae900c815912c50ca76dda3`
- Condicion controlada: `PAYMENT_SIMULATOR_DECLINE_ENABLED=true` y monto `29.99`
- Resultado esperado: checkout en estado `FAILED`
- Resultado observado: `FAILED`
- Estado de la prueba: `PASS`

La evidencia unitaria previa se mantiene como complemento historico. Esta prueba valida la propagacion completa del rechazo desde API, RabbitMQ, checkout-service, persistencia y retorno consultable en core-api.
