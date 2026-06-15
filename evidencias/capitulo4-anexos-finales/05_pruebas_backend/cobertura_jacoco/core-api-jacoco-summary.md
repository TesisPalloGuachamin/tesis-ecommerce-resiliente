# Resumen JaCoCo inicial - core-api

## Proposito

Evidencia complementaria inicial de cobertura para las pruebas automatizadas existentes de `core-api`, antes del refuerzo posterior de pruebas. No sustituye la evidencia funcional ya cerrada y no impone umbrales minimos de cobertura.

## Comando ejecutado

```bash
cd apps/core-api && mvn clean test jacoco:report
```

## Resultado de pruebas

- Resultado Maven: `BUILD SUCCESS`.
- Pruebas ejecutadas: 11.
- Fallos: 0.
- Errores: 0.
- Omitidas: 0.
- Clases analizadas por JaCoCo: 89.

## Cobertura resumida

| Metrica | Cubierto | No cubierto | Cobertura |
|---|---:|---:|---:|
| Instrucciones | 1107 | 4297 | 20.48% |
| Ramas | 23 | 139 | 14.20% |
| Lineas | 178 | 679 | 20.77% |
| Metodos | 135 | 502 | 21.19% |
| Complejidad | 142 | 576 | 19.78% |

## Artefactos

- Log: `core-api-jacoco.log`
- CSV/XML/HTML: los artefactos `core-api/jacoco.csv`, `core-api/jacoco.xml` y `core-api/index.html` fueron actualizados posteriormente con la corrida final.
- Resumen final: `core-api-jacoco-summary-after-improvement.md`

## Limitacion

La cobertura refleja solo las pruebas automatizadas existentes ejecutadas en esta campana complementaria. No mide pruebas manuales, carga, DRP, mobile ni flujos Expo.
