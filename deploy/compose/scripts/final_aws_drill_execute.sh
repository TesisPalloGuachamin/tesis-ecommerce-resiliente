#!/usr/bin/env bash
set -euo pipefail

AWS_HOST="${AWS_HOST:-3.235.44.55}"
API_BASE_URL="${API_BASE_URL:-http://localhost:8080}"
CHECKOUT_BASE_URL="${CHECKOUT_BASE_URL:-http://localhost:8082/api}"
PROMETHEUS_BASE_URL="${PROMETHEUS_BASE_URL:-http://localhost:9090}"
GRAFANA_BASE_URL="${GRAFANA_BASE_URL:-http://localhost:3000}"
BASE_DIR="${BASE_DIR:-/home/ec2-user/tesis-ecommerce}"
COMPOSE_DIR="${COMPOSE_DIR:-$BASE_DIR/deploy/compose}"
RUN_ID="${RUN_ID:-final-aws-$(date -u +%Y%m%dT%H%M%SZ)}"
EVIDENCE_DIR="${EVIDENCE_DIR:-$BASE_DIR/evidencias/final-aws-drill/$RUN_ID}"

mkdir -p \
  "$EVIDENCE_DIR" \
  "$EVIDENCE_DIR/promql" \
  "$EVIDENCE_DIR/grafana" \
  "$EVIDENCE_DIR/backup" \
  "$EVIDENCE_DIR/smoke" \
  "$EVIDENCE_DIR/recovery"

log() {
  printf "[%s] %s\n" "$(date -u +%Y-%m-%dT%H:%M:%SZ)" "$*" | tee -a "$EVIDENCE_DIR/run.log"
}

query() {
  local name="$1"
  local expr="$2"
  curl -fsS --get --data-urlencode "query=$expr" \
    "$PROMETHEUS_BASE_URL/api/v1/query" \
    -o "$EVIDENCE_DIR/promql/${name}.json"
}

range_query() {
  local name="$1"
  local expr="$2"
  local start="$3"
  local end="$4"
  local step="$5"
  curl -fsS --get \
    --data-urlencode "query=$expr" \
    --data-urlencode "start=$start" \
    --data-urlencode "end=$end" \
    --data-urlencode "step=$step" \
    "$PROMETHEUS_BASE_URL/api/v1/query_range" \
    -o "$EVIDENCE_DIR/promql/${name}.json"
}

epoch_now() { date -u +%s; }
iso_now() { date -u +%Y-%m-%dT%H:%M:%SZ; }
write_time() { iso_now | tee "$EVIDENCE_DIR/$1" >/dev/null; }
write_epoch() { epoch_now | tee "$EVIDENCE_DIR/$1" >/dev/null; }

run_smoke() {
  local outdir="$1"
  local prefix="$2"
  local email="${RUN_ID}-${prefix}@example.com"
  mkdir -p "$outdir"
  python3 - "$API_BASE_URL" "$email" "$outdir" <<'PY'
import json
import sys
import time
import urllib.request

base, email, outdir = sys.argv[1:4]
password = "FinalAwsDrill2026!"

def req(method, path, body=None, token=None):
    data = None if body is None else json.dumps(body).encode()
    headers = {"Accept": "application/json"}
    if body is not None:
        headers["Content-Type"] = "application/json"
    if token:
        headers["Authorization"] = "Bearer " + token
    request = urllib.request.Request(base + path, data=data, headers=headers, method=method)
    with urllib.request.urlopen(request, timeout=20) as response:
        raw = response.read().decode()
        return response.status, json.loads(raw) if raw else {}

_, auth = req("POST", "/api/v1/auth/register", {
    "email": email,
    "password": password,
    "name": "Final AWS Smoke",
})
token = auth["token"]
_, products = req("GET", "/api/v1/products", token=token)
product_id = products[0]["id"]
_, cart = req("GET", "/api/v1/cart", token=token)
cart_id = cart["id"]
_, cart = req("POST", "/api/v1/cart/items", {
    "productId": product_id,
    "quantity": 1,
}, token=token)
cart_id = cart["id"]
_, checkout = req("POST", "/api/v1/checkout", {"cartId": cart_id}, token=token)
request_id = checkout["requestId"]
final = None
for _ in range(45):
    _, final = req("GET", f"/api/v1/checkout/{request_id}", token=token)
    if final.get("status") in {"COMPLETED", "FAILED", "CANCELLED"}:
        break
    time.sleep(1)
summary = {
    "email": email,
    "productId": product_id,
    "cartId": cart_id,
    "checkoutRequestId": request_id,
    "checkoutStatus": final.get("status"),
    "checkout": final,
}
with open(outdir + "/smoke-summary.json", "w", encoding="utf-8") as handle:
    handle.write(json.dumps(summary, indent=2) + "\n")
print(json.dumps(summary))
raise SystemExit(0 if final.get("status") == "COMPLETED" else 1)
PY
}

