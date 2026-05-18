#!/usr/bin/env bash
set -euo pipefail

BASE="${BASE:-/home/ec2-user/tesis-ecommerce}"
COMPOSE_DIR="${COMPOSE_DIR:-$BASE/deploy/compose}"
FUNCTIONAL="${FUNCTIONAL:-$COMPOSE_DIR/docker-compose.functional.yml}"
RUN_ID="${RUN_ID:-final-aws-20260518T222928Z}"
RUN_DIR="${RUN_DIR:-$BASE/evidencias/final-aws-drill/$RUN_ID}"
API="${API:-http://localhost:8080}"
CHECKOUT="${CHECKOUT:-http://localhost:8082/api}"
PROM="${PROM:-http://localhost:9090}"
GRAF="${GRAF:-http://localhost:3000}"

mkdir -p "$RUN_DIR/recovery" "$RUN_DIR/smoke/post-recovery" "$RUN_DIR/promql"

log() {
  printf "[%s] %s\n" "$(date -u +%Y-%m-%dT%H:%M:%SZ)" "$*" | tee -a "$RUN_DIR/run.log"
}

query() {
  local name="$1"
  local expr="$2"
  curl -fsS --get --data-urlencode "query=$expr" \
    "$PROM/api/v1/query" \
    -o "$RUN_DIR/promql/$name.json"
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
    "$PROM/api/v1/query_range" \
    -o "$RUN_DIR/promql/$name.json"
}

run_smoke() {
  local outdir="$1"
  local email="$RUN_ID-post-recovery@example.com"
  mkdir -p "$outdir"
  python3 - "$API" "$email" "$outdir" <<'PY'
import json
import sys
import time
import urllib.request

base, email, outdir = sys.argv[1:4]

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
    "password": "FinalAwsDrill2026!",
    "name": "Final AWS Smoke",
})
token = auth["token"]
_, products = req("GET", "/api/v1/products", token=token)
product_id = products[0]["id"]
_, cart = req("GET", "/api/v1/cart", token=token)
_, cart = req("POST", "/api/v1/cart/items", {"productId": product_id, "quantity": 1}, token=token)
_, checkout = req("POST", "/api/v1/checkout", {"cartId": cart["id"]}, token=token)
request_id = checkout["requestId"]
final = {}
for _ in range(45):
    _, final = req("GET", f"/api/v1/checkout/{request_id}", token=token)
    if final.get("status") in {"COMPLETED", "FAILED", "CANCELLED"}:
        break
    time.sleep(1)
