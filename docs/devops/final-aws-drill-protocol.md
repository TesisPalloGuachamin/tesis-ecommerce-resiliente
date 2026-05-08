# Protocolo final AWS - ensayo tecnico controlado

Este protocolo prepara un unico ensayo fuerte en la region AWS actual. No abre
Azure, no usa otra region AWS y no modifica contratos backend.

## Alcance

- Linea base operativa con health, Prometheus y Grafana.
- Generacion trazable de 1000 compras reales de sandbox contra `core-api`.
- Caida inducida controlada sobre el stack Docker Compose actual.
- Recuperacion en la misma EC2/region con backup y restauracion PostgreSQL.
- Medicion de RTO, RPO, MTTR, latencia, errores y disponibilidad observada.

## Variables de corrida

```bash
export AWS_HOST="3.235.44.55"
export API_BASE_URL="http://${AWS_HOST}:8080"
export CHECKOUT_BASE_URL="http://${AWS_HOST}:8082/api"
export PROMETHEUS_BASE_URL="http://${AWS_HOST}:9090"
export GRAFANA_BASE_URL="http://${AWS_HOST}:3000"
export RUN_ID="final-aws-$(date -u +%Y%m%dT%H%M%SZ)"
export EVIDENCE_DIR="/home/ec2-user/tesis-ecommerce/evidencias/final-aws-drill/${RUN_ID}"
export COMPOSE_DIR="/home/ec2-user/tesis-ecommerce/deploy/compose"
```

Actualizar `AWS_HOST` si AWS Academy reinicia el laboratorio.

## Fase 0 - precondiciones

1. Confirmar rama y commit desplegado.
2. Confirmar containers arriba.
3. Confirmar health:
   - `GET ${API_BASE_URL}/actuator/health`
   - `GET ${CHECKOUT_BASE_URL}/actuator/health`
4. Confirmar targets Prometheus `UP`.
5. Confirmar dashboard Grafana cargado desde provisioning.
6. Ejecutar un smoke de checkout y exigir estado `COMPLETED`.

## Fase 1 - linea base

Crear carpeta de evidencias y capturar:

```bash
mkdir -p "${EVIDENCE_DIR}"
date -u +"%Y-%m-%dT%H:%M:%SZ" | tee "${EVIDENCE_DIR}/t_baseline_start.txt"
curl -fsS "${API_BASE_URL}/actuator/health" -o "${EVIDENCE_DIR}/baseline-core-health.json"
curl -fsS "${CHECKOUT_BASE_URL}/actuator/health" -o "${EVIDENCE_DIR}/baseline-checkout-health.json"
curl -fsS "${PROMETHEUS_BASE_URL}/api/v1/targets" -o "${EVIDENCE_DIR}/baseline-prometheus-targets.json"
docker compose -f "${COMPOSE_DIR}/docker-compose.dev.yml" ps > "${EVIDENCE_DIR}/baseline-compose-ps.txt"
```

## Fase 2 - backup previo defendible

El backup que defiende RPO debe generarse antes de la caida destructiva y despues
de la carga de 1000 compras si se quiere demostrar perdida cero de esa corrida.

```bash
date -u +"%Y-%m-%dT%H:%M:%SZ" | tee "${EVIDENCE_DIR}/t_backup_start.txt"
docker exec tesis-core-db pg_dump -U postgres -d core_db -Fc > "${EVIDENCE_DIR}/core_db_${RUN_ID}.dump"
docker exec tesis-checkout-db pg_dump -U postgres -d checkout_db -Fc > "${EVIDENCE_DIR}/checkout_db_${RUN_ID}.dump"
date -u +"%Y-%m-%dT%H:%M:%SZ" | tee "${EVIDENCE_DIR}/t_backup_end.txt"
ls -lh "${EVIDENCE_DIR}"/*.dump > "${EVIDENCE_DIR}/backup-files.txt"
```

## Fase 3 - 1000 compras

Usar el script versionado:

