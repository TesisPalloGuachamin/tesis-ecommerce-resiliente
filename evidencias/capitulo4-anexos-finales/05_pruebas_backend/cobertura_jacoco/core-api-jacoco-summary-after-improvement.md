# Resumen JaCoCo posterior a mejora - core-api

## Proposito

Evidencia complementaria posterior al refuerzo de pruebas unitarias de `core-api`. No modifica el cierre funcional original y no impone umbrales minimos de cobertura.

## Comando ejecutado

```bash
cd apps/core-api && mvn clean test jacoco:report
```

## Pruebas agregadas

- `CoreMapperTest`: mapeo de `Product`, `User`, `Cart`, `CartItem` y `CheckoutRequest`.
- `CoreDomainModelTest`: totales de carrito/item y estados por defecto de modelos de dominio.
- `CartAndCheckoutUseCaseTest`: casos de uso de carrito, checkout, errores controlados y publicacion mockeada de evento.
- `UserAuthUseCaseTest`: login exitoso, credenciales invalidas y consulta de usuario actual.

## Resultado de pruebas

- Resultado Maven: `BUILD SUCCESS`.
- Pruebas ejecutadas: 29.
- Fallos: 0.
- Errores: 0.
- Omitidas: 0.
- Clases analizadas por JaCoCo: 89.

## Cobertura anterior vs final

| Metrica | Antes | Final | Delta |
|---|---:|---:|---:|
| Instrucciones | 20.48% | 46.67% | +26.19 pp |
| Ramas | 14.20% | 40.12% | +25.92 pp |
| Lineas | 20.77% | 50.76% | +29.99 pp |
| Metodos | 21.19% | 46.00% | +24.81 pp |
| Complejidad | 19.78% | 44.29% | +24.51 pp |

## Meta orientativa

- Meta de lineas para `core-api`: al menos 35%.
- Resultado final: 50.76%.
- Estado: `ALCANZADA`.

## Artefactos

- Log posterior: `core-api-jacoco-after-improvement.log`
- CSV actualizado: `core-api/jacoco.csv`
- XML actualizado: `core-api/jacoco.xml`
- HTML indice actualizado: `core-api/index.html`

## Limitacion

La cobertura refleja pruebas automatizadas unitarias y de aplicacion con mocks. No mide pruebas manuales, carga, DRP, observabilidad, mobile ni flujos Expo.
