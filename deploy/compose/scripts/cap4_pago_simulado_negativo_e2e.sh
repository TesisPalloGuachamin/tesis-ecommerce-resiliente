#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
COMPOSE_FILE="$ROOT_DIR/deploy/compose/docker-compose.dev.yml"
OUT_DIR="${OUT_DIR:-$ROOT_DIR/evidencias/capitulo4-anexos-finales/05_pruebas_backend/pago_simulado_negativo_e2e}"
OVERRIDE_FILE="$OUT_DIR/docker-compose.runtime.override.yml"
RUN_ID="${RUN_ID:-cap4-payment-negative-e2e-$(date -u +%Y%m%dT%H%M%SZ)}"
CORE_URL="${CORE_URL:-http://localhost:8080}"
CHECKOUT_URL="${CHECKOUT_URL:-http://localhost:8082/api}"
RABBIT_URL="${RABBIT_URL:-http://localhost:15672/api}"
RABBITMQ_USER="${RABBITMQ_USER:-guest}"
RABBITMQ_PASSWORD="${RABBITMQ_PASSWORD:-guest}"
POSTGRES_USER="${POSTGRES_USER:-postgres}"
POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-postgres}"
RUNTIME_DIR="${RUNTIME_DIR:-$HOME/.tesis-cap4-runtime/$RUN_ID}"

export DOCKER_CONFIG="${DOCKER_CONFIG:-$ROOT_DIR/.docker-tmp}"
export TMPDIR="${TMPDIR:-$ROOT_DIR/.tmp}"
mkdir -p "$DOCKER_CONFIG" "$TMPDIR"

export POSTGRES_CORE_PORT="${POSTGRES_CORE_PORT:-55432}"
export POSTGRES_CHECKOUT_PORT="${POSTGRES_CHECKOUT_PORT:-55433}"

mkdir -p "$OUT_DIR"
: > "$OUT_DIR/comandos_ejecutados.txt"

log_cmd() {
  printf '%s\n' "$*" >> "$OUT_DIR/comandos_ejecutados.txt"
}

run_logged() {
  log_cmd "$*"
  "$@"
}

git_branch() {
  git -C "$ROOT_DIR" branch --show-current 2>/dev/null || echo "${GIT_BRANCH_OVERRIDE:-unknown}"
}

git_commit() {
  git -C "$ROOT_DIR" rev-parse HEAD 2>/dev/null || echo "${GIT_COMMIT_OVERRIDE:-unknown}"
}

write_versions() {
  {
    echo "run_id=$RUN_ID"
    echo "date_utc=$(date -u -Iseconds)"
    echo "date_local=$(date -Iseconds)"
    echo "git_branch=$(git_branch)"
    echo "git_commit=$(git_commit)"
    echo "runtime_dir=$RUNTIME_DIR"
    echo "docker=$(docker --version 2>/dev/null || true)"
    echo "docker_compose=$(docker compose version 2>/dev/null || true)"
    echo "java=$(java -version 2>&1 | head -1 || true)"
    echo "maven=$(mvn -version 2>/dev/null | head -1 || true)"
    echo "node=$(node --version 2>/dev/null || true)"
    echo "k6=$(k6 version 2>/dev/null || true)"
    echo "terraform=$(terraform version 2>/dev/null | head -1 || true)"
  } > "$OUT_DIR/ambiente_versiones.txt"
}

compose() {
  docker compose -f "$COMPOSE_FILE" -f "$OVERRIDE_FILE" "$@"
}

write_compose_override() {
  mkdir -p "$RUNTIME_DIR/core_db_data" "$RUNTIME_DIR/checkout_db_data" "$RUNTIME_DIR/rabbitmq_data"
  cat > "$OVERRIDE_FILE" <<EOF
services:
  core-db:
    volumes:
      - "$RUNTIME_DIR/core_db_data:/var/lib/postgresql/data"
  checkout-db:
    volumes:
      - "$RUNTIME_DIR/checkout_db_data:/var/lib/postgresql/data"
  rabbitmq:
    volumes:
      - "$RUNTIME_DIR/rabbitmq_data:/var/lib/rabbitmq"
EOF
}

wait_http() {
  local url="$1"
  local name="$2"
  for _ in $(seq 1 90); do
    if curl -fsS "$url" >/dev/null 2>&1; then
      return 0
    fi
    sleep 2
  done
  echo "Timeout waiting for $name at $url" >&2
  return 1
}