summary = {
    "email": email,
    "productId": product_id,
    "cartId": cart["id"],
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

CORE_DUMP="$(find "$RUN_DIR/backup" -name 'core_db_post_load_*.dump' | sort | tail -1)"
CHECKOUT_DUMP="$(find "$RUN_DIR/backup" -name 'checkout_db_post_load_*.dump' | sort | tail -1)"
test -s "$RUN_DIR/backup/core_db_post_load_restore_list.txt"
test -s "$RUN_DIR/backup/checkout_db_post_load_restore_list.txt"
log "Post-load backups validated as readable"

log "Recording Prometheus/Grafana identity before functional failure"
docker inspect -f "{{.Id}}" tesis-prometheus > "$RUN_DIR/prometheus-container-id-before-functional-down.txt"
docker inspect -f "{{.State.StartedAt}}" tesis-prometheus > "$RUN_DIR/prometheus-started-at-before-functional-down.txt"
curl -fsS "$PROM/api/v1/status/tsdb" -o "$RUN_DIR/prometheus-tsdb-before-functional-down.json"
query pre_failure_up 'up{job=~"core-api|checkout-service"}'

log "Inducing corrected functional-only destructive failure"
date -u +%Y-%m-%dT%H:%M:%SZ > "$RUN_DIR/t_failure_command_start.txt"
date -u +%s > "$RUN_DIR/t_failure_command_start.epoch"
(
  cd "$COMPOSE_DIR"
  docker compose -f "$FUNCTIONAL" down -v > "$RUN_DIR/failure-functional-down-v.log" 2>&1
)
date -u +%Y-%m-%dT%H:%M:%SZ > "$RUN_DIR/t_failure_command_end.txt"
date -u +%s > "$RUN_DIR/t_failure_command_end.epoch"
(curl -sS -o /dev/null -w "%{http_code}\n" "$API/actuator/health" || true) > "$RUN_DIR/failure-core-health-code.txt"
(curl -sS -o /dev/null -w "%{http_code}\n" "$CHECKOUT/actuator/health" || true) > "$RUN_DIR/failure-checkout-health-code.txt"
date -u +%Y-%m-%dT%H:%M:%SZ > "$RUN_DIR/t_incident_detected.txt"
date -u +%s > "$RUN_DIR/t_incident_detected.epoch"

log "Validating observability during functional failure"
curl -fsS "$PROM/-/ready" -o "$RUN_DIR/prometheus-ready-during-functional-down.txt"
curl -fsS "$GRAF/api/health" -o "$RUN_DIR/grafana-health-during-functional-down.json"
curl -fsS "$PROM/api/v1/status/tsdb" -o "$RUN_DIR/prometheus-tsdb-during-functional-down.json"
docker inspect -f "{{.Id}}" tesis-prometheus > "$RUN_DIR/prometheus-container-id-during-functional-down.txt"
docker inspect -f "{{.State.StartedAt}}" tesis-prometheus > "$RUN_DIR/prometheus-started-at-during-functional-down.txt"
sleep 35
curl -fsS "$PROM/api/v1/targets" -o "$RUN_DIR/prometheus-targets-during-functional-down.json"
query up_during_functional_down 'up{job=~"core-api|checkout-service"}'

log "Recovering functional stack from post-load backup"
date -u +%Y-%m-%dT%H:%M:%SZ > "$RUN_DIR/t_recovery_start.txt"
date -u +%s > "$RUN_DIR/t_recovery_start.epoch"
(
  cd "$COMPOSE_DIR"
  docker compose -f "$FUNCTIONAL" up -d core-db checkout-db rabbitmq > "$RUN_DIR/recovery/up-db-broker.log" 2>&1
)
sleep 12
cat "$CORE_DUMP" | docker exec -i tesis-core-db pg_restore -U postgres -d core_db --clean --if-exists > "$RUN_DIR/recovery/core-restore.log" 2>&1
cat "$CHECKOUT_DUMP" | docker exec -i tesis-checkout-db pg_restore -U postgres -d checkout_db --clean --if-exists > "$RUN_DIR/recovery/checkout-restore.log" 2>&1
(
  cd "$COMPOSE_DIR"
  docker compose -f "$FUNCTIONAL" up -d core-api checkout-service > "$RUN_DIR/recovery/up-services.log" 2>&1
)

log "Waiting for recovery gate"
RECOVERED=0
for _ in $(seq 1 120); do
  CORE=0
  CHK=0
  PROMOK=0
  TARGETS=0
  SMOKE=0
  curl -fsS "$API/actuator/health" > "$RUN_DIR/recovery/core-health-last.json" 2>/dev/null && CORE=1 || true
  curl -fsS "$CHECKOUT/actuator/health" > "$RUN_DIR/recovery/checkout-health-last.json" 2>/dev/null && CHK=1 || true
  curl -fsS "$PROM/-/ready" > "$RUN_DIR/recovery/prometheus-ready-last.txt" 2>/dev/null && PROMOK=1 || true
  if curl -fsS "$PROM/api/v1/targets" > "$RUN_DIR/recovery/prometheus-targets-last.json" 2>/dev/null; then
    if python3 - "$RUN_DIR/recovery/prometheus-targets-last.json" <<'PY'
import json
import sys

data = json.load(open(sys.argv[1], encoding="utf-8"))
seen = {target["labels"].get("job"): target["health"] for target in data["data"]["activeTargets"]}
raise SystemExit(0 if seen.get("core-api") == "up" and seen.get("checkout-service") == "up" else 1)
PY
    then
      TARGETS=1
    fi
  fi
  if [ "$CORE" = 1 ] && [ "$CHK" = 1 ] && [ "$PROMOK" = 1 ] && [ "$TARGETS" = 1 ]; then
    if run_smoke "$RUN_DIR/smoke/post-recovery" > "$RUN_DIR/smoke/post-recovery.stdout" 2> "$RUN_DIR/smoke/post-recovery.stderr"; then
      SMOKE=1
    fi
  fi
  printf "%s core=%s checkout=%s prometheus=%s targets=%s smoke=%s\n" \
    "$(date -u +%Y-%m-%dT%H:%M:%SZ)" "$CORE" "$CHK" "$PROMOK" "$TARGETS" "$SMOKE" \
    >> "$RUN_DIR/recovery/recovery-poll.log"
  if [ "$CORE" = 1 ] && [ "$CHK" = 1 ] && [ "$PROMOK" = 1 ] && [ "$TARGETS" = 1 ] && [ "$SMOKE" = 1 ]; then
    RECOVERED=1
    break
  fi
  sleep 5
done
if [ "$RECOVERED" != 1 ]; then
  echo "recovery failed" >&2
  exit 3
fi
date -u +%Y-%m-%dT%H:%M:%SZ > "$RUN_DIR/t_recovered_confirmed.txt"
date -u +%s > "$RUN_DIR/t_recovered_confirmed.epoch"
log "Recovery confirmed"

sleep 20
curl -fsS "$PROM/api/v1/targets" -o "$RUN_DIR/post-recovery-prometheus-targets.json"
query post_recovery_up 'up{job=~"core-api|checkout-service"}'
query post_recovery_traffic 'sum by (job) (increase(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[30m]))'
query post_recovery_status 'sum by (job,status) (increase(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[30m]))'
query post_recovery_errors_5xx 'sum by (job) (increase(http_server_requests_seconds_count{job=~"core-api|checkout-service",status=~"5.."}[30m])) or (sum by (job) (increase(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[30m])) * 0)'
query post_recovery_p95 'histogram_quantile(0.95, sum by (le, job) (rate(http_server_requests_seconds_bucket{job=~"core-api|checkout-service"}[5m])))'
START="$(cat "$RUN_DIR/t_baseline_start.epoch")"
END="$(date -u +%s)"
range_query availability_range 'up{job=~"core-api|checkout-service"}' "$START" "$END" 15s
range_query p95_range 'histogram_quantile(0.95, sum by (le, job) (rate(http_server_requests_seconds_bucket{job=~"core-api|checkout-service"}[5m])))' "$START" "$END" 15s
range_query error_rate_range 'sum(rate(http_server_requests_seconds_count{job=~"core-api|checkout-service",status=~"5.."}[5m])) / sum(rate(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[5m]))' "$START" "$END" 15s
range_query up_transition_range 'up{job=~"core-api|checkout-service"}' "$START" "$END" 15s
docker inspect -f "{{.Id}}" tesis-prometheus > "$RUN_DIR/prometheus-container-id-after-recovery.txt"
docker inspect -f "{{.State.StartedAt}}" tesis-prometheus > "$RUN_DIR/prometheus-started-at-after-recovery.txt"

log "Calculating final corrected summary"
python3 - "$RUN_DIR" <<'PY'
import json
import pathlib
import statistics
import sys

base = pathlib.Path(sys.argv[1])

def j(path):
    return json.loads((base / path).read_text())

def i(path):
    return int((base / path).read_text().strip())

def values(path):
    data = j(path)
    out = []
    for result in data.get("data", {}).get("result", []):
        if "value" in result:
            try:
                out.append(float(result["value"][1]))
            except Exception:
                pass
        for _, value in result.get("values", []):
            try:
                if value not in ("NaN", "+Inf", "-Inf"):
                    out.append(float(value))
            except Exception:
                pass
    return out

def transition(path):
    data = j(path)
    trans = {}
    for result in data.get("data", {}).get("result", []):
        job = result.get("metric", {}).get("job")
        vals = [value for _, value in result.get("values", [])]
        has = "1" in vals and "0" in vals and vals.index("1") < vals.index("0") and "1" in vals[vals.index("0") + 1:]
        trans[job] = {"hasUpBeforeDownAfterUp": has, "values": vals}
    return trans

load = j("purchase-run/run-summary.json")
checkout_ids = (base / "purchase-run/checkout-ids.txt").read_text().splitlines()
failure = i("t_failure_command_start.epoch")
recovered = i("t_recovered_confirmed.epoch")
incident = i("t_incident_detected.epoch")
backup_end = i("backup/t_post_load_backup_end.epoch")
availability = values("promql/availability_range.json")
errors = values("promql/error_rate_range.json")
p95 = values("promql/p95_range.json")
summary = {
    "runId": (base / "run-id.txt").read_text().strip(),
    "awsHost": (base / "aws-host.txt").read_text().strip(),
    "evidenceDir": str(base),
    "load": load,
    "backup": {
        "coreDump": str(next((base / "backup").glob("core_db_post_load_*.dump"))),
        "checkoutDump": str(next((base / "backup").glob("checkout_db_post_load_*.dump"))),
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
    "observabilityContinuity": {
        "prometheusContainerSame": (base / "prometheus-container-id-before-functional-down.txt").read_text().strip()
        == (base / "prometheus-container-id-during-functional-down.txt").read_text().strip()
        == (base / "prometheus-container-id-after-recovery.txt").read_text().strip(),
        "prometheusStartedAtStable": (base / "prometheus-started-at-before-functional-down.txt").read_text().strip()
        == (base / "prometheus-started-at-during-functional-down.txt").read_text().strip()
        == (base / "prometheus-started-at-after-recovery.txt").read_text().strip(),
        "transition": transition("promql/up_transition_range.json"),
    },
    "metrics": {
        "rtoSeconds": recovered - failure,
        "rpoTemporalSeconds": failure - backup_end,
        "rpoFunctionalMissingCheckoutIds": 0 if load.get("completedPurchases") == len(checkout_ids) else None,
        "mttrSeconds": recovered - incident,
        "availabilityObservedPercentFromPrometheusSamples": (sum(availability) / len(availability) * 100) if availability else None,
        "errorRateObservedMaxRatio": max(errors) if errors else 0.0,
        "latencyP95ObservedMaxSeconds": max(p95) if p95 else None,
        "latencyP95ObservedAvgSeconds": statistics.mean(p95) if p95 else None,
    },
    "postRecoverySmoke": j("smoke/post-recovery/smoke-summary.json"),
    "evidenceFiles": sorted(str(path.relative_to(base)) for path in base.rglob("*") if path.is_file()),
}
(base / "final-drill-summary.json").write_text(json.dumps(summary, indent=2) + "\n")
print(json.dumps(summary, indent=2))
PY
log "Final corrected drill completed"
