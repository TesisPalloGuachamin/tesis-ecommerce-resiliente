#!/usr/bin/env bash
set -euo pipefail

BASE="${BASE:-/home/ec2-user/tesis-ecommerce}"
COMPOSE_DIR="${COMPOSE_DIR:-$BASE/deploy/compose}"
FUNCTIONAL="${FUNCTIONAL:-$COMPOSE_DIR/docker-compose.functional.yml}"
OBSERVABILITY="${OBSERVABILITY:-$COMPOSE_DIR/docker-compose.observability.yml}"
API="${API:-http://localhost:8080}"
CHECKOUT="${CHECKOUT:-http://localhost:8082/api}"
PROM="${PROM:-http://localhost:9090}"
GRAF="${GRAF:-http://localhost:3000}"
RUN_ID="${RUN_ID:-obs-split-$(date -u +%Y%m%dT%H%M%SZ)}"
EVD="${EVD:-$BASE/evidencias/final-aws-observability-split/$RUN_ID}"

mkdir -p "$EVD" "$EVD/backup" "$EVD/promql" "$EVD/recovery"

log() {
  printf "[%s] %s\n" "$(date -u +%Y-%m-%dT%H:%M:%SZ)" "$*" | tee -a "$EVD/run.log"
}

query() {
  local name="$1"
  local expr="$2"
  curl -fsS --get --data-urlencode "query=$expr" \
    "$PROM/api/v1/query" \
    -o "$EVD/promql/$name.json"
}

printf "%s\n" "$RUN_ID" > "$EVD/run-id.txt"
printf "%s\n" "$FUNCTIONAL" > "$EVD/functional-compose-file.txt"
printf "%s\n" "$OBSERVABILITY" > "$EVD/observability-compose-file.txt"

log "Creating shared network"
docker network inspect tesis-ecommerce-network >/dev/null 2>&1 || docker network create tesis-ecommerce-network >/dev/null

