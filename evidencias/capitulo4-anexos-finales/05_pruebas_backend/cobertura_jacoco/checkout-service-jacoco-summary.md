# Resumen JaCoCo inicial - checkout-service

## Proposito

Evidencia complementaria inicial de cobertura para las pruebas automatizadas existentes de `checkout-service`, antes del refuerzo posterior de pruebas. No sustituye la evidencia funcional ya cerrada y no impone umbrales minimos de cobertura.

## Comando ejecutado

```bash
cd apps/checkout-service && mvn clean test jacoco:report
```

## Resultado de pruebas

- Resultado Maven: `BUILD SUCCESS`.
- Pruebas ejecutadas: 4.
- Fallos: 0.
- Errores: 0.
- Omitidas: 0.
- Clases analizadas por JaCoCo: 31.

## Cobertura resumida

| Metrica | Cubierto | No cubierto | Cobertura |
|---|---:|---:|---:|
| Instrucciones | 938 | 3915 | 19.33% |
| Ramas | 13 | 607 | 2.10% |
| Lineas | 117 | 193 | 37.74% |
| Metodos | 98 | 260 | 27.37% |
| Complejidad | 101 | 567 | 15.12% |

## Artefactos

- Log: `checkout-service-jacoco.log`
- CSV/XML/HTML: los artefactos `checkout-service/jacoco.csv`, `checkout-service/jacoco.xml` y `checkout-service/index.html` fueron actualizados posteriormente con la corrida final.
- Resumen final: `checkout-service-jacoco-summary-after-improvement.md`

## Limitacion

La cobertura refleja solo las pruebas automatizadas existentes ejecutadas en esta campana complementaria. No mide pruebas manuales, carga, DRP, mobile ni flujos Expo.
