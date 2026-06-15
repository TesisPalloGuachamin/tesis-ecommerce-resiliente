# Resumen JaCoCo posterior a mejora - checkout-service

## Proposito

Evidencia complementaria posterior al refuerzo de pruebas unitarias de `checkout-service`. No modifica el cierre funcional original y no impone umbrales minimos de cobertura.

## Comando ejecutado

```bash
cd apps/checkout-service && mvn clean test jacoco:report
```

## Pruebas agregadas

- `CheckoutDomainModelTest`: modelos `CheckoutRequest`, `Order`, `OrderItem`, `PaymentAttempt` e `InboxEvent`.
- `CheckoutEventDtoTest`: DTOs/eventos de checkout solicitado, aceptado, completado, fallido y pago procesado.
- Nuevos casos en `ProcessCheckoutUseCaseTest`: pago fallido, evento sin items e inconsistencia de idempotencia.

## Resultado de pruebas

- Resultado Maven: `BUILD SUCCESS`.
- Pruebas ejecutadas: 13.
- Fallos: 0.
- Errores: 0.
- Omitidas: 0.
- Clases analizadas por JaCoCo: 31.

## Cobertura anterior vs final

| Metrica | Antes | Final | Delta |
|---|---:|---:|---:|
| Instrucciones | 19.33% | 29.63% | +10.30 pp |
| Ramas | 2.10% | 3.06% | +0.96 pp |
| Lineas | 37.74% | 54.19% | +16.45 pp |
| Metodos | 27.37% | 51.40% | +24.03 pp |
| Complejidad | 15.12% | 28.59% | +13.47 pp |

## Meta orientativa

- Meta de lineas para `checkout-service`: al menos 50%.
- Resultado final: 54.19%.
- Estado: `ALCANZADA`.

## Artefactos

- Log posterior: `checkout-service-jacoco-after-improvement.log`
- CSV actualizado: `checkout-service/jacoco.csv`
- XML actualizado: `checkout-service/jacoco.xml`
- HTML indice actualizado: `checkout-service/index.html`

## Limitacion

La cobertura refleja pruebas automatizadas unitarias y de aplicacion con mocks. No mide pruebas manuales, carga, DRP, observabilidad, mobile ni flujos Expo.