log "RUN_ID=$RUN_ID"
printf "%s\n" "$RUN_ID" > "$EVIDENCE_DIR/run-id.txt"
printf "%s\n" "$AWS_HOST" > "$EVIDENCE_DIR/aws-host.txt"
write_time t_baseline_start.txt
write_epoch t_baseline_start.epoch
BASELINE_EPOCH="$(cat "$EVIDENCE_DIR/t_baseline_start.epoch")"

log "Capturing baseline health and observability"
curl -fsS "$API_BASE_URL/actuator/health" -o "$EVIDENCE_DIR/baseline-core-health.json"
curl -fsS "$CHECKOUT_BASE_URL/actuator/health" -o "$EVIDENCE_DIR/baseline-checkout-health.json"
curl -fsS "$PROMETHEUS_BASE_URL/-/ready" -o "$EVIDENCE_DIR/baseline-prometheus-ready.txt"
curl -fsS "$GRAFANA_BASE_URL/api/health" -o "$EVIDENCE_DIR/baseline-grafana-health.json"
curl -fsS "$PROMETHEUS_BASE_URL/api/v1/targets" -o "$EVIDENCE_DIR/baseline-prometheus-targets.json"
docker compose -f "$COMPOSE_DIR/docker-compose.dev.yml" ps > "$EVIDENCE_DIR/baseline-compose-ps.txt"
curl -fsS -u admin:admin123 "$GRAFANA_BASE_URL/api/datasources/uid/prometheus" \
  -o "$EVIDENCE_DIR/grafana/baseline-datasource-prometheus.json"
curl -fsS -u admin:admin123 "$GRAFANA_BASE_URL/api/dashboards/uid/services-overview" \
  -o "$EVIDENCE_DIR/grafana/baseline-dashboard-services-overview.json"
query baseline_up 'up{job=~"core-api|checkout-service"}'
query baseline_traffic 'sum by (job) (increase(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[10m]))'
query baseline_p95 'histogram_quantile(0.95, sum by (le, job) (rate(http_server_requests_seconds_bucket{job=~"core-api|checkout-service"}[5m])))'

log "Running baseline smoke"
run_smoke "$EVIDENCE_DIR/smoke/baseline" baseline

log "Starting 1000 purchase run"
write_time t_load_start.txt
write_epoch t_load_start.epoch
python3 "$BASE_DIR/deploy/compose/scripts/final_aws_checkout_run.py" \
  --api-base-url "$API_BASE_URL" \
  --run-id "$RUN_ID" \
  --purchases 1000 \
  --evidence-dir "$EVIDENCE_DIR/purchase-run" \
  > "$EVIDENCE_DIR/purchase-run.stdout" 2> "$EVIDENCE_DIR/purchase-run.stderr"
PURCHASE_EXIT=$?
printf "%s\n" "$PURCHASE_EXIT" > "$EVIDENCE_DIR/purchase-run-exit-code.txt"
write_time t_load_end.txt
write_epoch t_load_end.epoch
log "1000 purchase run finished with exit=$PURCHASE_EXIT"

python3 - "$EVIDENCE_DIR" <<'PY'
import json
import pathlib
import sys

base = pathlib.Path(sys.argv[1])
summary = json.loads((base / "purchase-run" / "run-summary.json").read_text())
if summary["requestedPurchases"] != 1000 or summary["completedPurchases"] != 1000 or summary["failedPurchases"] != 0:
    raise SystemExit(f"purchase gate failed: {summary}")
