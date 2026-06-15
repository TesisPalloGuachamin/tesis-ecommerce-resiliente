# Reporte de trazabilidad Capitulo 4

## 1. Proposito

Este bloque consolida la trazabilidad del Capitulo 4 para evitar que las evidencias queden como anexos aislados. La cadena documental conecta metodologia, componente implementado, evidencia, resultado observado, metrica, objetivo especifico y commit de respaldo.

No se ejecutaron nuevas pruebas, no se modifico codigo y no se alteraron las evidencias tecnicas previamente cerradas.

## 2. Conexion con el Capitulo 4

Las tablas creadas permiten insertar o referenciar resultados en las secciones de validacion funcional, pruebas backend, rendimiento, observabilidad, integracion mobile-backend y DRP. El objetivo es que cada afirmacion tecnica del capitulo tenga una evidencia asociada y una limitacion explicita.

Archivos creados:

- `tabla_maestra_trazabilidad_capitulo4.md`
- `tabla_resumen_evidencias_capitulo4.md`
- `tabla_metricas_consolidadas_capitulo4.md`
- `tabla_metodologia_evidencia.md`
- `indice_commits_capitulo4.md`

## 3. Evidencias operativas

Se consideran evidencias operativas aquellas que contienen ejecucion o validacion tecnica observada:

- Compra hasta `COMPLETED`.
- Venta/listing hasta `ACTIVE`.
- RabbitMQ publicacion/consumo/colas/logs/DB.
- Pruebas backend Maven/Surefire.
- JaCoCo backend.
- CARGA-200, CARGA-500 y CARGA-1000.
- Observabilidad Prometheus/Grafana.
- Mobile-backend.
- DRP AWS principal.
- AWS alterna y Azure, con limitaciones indicadas.

## 4. Evidencias complementarias

Se consideran complementarias las evidencias que refuerzan la interpretacion pero no reemplazan los cierres principales:

- JaCoCo como refuerzo academico de pruebas backend.
- CARGA-1000 como estabilidad funcional posterior, no como DRP.
- Capturas de observabilidad como apoyo visual, no como criterio primario.
- Referencias de commit para trazabilidad documental.

## 5. Escenarios documentales

Google Cloud y on-premise se mantienen como escenarios documentales. No se reportan como pruebas ejecutadas, no tienen RTO/RPO/MTTR operativo y no deben usarse para comparar rendimiento o disponibilidad observada.

## 6. Que no se debe afirmar

Las evidencias del Capitulo 4 no soportan afirmar:

- SLA productivo.
- Operacion active-active.
- Alta disponibilidad empresarial.
- Failover automatico movil.
- Pagos reales o transacciones financieras reales.
- Integracion real con pasarela de pago externa.
- GCP/on-premise como escenarios operativos observados.

## 7. Resultado

Estado del bloque de tablas finales y trazabilidad: `CERRADO`.

El cierre es documental: organiza evidencias ya generadas y versionadas, consolida metricas y mantiene las restricciones academicas del alcance.