rabbit_queues() {
  local target="$1"
  curl -fsS -u "$RABBITMQ_USER:$RABBITMQ_PASSWORD" "$RABBIT_URL/queues/%2F" \
    | jq '[.[] | {name, messages, messages_ready, messages_unacknowledged, consumers, durable, state}]' \
    > "$target"
}

http_json() {
  local name="$1"
  local method="$2"
  local url="$3"
  local data="${4:-}"
  local auth="${5:-}"
  local body_file="$OUT_DIR/http_${name}.body.json"
  local meta_file="$OUT_DIR/http_${name}.meta.json"
  local code

  if [[ -n "$data" && -n "$auth" ]]; then
    code=$(curl -sS -o "$body_file" -w "%{http_code}" -X "$method" "$url" \
      -H "Content-Type: application/json" -H "Authorization: Bearer $auth" -d "$data")
  elif [[ -n "$data" ]]; then
    code=$(curl -sS -o "$body_file" -w "%{http_code}" -X "$method" "$url" \
      -H "Content-Type: application/json" -d "$data")
  elif [[ -n "$auth" ]]; then
    code=$(curl -sS -o "$body_file" -w "%{http_code}" -X "$method" "$url" \
      -H "Authorization: Bearer $auth")
  else
    code=$(curl -sS -o "$body_file" -w "%{http_code}" -X "$method" "$url")
  fi

  jq -n --arg name "$name" --arg method "$method" --arg url "$url" --arg code "$code" \
    '{name:$name, method:$method, url:$url, http_code:($code|tonumber)}' > "$meta_file"
}

db_core() {
  docker exec -e PGPASSWORD="$POSTGRES_PASSWORD" tesis-core-db \
    psql -U "$POSTGRES_USER" -d core_db "$@"
}

db_checkout() {
  docker exec -e PGPASSWORD="$POSTGRES_PASSWORD" tesis-checkout-db \
    psql -U "$POSTGRES_USER" -d checkout_db "$@"
}

poll_checkout_status() {
  local request_id="$1"
  local token="$2"
  local status="PENDING"

  for _ in $(seq 1 60); do
    http_json "checkout_status" GET "$CORE_URL/api/v1/checkout/$request_id" "" "$token"
    status="$(jq -r '.status // empty' "$OUT_DIR/http_checkout_status.body.json")"
    if [[ "$status" == "FAILED" || "$status" == "COMPLETED" ]]; then
      echo "$status"
      return 0
    fi
    sleep 2
  done

  echo "$status"
  return 1
}