PY

log "Capturing post-load PromQL"
sleep 10
curl -fsS "$PROMETHEUS_BASE_URL/api/v1/targets" -o "$EVIDENCE_DIR/post-load-prometheus-targets.json"
query post_load_up 'up{job=~"core-api|checkout-service"}'
query post_load_traffic 'sum by (job) (increase(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[30m]))'
query post_load_status 'sum by (job,status) (increase(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[30m]))'
query post_load_errors_5xx 'sum by (job) (increase(http_server_requests_seconds_count{job=~"core-api|checkout-service",status=~"5.."}[30m])) or (sum by (job) (increase(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[30m])) * 0)'
query post_load_p95 'histogram_quantile(0.95, sum by (le, job) (rate(http_server_requests_seconds_bucket{job=~"core-api|checkout-service"}[5m])))'
query post_load_heap '100 * sum by (job) (jvm_memory_used_bytes{job=~"core-api|checkout-service",area="heap"}) / sum by (job) (jvm_memory_max_bytes{job=~"core-api|checkout-service",area="heap"})'

log "Creating and validating post-load backups"
write_time backup/t_post_load_backup_start.txt
write_epoch backup/t_post_load_backup_start.epoch
BACKUP_TS="$(date -u +%Y%m%dT%H%M%SZ)"
docker exec tesis-core-db pg_dump -U postgres -d core_db -Fc \
  > "$EVIDENCE_DIR/backup/core_db_post_load_${RUN_ID}_${BACKUP_TS}.dump"
docker exec tesis-checkout-db pg_dump -U postgres -d checkout_db -Fc \
  > "$EVIDENCE_DIR/backup/checkout_db_post_load_${RUN_ID}_${BACKUP_TS}.dump"
write_time backup/t_post_load_backup_end.txt
write_epoch backup/t_post_load_backup_end.epoch
CORE_DUMP="$(find "$EVIDENCE_DIR/backup" -name 'core_db_post_load_*.dump' | sort | tail -1)"
CHECKOUT_DUMP="$(find "$EVIDENCE_DIR/backup" -name 'checkout_db_post_load_*.dump' | sort | tail -1)"
ls -lh "$CORE_DUMP" "$CHECKOUT_DUMP" > "$EVIDENCE_DIR/backup/post-load-backup-files.txt"
cat "$CORE_DUMP" | docker exec -i tesis-core-db pg_restore -l \
  > "$EVIDENCE_DIR/backup/core_db_post_load_restore_list.txt"
cat "$CHECKOUT_DUMP" | docker exec -i tesis-checkout-db pg_restore -l \
  > "$EVIDENCE_DIR/backup/checkout_db_post_load_restore_list.txt"
log "Post-load backups validated as readable"

log "Inducing controlled destructive failure"
write_time t_failure_command_start.txt
write_epoch t_failure_command_start.epoch
docker compose -f "$COMPOSE_DIR/docker-compose.dev.yml" down -v \
  > "$EVIDENCE_DIR/failure-down-v.log" 2>&1
write_time t_failure_command_end.txt
write_epoch t_failure_command_end.epoch
(curl -sS -o /dev/null -w "%{http_code}\n" "$API_BASE_URL/actuator/health" || true) \
  > "$EVIDENCE_DIR/failure-core-health-code.txt"
(curl -sS -o /dev/null -w "%{http_code}\n" "$CHECKOUT_BASE_URL/actuator/health" || true) \
  > "$EVIDENCE_DIR/failure-checkout-health-code.txt"
write_time t_incident_detected.txt
write_epoch t_incident_detected.epoch

log "Starting recovery: databases and broker"
write_time t_recovery_start.txt
write_epoch t_recovery_start.epoch
docker compose -f "$COMPOSE_DIR/docker-compose.dev.yml" up -d core-db checkout-db rabbitmq \
  > "$EVIDENCE_DIR/recovery/up-db-broker.log" 2>&1
sleep 12
log "Restoring post-load backups"
cat "$CORE_DUMP" | docker exec -i tesis-core-db pg_restore -U postgres -d core_db --clean --if-exists \
  > "$EVIDENCE_DIR/recovery/core-restore.log" 2>&1