log "Taking correction backup before split validation"
docker exec tesis-core-db pg_dump -U postgres -d core_db -Fc > "$EVD/backup/core_db_before_split.dump"
docker exec tesis-checkout-db pg_dump -U postgres -d checkout_db -Fc > "$EVD/backup/checkout_db_before_split.dump"
cat "$EVD/backup/core_db_before_split.dump" | docker exec -i tesis-core-db pg_restore -l > "$EVD/backup/core_db_restore_list.txt"
cat "$EVD/backup/checkout_db_before_split.dump" | docker exec -i tesis-checkout-db pg_restore -l > "$EVD/backup/checkout_db_restore_list.txt"
ls -lh "$EVD/backup"/*.dump > "$EVD/backup/backup-files.txt"

log "Migrating old combined compose to split compose without deleting volumes"
(
  cd "$COMPOSE_DIR"
  docker compose -f docker-compose.dev.yml down > "$EVD/combined-down.log" 2>&1 || true
  docker compose -f docker-compose.observability.yml up -d > "$EVD/observability-up.log" 2>&1
  docker compose -f docker-compose.functional.yml up -d core-db checkout-db rabbitmq core-api checkout-service > "$EVD/functional-up.log" 2>&1
)

log "Waiting for split stack health"
for i in $(seq 1 90); do
  if curl -fsS "$API/actuator/health" > "$EVD/core-health-before.json" 2>/dev/null \
    && curl -fsS "$CHECKOUT/actuator/health" > "$EVD/checkout-health-before.json" 2>/dev/null \
    && curl -fsS "$PROM/-/ready" > "$EVD/prometheus-ready-before.txt" 2>/dev/null \
    && curl -fsS "$GRAF/api/health" > "$EVD/grafana-health-before.json" 2>/dev/null; then
    break
  fi
  sleep 5
  if [ "$i" = 90 ]; then
    exit 2
  fi
done
sleep 20
curl -fsS "$PROM/api/v1/targets" > "$EVD/prometheus-targets-before.json"
curl -fsS "$PROM/api/v1/status/tsdb" > "$EVD/prometheus-tsdb-before.json"
query up_before 'up{job=~"core-api|checkout-service"}'
query prometheus_head_series_before 'prometheus_tsdb_head_series'
docker inspect -f "{{.Id}}" tesis-prometheus > "$EVD/prometheus-container-id-before.txt"
docker inspect -f "{{.State.StartedAt}}" tesis-prometheus > "$EVD/prometheus-started-at-before.txt"
docker volume ls --format "{{.Name}}" | sort > "$EVD/docker-volumes-before.txt"
docker ps --format "table {{.Names}}\t{{.Status}}" > "$EVD/docker-ps-before-functional-down.txt"

log "Running corrected destructive command against functional stack only"
date -u +%Y-%m-%dT%H:%M:%SZ > "$EVD/t_functional_down_start.txt"
(
  cd "$COMPOSE_DIR"
  docker compose -f docker-compose.functional.yml down -v > "$EVD/functional-down-v.log" 2>&1
)
date -u +%Y-%m-%dT%H:%M:%SZ > "$EVD/t_functional_down_end.txt"

log "Validating observability survived functional down"
curl -fsS "$PROM/-/ready" > "$EVD/prometheus-ready-during-functional-down.txt"
curl -fsS "$GRAF/api/health" > "$EVD/grafana-health-during-functional-down.json"
curl -fsS "$PROM/api/v1/status/tsdb" > "$EVD/prometheus-tsdb-during-functional-down.json"
docker inspect -f "{{.Id}}" tesis-prometheus > "$EVD/prometheus-container-id-during.txt"
docker inspect -f "{{.State.StartedAt}}" tesis-prometheus > "$EVD/prometheus-started-at-during.txt"
docker ps --format "table {{.Names}}\t{{.Status}}" > "$EVD/docker-ps-during-functional-down.txt"
sleep 35
curl -fsS "$PROM/api/v1/targets" > "$EVD/prometheus-targets-during-functional-down.json"
query up_during 'up{job=~"core-api|checkout-service"}'
query prometheus_head_series_during 'prometheus_tsdb_head_series'
end="$(date -u +%s)"
start="$((end - 180))"
curl -fsS --get \
  --data-urlencode 'query=up{job=~"core-api|checkout-service"}' \
  --data-urlencode "start=$start" \
  --data-urlencode "end=$end" \
  --data-urlencode 'step=15s' \
  "$PROM/api/v1/query_range" \
  > "$EVD/promql/up_range_survived_functional_down.json"

log "Recovering functional stack from correction backup"
(
  cd "$COMPOSE_DIR"
  docker compose -f docker-compose.functional.yml up -d core-db checkout-db rabbitmq > "$EVD/recovery/up-db-broker.log" 2>&1
)
sleep 12
cat "$EVD/backup/core_db_before_split.dump" | docker exec -i tesis-core-db pg_restore -U postgres -d core_db --clean --if-exists > "$EVD/recovery/core-restore.log" 2>&1
cat "$EVD/backup/checkout_db_before_split.dump" | docker exec -i tesis-checkout-db pg_restore -U postgres -d checkout_db --clean --if-exists > "$EVD/recovery/checkout-restore.log" 2>&1
(
  cd "$COMPOSE_DIR"
  docker compose -f docker-compose.functional.yml up -d core-api checkout-service > "$EVD/recovery/up-services.log" 2>&1
)
for i in $(seq 1 90); do
  if curl -fsS "$API/actuator/health" > "$EVD/core-health-after.json" 2>/dev/null \
    && curl -fsS "$CHECKOUT/actuator/health" > "$EVD/checkout-health-after.json" 2>/dev/null; then
    break
  fi
  sleep 5
  if [ "$i" = 90 ]; then
    exit 3
  fi
done
sleep 20
curl -fsS "$PROM/api/v1/targets" > "$EVD/prometheus-targets-after-recovery.json"
query up_after_recovery 'up{job=~"core-api|checkout-service"}'
docker inspect -f "{{.Id}}" tesis-prometheus > "$EVD/prometheus-container-id-after.txt"
docker inspect -f "{{.State.StartedAt}}" tesis-prometheus > "$EVD/prometheus-started-at-after.txt"
docker volume ls --format "{{.Name}}" | sort > "$EVD/docker-volumes-after.txt"

log "Building validation summary"
python3 - "$EVD" <<'PY'
import json
import pathlib
import sys

base = pathlib.Path(sys.argv[1])

def text(name):
    return (base / name).read_text().strip()

def js(name):
    return json.loads((base / name).read_text())

before_id = text("prometheus-container-id-before.txt")
during_id = text("prometheus-container-id-during.txt")
after_id = text("prometheus-container-id-after.txt")
before_started = text("prometheus-started-at-before.txt")
during_started = text("prometheus-started-at-during.txt")
after_started = text("prometheus-started-at-after.txt")
summary = {
    "runId": text("run-id.txt"),
    "evidenceDir": str(base),
    "correctedDestructiveCommand": "docker compose -f docker-compose.functional.yml down -v",
    "prometheusContainerSameDuringFunctionalDown": before_id == during_id == after_id,
    "prometheusStartedAtStable": before_started == during_started == after_started,
    "prometheusReadyDuringFunctionalDown": text("prometheus-ready-during-functional-down.txt"),
    "grafanaHealthDuringFunctionalDown": js("grafana-health-during-functional-down.json"),
    "coreHealthAfterRecovery": js("core-health-after.json").get("status"),
    "checkoutHealthAfterRecovery": js("checkout-health-after.json").get("status"),
    "targetHealthDuringFunctionalDown": [
        {"job": target["labels"].get("job"), "health": target["health"], "lastError": target.get("lastError", "")}
        for target in js("prometheus-targets-during-functional-down.json")["data"]["activeTargets"]
        if target["labels"].get("job") in {"core-api", "checkout-service"}
    ],
    "targetHealthAfterRecovery": [
        {"job": target["labels"].get("job"), "health": target["health"], "lastError": target.get("lastError", "")}
        for target in js("prometheus-targets-after-recovery.json")["data"]["activeTargets"]
        if target["labels"].get("job") in {"core-api", "checkout-service"}
    ],
    "backupFiles": text("backup/backup-files.txt").splitlines(),
    "evidenceFileCount": len([path for path in base.rglob("*") if path.is_file()]),
}
(base / "observability-split-summary.json").write_text(json.dumps(summary, indent=2) + "\n")
print(json.dumps(summary, indent=2))
PY