main() {
  write_compose_override
  write_versions
  log_cmd "export PAYMENT_SIMULATOR_DECLINE_ENABLED=true"
  log_cmd "export PAYMENT_SIMULATOR_DECLINE_AMOUNT=29.99"
  local build_args=()
  if [[ "${SKIP_DOCKER_BUILD:-false}" != "true" ]]; then
    build_args=(--build)
    log_cmd "docker compose -f deploy/compose/docker-compose.dev.yml -f pago_simulado_negativo_e2e/docker-compose.runtime.override.yml up -d --build core-api checkout-service"
  else
    log_cmd "SKIP_DOCKER_BUILD=true docker compose -f deploy/compose/docker-compose.dev.yml -f pago_simulado_negativo_e2e/docker-compose.runtime.override.yml up -d core-api checkout-service"
  fi

  export PAYMENT_SIMULATOR_DECLINE_ENABLED=true
  export PAYMENT_SIMULATOR_DECLINE_AMOUNT=29.99
  export CHECKOUT_SERVICE_LOG_LEVEL=DEBUG
  export CORE_API_LOG_LEVEL=DEBUG
  run_logged docker compose -f "$COMPOSE_FILE" -f "$OVERRIDE_FILE" up -d "${build_args[@]}" core-api checkout-service

  wait_http "$CORE_URL/actuator/health" "core-api"
  wait_http "$CHECKOUT_URL/actuator/health" "checkout-service"
  wait_http "http://localhost:15672" "rabbitmq-management"

  compose ps > "$OUT_DIR/docker_compose_ps_before.txt"
  compose images > "$OUT_DIR/docker_images.txt"
  rabbit_queues "$OUT_DIR/rabbitmq_queues_before.json"

  local email="cap4-negative-${RUN_ID}@example.com"
  local password="Cap4Test123!"
  local register_payload
  register_payload=$(jq -n --arg email "$email" --arg password "$password" \
    '{email:$email, password:$password, name:"Cap4 Negative Payment"}')

  http_json "register" POST "$CORE_URL/api/v1/auth/register" "$register_payload"
  local token
  token="$(jq -r '.token' "$OUT_DIR/http_register.body.json")"

  http_json "products" GET "$CORE_URL/api/v1/products" "" "$token"
  local product_id
  product_id="$(jq -r '.[] | select((.price|tostring) == "29.99") | .id' "$OUT_DIR/http_products.body.json" | head -1)"
  if [[ -z "$product_id" || "$product_id" == "null" ]]; then
    echo "No product with price 29.99 found" >&2
    exit 2
  fi

  local cart_payload
  cart_payload=$(jq -n --arg productId "$product_id" '{productId:$productId, quantity:1}')
  http_json "cart_add" POST "$CORE_URL/api/v1/cart/items" "$cart_payload" "$token"
  local cart_id
  cart_id="$(jq -r '.id' "$OUT_DIR/http_cart_add.body.json")"

  local checkout_payload
  checkout_payload=$(jq -n --arg cartId "$cart_id" '{cartId:$cartId}')
  http_json "checkout_start" POST "$CORE_URL/api/v1/checkout" "$checkout_payload" "$token"
  local request_id
  request_id="$(jq -r '.requestId' "$OUT_DIR/http_checkout_start.body.json")"

  local final_status
  final_status="$(poll_checkout_status "$request_id" "$token")"
  http_json "checkout_final" GET "$CORE_URL/api/v1/checkout/$request_id" "" "$token"

  jq -n \
    --slurpfile register "$OUT_DIR/http_register.body.json" \
    --slurpfile products "$OUT_DIR/http_products.body.json" \
    --slurpfile cart "$OUT_DIR/http_cart_add.body.json" \
    --slurpfile checkout_start "$OUT_DIR/http_checkout_start.body.json" \
    --slurpfile checkout_final "$OUT_DIR/http_checkout_final.body.json" \
    --slurpfile meta_register "$OUT_DIR/http_register.meta.json" \
    --slurpfile meta_products "$OUT_DIR/http_products.meta.json" \
    --slurpfile meta_cart "$OUT_DIR/http_cart_add.meta.json" \
    --slurpfile meta_checkout_start "$OUT_DIR/http_checkout_start.meta.json" \
    --slurpfile meta_checkout_final "$OUT_DIR/http_checkout_final.meta.json" \
    '{register:{meta:$meta_register[0], body:$register[0]},
      products:{meta:$meta_products[0], body:$products[0]},
      cart_add:{meta:$meta_cart[0], body:$cart[0]},
      checkout_start:{meta:$meta_checkout_start[0], body:$checkout_start[0]},
      checkout_final:{meta:$meta_checkout_final[0], body:$checkout_final[0]}}' \
    > "$OUT_DIR/respuestas_http.json"

  local event_id
  event_id="$(db_core -Atc "select payload::jsonb ->> 'eventId' from outbox_events where payload::jsonb ->> 'requestId' = '$request_id' order by created_at desc limit 1;")"

  db_core -c "select request_id, status, total_amount, created_at, updated_at from checkout_requests where request_id = '$request_id';" \
    > "$OUT_DIR/verificacion_core_db.txt"
  db_checkout -c "select o.checkout_request_id as event_id, o.status as order_status, o.total_amount, pa.status as payment_status, pa.error_message from orders o left join payment_attempts pa on pa.order_id = o.id where o.checkout_request_id = '$event_id';" \
    > "$OUT_DIR/verificacion_checkout_db.txt"
  db_checkout -c "select event_id, event_type, processed, created_at, processed_at from inbox_events where event_id = '$event_id';" \
    >> "$OUT_DIR/verificacion_checkout_db.txt"

  rabbit_queues "$OUT_DIR/rabbitmq_queues_after.json"
  jq -n --slurpfile before "$OUT_DIR/rabbitmq_queues_before.json" --slurpfile after "$OUT_DIR/rabbitmq_queues_after.json" \
    --arg requestId "$request_id" --arg eventId "$event_id" \
    '{requestId:$requestId, eventId:$eventId, before:$before[0], after:$after[0]}' \
    > "$OUT_DIR/eventos_rabbitmq.json"

  compose logs --no-color core-api > "$OUT_DIR/logs_core_api.txt"
  compose logs --no-color checkout-service > "$OUT_DIR/logs_checkout_service.txt"
  jq -n --arg requestId "$request_id" --arg eventId "$event_id" --arg status "$final_status" \
    '{requestId:$requestId, eventId:$eventId, finalStatus:$status, expectedStatus:"FAILED"}' \
    > "$OUT_DIR/estado_checkout.json"

  local result="FAIL"
  local exit_code=1
  if [[ "$final_status" == "FAILED" ]] \
      && grep -q "FAILED" "$OUT_DIR/verificacion_core_db.txt" \
      && grep -q "FAILED" "$OUT_DIR/verificacion_checkout_db.txt" \
      && grep -q "simulated decline" "$OUT_DIR/verificacion_checkout_db.txt" \
      && ! grep -q "COMPLETED" "$OUT_DIR/http_checkout_final.body.json"; then
    result="PASS"
    exit_code=0
  fi

  {
    echo "RUN_ID=$RUN_ID"
    echo "test=negative simulated payment end-to-end"
    echo "condition=PAYMENT_SIMULATOR_DECLINE_ENABLED=true and total amount 29.99"
    echo "request_id=$request_id"
    echo "event_id=$event_id"
    echo "expected=FAILED"
    echo "observed=$final_status"
    echo "result=$result"
  } > "$OUT_DIR/resultado_prueba.txt"
  echo "$exit_code" > "$OUT_DIR/exit_code.txt"

  cat > "$OUT_DIR/README.md" <<EOF