```bash
python3 /home/ec2-user/tesis-ecommerce/deploy/compose/scripts/final_aws_checkout_run.py \
  --api-base-url "${API_BASE_URL}" \
  --run-id "${RUN_ID}" \
  --purchases 1000 \
  --evidence-dir "${EVIDENCE_DIR}/purchase-run"
```

Evidencias generadas:

- `purchase-events.jsonl`
- `run-summary.json`
- `checkout-ids.txt`

## Fase 4 - backup posterior a las 1000 compras

Este backup es el candidato principal para restauracion en el ensayo final.

```bash
date -u +"%Y-%m-%dT%H:%M:%SZ" | tee "${EVIDENCE_DIR}/t_post_load_backup_start.txt"
docker exec tesis-core-db pg_dump -U postgres -d core_db -Fc > "${EVIDENCE_DIR}/core_db_post_load_${RUN_ID}.dump"
docker exec tesis-checkout-db pg_dump -U postgres -d checkout_db -Fc > "${EVIDENCE_DIR}/checkout_db_post_load_${RUN_ID}.dump"
date -u +"%Y-%m-%dT%H:%M:%SZ" | tee "${EVIDENCE_DIR}/t_post_load_backup_end.txt"
```

## Fase 5 - caida inducida

El punto exacto de caida es el timestamp inmediatamente anterior al comando
destructivo.

Advertencia metodologica: `down -v` elimina tambien el volumen local de
Prometheus si se usa el compose completo. Por tanto, antes de ejecutar la caida
se deben capturar las consultas PromQL post-carga como evidencia puntual. La
disponibilidad de la ventana completa debe calcularse con timestamps externos
del ensayo, salvo que Prometheus se preserve fuera del stack afectado.

```bash
date -u +"%Y-%m-%dT%H:%M:%SZ" | tee "${EVIDENCE_DIR}/t_failure_command_start.txt"
docker compose -f "${COMPOSE_DIR}/docker-compose.dev.yml" down -v
date -u +"%Y-%m-%dT%H:%M:%SZ" | tee "${EVIDENCE_DIR}/t_failure_command_end.txt"
```

Confirmar indisponibilidad:

```bash
curl -sS -o /dev/null -w "%{http_code}\n" "${API_BASE_URL}/actuator/health" \
  | tee "${EVIDENCE_DIR}/failure-core-health-code.txt" || true
curl -sS -o /dev/null -w "%{http_code}\n" "${CHECKOUT_BASE_URL}/actuator/health" \
  | tee "${EVIDENCE_DIR}/failure-checkout-health-code.txt" || true
```

## Fase 6 - recuperacion y restauracion

```bash
date -u +"%Y-%m-%dT%H:%M:%SZ" | tee "${EVIDENCE_DIR}/t_recovery_start.txt"
docker compose -f "${COMPOSE_DIR}/docker-compose.dev.yml" up -d core-db checkout-db rabbitmq
docker compose -f "${COMPOSE_DIR}/docker-compose.dev.yml" up -d core-api checkout-service prometheus grafana
```

Restaurar bases si la caida destruyo volumenes:

```bash
cat "${EVIDENCE_DIR}/core_db_post_load_${RUN_ID}.dump" \
  | docker exec -i tesis-core-db pg_restore -U postgres -d core_db --clean --if-exists
cat "${EVIDENCE_DIR}/checkout_db_post_load_${RUN_ID}.dump" \
  | docker exec -i tesis-checkout-db pg_restore -U postgres -d checkout_db --clean --if-exists
```

Esperar hasta que ambos servicios esten sanos, Prometheus tenga targets `UP` y
un smoke de checkout nuevo termine en `COMPLETED`.

El punto exacto de servicio recuperado es el timestamp posterior a la primera
verificacion que cumple simultaneamente:

- `core-api` health `UP`.
- `checkout-service` health `UP`.
- Prometheus target `core-api` `UP`.
- Prometheus target `checkout-service` `UP`.
- Smoke funcional post-recovery con checkout `COMPLETED`.

```bash
date -u +"%Y-%m-%dT%H:%M:%SZ" | tee "${EVIDENCE_DIR}/t_recovered_confirmed.txt"
```