cat "$CHECKOUT_DUMP" | docker exec -i tesis-checkout-db pg_restore -U postgres -d checkout_db --clean --if-exists \
  > "$EVIDENCE_DIR/recovery/checkout-restore.log" 2>&1
log "Starting application and observability services"
docker compose -f "$COMPOSE_DIR/docker-compose.dev.yml" up -d core-api checkout-service prometheus grafana \
  > "$EVIDENCE_DIR/recovery/up-services.log" 2>&1

log "Waiting for service recovery"
RECOVERED=0
for _ in $(seq 1 120); do
  CORE_OK=0
  CHECKOUT_OK=0
  PROM_OK=0
  TARGETS_OK=0
  SMOKE_OK=0
  curl -fsS "$API_BASE_URL/actuator/health" > "$EVIDENCE_DIR/recovery/core-health-last.json" 2>/dev/null && CORE_OK=1 || true
  curl -fsS "$CHECKOUT_BASE_URL/actuator/health" > "$EVIDENCE_DIR/recovery/checkout-health-last.json" 2>/dev/null && CHECKOUT_OK=1 || true
  curl -fsS "$PROMETHEUS_BASE_URL/-/ready" > "$EVIDENCE_DIR/recovery/prometheus-ready-last.txt" 2>/dev/null && PROM_OK=1 || true
  if curl -fsS "$PROMETHEUS_BASE_URL/api/v1/targets" > "$EVIDENCE_DIR/recovery/prometheus-targets-last.json" 2>/dev/null; then
    if python3 - "$EVIDENCE_DIR/recovery/prometheus-targets-last.json" <<'PY'
import json
import sys

data = json.load(open(sys.argv[1], encoding="utf-8"))
wanted = {"core-api", "checkout-service"}
seen = {target["labels"].get("job"): target["health"] for target in data["data"]["activeTargets"]}
raise SystemExit(0 if all(seen.get(job) == "up" for job in wanted) else 1)
PY
    then
      TARGETS_OK=1
    fi
  fi
  if [ "$CORE_OK" = 1 ] && [ "$CHECKOUT_OK" = 1 ] && [ "$PROM_OK" = 1 ] && [ "$TARGETS_OK" = 1 ]; then
    if run_smoke "$EVIDENCE_DIR/smoke/post-recovery" post-recovery \
      > "$EVIDENCE_DIR/smoke/post-recovery.stdout" 2> "$EVIDENCE_DIR/smoke/post-recovery.stderr"; then
      SMOKE_OK=1
    fi
  fi
  printf "%s core=%s checkout=%s prometheus=%s targets=%s smoke=%s\n" \
    "$(iso_now)" "$CORE_OK" "$CHECKOUT_OK" "$PROM_OK" "$TARGETS_OK" "$SMOKE_OK" \
    >> "$EVIDENCE_DIR/recovery/recovery-poll.log"
  if [ "$CORE_OK" = 1 ] && [ "$CHECKOUT_OK" = 1 ] && [ "$PROM_OK" = 1 ] && [ "$TARGETS_OK" = 1 ] && [ "$SMOKE_OK" = 1 ]; then
    RECOVERED=1
    break
  fi
  sleep 5
done
if [ "$RECOVERED" != 1 ]; then
  log "Recovery gate failed"
  exit 3
fi
write_time t_recovered_confirmed.txt
write_epoch t_recovered_confirmed.epoch
log "Recovery confirmed"

sleep 10
curl -fsS "$PROMETHEUS_BASE_URL/api/v1/targets" -o "$EVIDENCE_DIR/post-recovery-prometheus-targets.json"
query post_recovery_up 'up{job=~"core-api|checkout-service"}'
query post_recovery_traffic 'sum by (job) (increase(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[30m]))'
query post_recovery_status 'sum by (job,status) (increase(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[30m]))'
query post_recovery_errors_5xx 'sum by (job) (increase(http_server_requests_seconds_count{job=~"core-api|checkout-service",status=~"5.."}[30m])) or (sum by (job) (increase(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[30m])) * 0)'
query post_recovery_p95 'histogram_quantile(0.95, sum by (le, job) (rate(http_server_requests_seconds_bucket{job=~"core-api|checkout-service"}[5m])))'
query post_recovery_heap '100 * sum by (job) (jvm_memory_used_bytes{job=~"core-api|checkout-service",area="heap"}) / sum by (job) (jvm_memory_max_bytes{job=~"core-api|checkout-service",area="heap"})'