# Pago simulado negativo E2E

Evidencia generada por \`deploy/compose/scripts/cap4_pago_simulado_negativo_e2e.sh\`.

- RUN_ID: \`$RUN_ID\`
- Commit: \`$(git_commit)\`
- Condicion controlada: \`PAYMENT_SIMULATOR_DECLINE_ENABLED=true\` y monto \`29.99\`
- Resultado esperado: checkout en estado \`FAILED\`
- Resultado observado: \`$final_status\`
- Estado de la prueba: \`$result\`

La evidencia unitaria previa se mantiene como complemento historico. Esta prueba valida la propagacion completa del rechazo desde API, RabbitMQ, checkout-service, persistencia y retorno consultable en core-api.
EOF

  cat > "$OUT_DIR/REPORTE_PAGO_SIMULADO_NEGATIVO_E2E.md" <<EOF
# Reporte pago simulado negativo E2E

| Campo | Valor |
|---|---|
| RUN_ID | \`$RUN_ID\` |
| Fecha UTC | \`$(date -u -Iseconds)\` |
| Rama | \`$(git_branch)\` |
| Commit | \`$(git_commit)\` |
| Ambiente | Docker Compose local \`deploy/compose/docker-compose.dev.yml\` |
| Dato de entrada | Producto con precio \`29.99\`, cantidad \`1\` |
| Condicion simulada | \`PaymentPort\` retorna \`success=false\` por bandera sandbox del adaptador simulado |
| HTTP registro | \`$(jq -r '.register.meta.http_code' "$OUT_DIR/respuestas_http.json")\` |
| HTTP productos | \`$(jq -r '.products.meta.http_code' "$OUT_DIR/respuestas_http.json")\` |
| HTTP carrito | \`$(jq -r '.cart_add.meta.http_code' "$OUT_DIR/respuestas_http.json")\` |
| HTTP checkout | \`$(jq -r '.checkout_start.meta.http_code' "$OUT_DIR/respuestas_http.json")\` |
| Estado esperado | \`FAILED\` |
| Estado observado | \`$final_status\` |
| RequestId | \`$request_id\` |
| EventId | \`$event_id\` |
| Criterio de aprobacion | Estado \`FAILED\` en API y bases, error \`simulated decline\`, sin estado final \`COMPLETED\` |
| Resultado | \`$result\` |

## Archivos de evidencia

- \`respuestas_http.json\`
- \`estado_checkout.json\`
- \`verificacion_core_db.txt\`
- \`verificacion_checkout_db.txt\`
- \`eventos_rabbitmq.json\`
- \`logs_core_api.txt\`
- \`logs_checkout_service.txt\`
- \`docker_images.txt\`
- \`resultado_prueba.txt\`
- \`exit_code.txt\`
EOF

  exit "$exit_code"
}

main "$@"
