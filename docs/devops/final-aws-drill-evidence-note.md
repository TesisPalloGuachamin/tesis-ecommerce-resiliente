# Nota tecnica - evidencia del ensayo final AWS

## Contexto observado

El ensayo final AWS `final-aws-20260508T055011Z` completo 1000 compras de
sandbox, genero backups post-carga, indujo una caida controlada con
`docker compose down -v`, restauro los datos y confirmo recuperacion con smoke
post-recovery en estado `COMPLETED`.

Durante la caida, Prometheus tambien fue detenido y su volumen local fue
eliminado por el uso de `down -v`. Esto impidio conservar una serie historica
continua dentro de Prometheus para toda la ventana del ensayo.

## Por que no invalida la corrida

La prueba no dependia exclusivamente de Prometheus para demostrar recuperacion.
Las metricas principales de resiliencia quedaron respaldadas por fuentes
independientes:

- timestamps externos del ensayo para linea base, caida, deteccion y
  recuperacion;
- archivos de evidencia generados antes, durante y despues de la caida;
- backups `pg_dump` post-carga validados con `pg_restore -l`;
- checkout IDs trazables de la corrida de carga;
- smoke funcional post-recovery con checkout `COMPLETED`;
- snapshots PromQL guardados antes de destruir el stack y luego de recuperar.

## Clasificacion metodologica

| Elemento | Clasificacion | Fuente |
| --- | --- | --- |
| 1000 compras completadas | Observado | `purchase-run/run-summary.json`, `purchase-events.jsonl` |
| Backup post-carga legible | Observado | dumps y listas `pg_restore -l` |
| Caida inducida | Observado | `t_failure_command_start.txt`, `failure-down-v.log` |
| RTO | Observado | `t_failure_command_start.txt`, `t_recovered_confirmed.txt` |
| RPO temporal | Observado | `t_post_load_backup_end.txt`, `t_failure_command_start.txt` |
| RPO funcional | Observado | checkout IDs completados y backup post-carga restaurado |
| MTTR | Observado | `t_incident_detected.txt`, `t_recovered_confirmed.txt` |
| Disponibilidad | Observado con limitacion | timestamps externos; Prometheus no fue continuo |
| Latencia p95 | Observado con limitacion | snapshots PromQL post-carga y post-recovery |
| Tasa de error | Observado con limitacion | snapshots PromQL guardados |
| Prometheus continuo durante caida | Disenado/documental | requiere preservar Prometheus fuera del stack afectado |

## Recomendacion para redaccion

No afirmar que Prometheus conservo una serie continua durante toda la caida. La
redaccion correcta es indicar que Prometheus y Grafana observaron el flujo antes
y despues de la falla, mientras que la disponibilidad total del ensayo se
calculo con timestamps externos debido a que la estrategia destructiva elimino
el volumen local de Prometheus.

## Correccion operativa para repeticion

Para una repeticion completa del ensayo, Prometheus y Grafana deben ejecutarse
desde `deploy/compose/docker-compose.observability.yml`, mientras que la caida
destructiva debe aplicarse solo al stack funcional definido en
`deploy/compose/docker-compose.functional.yml`.

El comando destructivo corregido es:

```bash
docker compose -f deploy/compose/docker-compose.functional.yml down -v
```

Con esta separacion, el volumen `prometheus_data` ya no pertenece al compose que
se destruye durante la falla funcional. Prometheus puede seguir registrando
targets `DOWN` durante la caida y targets `UP` durante la recuperacion.