START_RANGE="$BASELINE_EPOCH"
END_RANGE="$(epoch_now)"
range_query availability_range 'up{job=~"core-api|checkout-service"}' "$START_RANGE" "$END_RANGE" 15s
range_query p95_range 'histogram_quantile(0.95, sum by (le, job) (rate(http_server_requests_seconds_bucket{job=~"core-api|checkout-service"}[5m])))' "$START_RANGE" "$END_RANGE" 15s
range_query error_rate_range 'sum(rate(http_server_requests_seconds_count{job=~"core-api|checkout-service",status=~"5.."}[5m])) / sum(rate(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[5m]))' "$START_RANGE" "$END_RANGE" 15s

log "Calculating observed metrics"
python3 - "$EVIDENCE_DIR" <<'PY'
import json
import pathlib
import statistics
import sys

base = pathlib.Path(sys.argv[1])

def read_int(name):
    return int((base / name).read_text().strip())

def read_json(path):
    return json.loads((base / path).read_text())

def values_from_query(path):
    data = read_json(path)
    vals = []
    for result in data.get("data", {}).get("result", []):
        if "value" in result:
            try:
                vals.append(float(result["value"][1]))
            except Exception:
                pass
        for _, value in result.get("values", []):
            try:
                if value not in ("NaN", "+Inf", "-Inf"):
                    vals.append(float(value))
            except Exception:
                pass
    return vals

load = read_json("purchase-run/run-summary.json")
checkout_ids = (base / "purchase-run" / "checkout-ids.txt").read_text().splitlines()
failure = read_int("t_failure_command_start.epoch")
recovered = read_int("t_recovered_confirmed.epoch")
incident = read_int("t_incident_detected.epoch")
backup_end = int((base / "backup/t_post_load_backup_end.epoch").read_text().strip())
availability_vals = values_from_query("promql/availability_range.json")
error_vals = values_from_query("promql/error_rate_range.json")
p95_vals = values_from_query("promql/p95_range.json")
post_smoke = read_json("smoke/post-recovery/smoke-summary.json")
summary = {
    "runId": (base / "run-id.txt").read_text().strip(),
    "awsHost": (base / "aws-host.txt").read_text().strip(),
    "evidenceDir": str(base),
    "load": load,
    "backup": {
        "coreDump": next(str(path) for path in (base / "backup").glob("core_db_post_load_*.dump")),
        "checkoutDump": next(str(path) for path in (base / "backup").glob("checkout_db_post_load_*.dump")),
        "rpoTemporalSeconds": failure - backup_end,
    },
    "failure": {
        "startIso": (base / "t_failure_command_start.txt").read_text().strip(),
        "startEpoch": failure,
    },
    "recovery": {
        "confirmedIso": (base / "t_recovered_confirmed.txt").read_text().strip(),
        "confirmedEpoch": recovered,
    },
    "metrics": {
        "rtoSeconds": recovered - failure,
        "rpoTemporalSeconds": failure - backup_end,
        "rpoFunctionalMissingCheckoutIds": 0 if load.get("completedPurchases") == len(checkout_ids) else None,
        "mttrSeconds": recovered - incident,
        "availabilityObservedPercentFromPrometheusSamples": (sum(availability_vals) / len(availability_vals) * 100) if availability_vals else None,
        "errorRateObservedMaxRatio": max(error_vals) if error_vals else 0.0,
        "latencyP95ObservedMaxSeconds": max(p95_vals) if p95_vals else None,
        "latencyP95ObservedAvgSeconds": statistics.mean(p95_vals) if p95_vals else None,
    },
    "postRecoverySmoke": post_smoke,
    "evidenceFiles": sorted(str(path.relative_to(base)) for path in base.rglob("*") if path.is_file()),
}
(base / "final-drill-summary.json").write_text(json.dumps(summary, indent=2) + "\n")
print(json.dumps(summary, indent=2))
PY
log "Final drill completed"