## PromQL por fase

Linea base, durante carga, caida y recuperacion:

```promql
up{job=~"core-api|checkout-service"}
```

Request rate:

```promql
sum by (job) (rate(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[5m]))
```

Errores 5xx:

```promql
sum by (job) (rate(http_server_requests_seconds_count{job=~"core-api|checkout-service",status=~"5.."}[5m]))
```

Tasa de error:

```promql
sum(rate(http_server_requests_seconds_count{job=~"core-api|checkout-service",status=~"5.."}[5m]))
/
sum(rate(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[5m]))
```

Latencia p95:

```promql
histogram_quantile(0.95, sum by (le, job) (rate(http_server_requests_seconds_bucket{job=~"core-api|checkout-service"}[5m])))
```

Disponibilidad observada por ventana Prometheus:

```promql
avg_over_time(up{job=~"core-api|checkout-service"}[30m]) * 100
```

Uptime:

```promql
process_uptime_seconds{job=~"core-api|checkout-service"}
```

CPU:

```promql
process_cpu_usage{job=~"core-api|checkout-service"}
```

JVM heap:

```promql
100 * sum by (job) (jvm_memory_used_bytes{job=~"core-api|checkout-service",area="heap"}) / sum by (job) (jvm_memory_max_bytes{job=~"core-api|checkout-service",area="heap"})
```

## Calculos

- RTO: `t_recovered_confirmed - t_failure_command_start`.
- RPO temporal: `t_failure_command_start - t_post_load_backup_end`.
- RPO funcional: cantidad de `checkoutRequestId` de `checkout-ids.txt` que no
  aparecen como `COMPLETED` tras restaurar.
- MTTR: `t_recovered_confirmed - t_incident_detected`, donde
  `t_incident_detected` es el primer health fallido o target DOWN registrado
  despues de la caida.
- Disponibilidad observada con Prometheus continuo: porcentaje de muestras
  `up == 1` en la ventana real del ensayo para `core-api` y `checkout-service`.
- Disponibilidad observada con `down -v` sobre todo el stack: usar timestamps
  externos del ensayo:
  `(t_recovered_confirmed - t_baseline_start - RTO) / (t_recovered_confirmed - t_baseline_start) * 100`.
  En este caso, las series Prometheus se reportan como snapshots post-carga y
  post-recovery, no como serie historica continua.
- Tasa de error: requests 5xx / requests totales en la ventana del ensayo.
- Latencia p95: valor maximo o promedio observado del PromQL p95 durante carga,
  segun se declare antes de ejecutar.

## Nota metodologica de Prometheus

Si la caida incluye `docker compose down -v`, Prometheus deja de ser una fuente
continua de disponibilidad porque su TSDB local se elimina junto con los demas
volumenes. Esto no invalida la corrida si existen:

- timestamps externos para linea base, caida, deteccion, recuperacion y
  confirmacion;
- snapshots PromQL capturados antes de la caida y despues de la recuperacion;
- dumps post-carga validados antes de destruir volumenes;
- smoke funcional post-recovery en `COMPLETED`.

En la tesis, las metricas deben clasificarse asi:

- RTO, RPO temporal, MTTR y smoke post-recovery: observados por timestamps y
  evidencia funcional.
- Disponibilidad: observada con limitacion, calculada por timestamps externos
  cuando Prometheus no conserva continuidad historica.
- Latencia y tasa de error: observadas por snapshots PromQL guardados antes de
  la caida y tras la recuperacion.

## Riesgos antes de ejecutar

- `docker compose down -v` es destructivo para volumenes; solo usar despues de
  confirmar backups legibles.
- AWS Academy puede cambiar IP al reiniciar; fijar `AWS_HOST` al inicio.
- Las 1000 compras crean usuarios/carritos/checkouts reales de sandbox; no correr
  sobre datos que se quieran conservar sin backup.
- Si Prometheus cae con el stack, la medicion de disponibilidad debe apoyarse en
  timestamps externos y evidencias capturadas antes/despues, no solo en series
  internas durante la caida.
