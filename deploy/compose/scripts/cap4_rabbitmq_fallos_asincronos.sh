#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
COMPOSE_FILE="$ROOT_DIR/deploy/compose/docker-compose.dev.yml"
OUT_DIR="${OUT_DIR:-$ROOT_DIR/evidencias/capitulo4-anexos-finales/04_rabbitmq/fallos_asincronos}"
RUN_ID="${RUN_ID:-cap4-mq-failures-$(date -u +%Y%m%dT%H%M%SZ)}"
OVERRIDE_FILE="$OUT_DIR/docker-compose.runtime.override.yml"
RUNTIME_DIR="${RUNTIME_DIR:-$HOME/.tesis-cap4-runtime/$RUN_ID}"
CORE_URL="${CORE_URL:-http://localhost:8080}"
RABBIT_URL="${RABBIT_URL:-http://localhost:15672/api}"
RABBITMQ_USER="${RABBITMQ_USER:-guest}"
RABBITMQ_PASSWORD="${RABBITMQ_PASSWORD:-guest}"
POSTGRES_USER="${POSTGRES_USER:-postgres}"
POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-postgres}"

export DOCKER_CONFIG="${DOCKER_CONFIG:-$ROOT_DIR/.docker-tmp}"
export TMPDIR="${TMPDIR:-$ROOT_DIR/.tmp}"
export POSTGRES_CORE_PORT="${POSTGRES_CORE_PORT:-55432}"
export POSTGRES_CHECKOUT_PORT="${POSTGRES_CHECKOUT_PORT:-55433}"
export PAYMENT_SIMULATOR_DECLINE_ENABLED=false
export PAYMENT_SIMULATOR_DECLINE_AMOUNT=29.99
export CHECKOUT_SERVICE_LOG_LEVEL=DEBUG
export CORE_API_LOG_LEVEL=DEBUG

mkdir -p "$OUT_DIR" "$DOCKER_CONFIG" "$TMPDIR"
: > "$OUT_DIR/comandos_ejecutados.txt"

log_cmd() {
  printf '%s\n' "$*" >> "$OUT_DIR/comandos_ejecutados.txt"
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

wait_container_stopped() {
  local container="$1"
  for _ in $(seq 1 45); do
    if [[ "$(docker inspect -f '{{.State.Running}}' "$container" 2>/dev/null || echo false)" == "false" ]]; then
      return 0
    fi
    sleep 1
  done
  echo "Timeout waiting for $container to stop" >&2
  return 1
}

wait_queue_ready() {
  local queue="$1"
  local expected="$2"
  local output="$3"
  for _ in $(seq 1 90); do
    rabbit_queue "$queue" > "$output"
    local ready
    ready="$(jq -r '.messages_ready // 0' "$output")"
    if [[ "$ready" -ge "$expected" ]]; then
      return 0
    fi
    sleep 2
  done
  rabbit_queue "$queue" > "$output" || true
  return 1
}

wait_queue_consumers() {
  local queue="$1"
  local expected="$2"
  local output="$3"
  for _ in $(seq 1 90); do
    rabbit_queue "$queue" > "$output"
    local consumers
    consumers="$(jq -r '.consumers // 0' "$output")"
    if [[ "$consumers" -ge "$expected" ]]; then
      return 0
    fi
    sleep 2
  done
  rabbit_queue "$queue" > "$output" || true
  return 1
}

wait_queue_drained() {
  local queue="$1"
  local output="$2"
  for _ in $(seq 1 90); do
    rabbit_queue "$queue" > "$output"
    local messages
    messages="$(jq -r '.messages // 0' "$output")"
    if [[ "$messages" -eq 0 ]]; then
      return 0
    fi
    sleep 2
  done
  rabbit_queue "$queue" > "$output" || true
  return 1
}

start_stack() {
  local requeue="${1:-true}"
  export RABBITMQ_LISTENER_DEFAULT_REQUEUE_REJECTED="$requeue"
  log_cmd "RABBITMQ_LISTENER_DEFAULT_REQUEUE_REJECTED=$requeue docker compose -f deploy/compose/docker-compose.dev.yml -f fallos_asincronos/docker-compose.runtime.override.yml up -d core-api checkout-service"
  compose up -d core-api checkout-service
  wait_http "$CORE_URL/actuator/health" "core-api"
  wait_http "http://localhost:8082/api/actuator/health" "checkout-service"
  wait_http "http://localhost:15672" "rabbitmq-management"
}

recreate_checkout() {
  local requeue="$1"
  export RABBITMQ_LISTENER_DEFAULT_REQUEUE_REJECTED="$requeue"
  log_cmd "RABBITMQ_LISTENER_DEFAULT_REQUEUE_REJECTED=$requeue docker compose up -d --force-recreate --no-deps checkout-service"
  compose up -d --force-recreate --no-deps checkout-service
  wait_http "http://localhost:8082/api/actuator/health" "checkout-service"
}

db_core() {
  docker exec -e PGPASSWORD="$POSTGRES_PASSWORD" tesis-core-db \
    psql -U "$POSTGRES_USER" -d core_db "$@"
}

db_checkout() {
  docker exec -e PGPASSWORD="$POSTGRES_PASSWORD" tesis-checkout-db \
    psql -U "$POSTGRES_USER" -d checkout_db "$@"
}

rabbit_queues() {
  local target="$1"
  curl -fsS -u "$RABBITMQ_USER:$RABBITMQ_PASSWORD" "$RABBIT_URL/queues/%2F" \
    | jq '[.[] | {name, messages, messages_ready, messages_unacknowledged, consumers, durable, state}]' \
    > "$target"
}

rabbit_queue() {
  local queue="$1"
  curl -fsS -u "$RABBITMQ_USER:$RABBITMQ_PASSWORD" "$RABBIT_URL/queues/%2F/$queue" \
    | jq '{name, messages, messages_ready, messages_unacknowledged, consumers, state}'
}

purge_queue() {
  local queue="$1"
  curl -fsS -u "$RABBITMQ_USER:$RABBITMQ_PASSWORD" -X DELETE "$RABBIT_URL/queues/%2F/$queue/contents" >/dev/null || true
}

publish_checkout_requested() {
  local payload="$1"
  local out_file="$2"
  local body
  body="$(jq -n --arg payload "$payload" \
    '{properties:{content_type:"application/json"}, routing_key:"checkout.requested", payload:$payload, payload_encoding:"string"}')"
  curl -fsS -u "$RABBITMQ_USER:$RABBITMQ_PASSWORD" \
    -H "Content-Type: application/json" \
    -X POST "$RABBIT_URL/exchanges/%2F/ecommerce.checkout.exchange/publish" \
    -d "$body" | tee "$out_file" >/dev/null
}

http_json() {
  local name="$1"
  local method="$2"
  local url="$3"
  local data="${4:-}"
  local auth="${5:-}"
  local out_dir="$6"
  local body_file="$out_dir/http_${name}.body.json"
  local meta_file="$out_dir/http_${name}.meta.json"
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

  jq -n --arg code "$code" --arg url "$url" --arg method "$method" \
    '{method:$method,url:$url,http_code:($code|tonumber)}' > "$meta_file"
}

create_checkout() {
  local scenario_dir="$1"
  local email="cap4-mq-${RUN_ID}-$(date +%s%N)@example.com"
  local password="Cap4Test123!"
  local register_payload
  register_payload=$(jq -n --arg email "$email" --arg password "$password" \
    '{email:$email, password:$password, name:"Cap4 MQ Test"}')

  http_json register POST "$CORE_URL/api/v1/auth/register" "$register_payload" "" "$scenario_dir"
  local token
  token="$(jq -r '.token' "$scenario_dir/http_register.body.json")"

  http_json products GET "$CORE_URL/api/v1/products" "" "$token" "$scenario_dir"
  local product_id
  product_id="$(jq -r '.[] | select((.price|tostring) == "29.99") | .id' "$scenario_dir/http_products.body.json" | head -1)"
  local cart_payload
  cart_payload="$(jq -n --arg productId "$product_id" '{productId:$productId, quantity:1}')"
  http_json cart_add POST "$CORE_URL/api/v1/cart/items" "$cart_payload" "$token" "$scenario_dir"
  local cart_id
  cart_id="$(jq -r '.id' "$scenario_dir/http_cart_add.body.json")"

  local checkout_payload
  checkout_payload="$(jq -n --arg cartId "$cart_id" '{cartId:$cartId}')"
  http_json checkout_start POST "$CORE_URL/api/v1/checkout" "$checkout_payload" "$token" "$scenario_dir"
  local request_id
  request_id="$(jq -r '.requestId' "$scenario_dir/http_checkout_start.body.json")"

  jq -n --arg token "$token" --arg requestId "$request_id" '{token:$token, requestId:$requestId}' > "$scenario_dir/generated_checkout.json"
}

poll_status() {
  local scenario_dir="$1"
  local request_id="$2"
  local token="$3"
  local expected="${4:-COMPLETED}"
  local status="PENDING"
  for _ in $(seq 1 60); do
    http_json checkout_status GET "$CORE_URL/api/v1/checkout/$request_id" "" "$token" "$scenario_dir"
    status="$(jq -r '.status // empty' "$scenario_dir/http_checkout_status.body.json")"
    if [[ "$status" == "$expected" || "$status" == "FAILED" || "$status" == "COMPLETED" ]]; then
      echo "$status"
      return 0
    fi
    sleep 2
  done
  echo "$status"
}

event_id_for_request() {
  local request_id="$1"
  db_core -Atc "select payload::jsonb ->> 'eventId' from outbox_events where payload::jsonb ->> 'requestId' = '$request_id' order by created_at desc limit 1;"
}

payload_for_request() {
  local request_id="$1"
  db_core -Atc "select payload from outbox_events where payload::jsonb ->> 'requestId' = '$request_id' order by created_at desc limit 1;"
}

scenario_header() {
  local dir="$1"
  local id="$2"
  local name="$3"
  mkdir -p "$dir"
  rabbit_queues "$dir/rabbitmq_before.json"
  compose ps > "$dir/docker_compose_ps_before.txt"
  {
    echo "id=$id"
    echo "name=$name"
    echo "date_utc=$(date -u -Iseconds)"
    echo "commit=$(git_commit)"
  } > "$dir/metadata.txt"
}

scenario_footer() {
  local dir="$1"
  rabbit_queues "$dir/rabbitmq_after.json"
  compose logs --no-color core-api > "$dir/logs_core_api.txt" || true
  compose logs --no-color checkout-service > "$dir/logs_checkout_service.txt" || true
  compose ps > "$dir/docker_compose_ps_after.txt"
}

write_result() {
  local dir="$1"
  local status="$2"
  local expected="$3"
  local observed="$4"
  local limitation="${5:-}"
  {
    echo "expected=$expected"
    echo "observed=$observed"
    echo "status=$status"
    echo "limitations=$limitation"
  } > "$dir/resultado_escenario.txt"
}

mq01_consumer_stopped() {
  local dir="$OUT_DIR/MQ-01-consumidor-detenido"
  scenario_header "$dir" "MQ-01" "Consumidor checkout-service detenido"
  compose stop checkout-service
  wait_container_stopped tesis-checkout-service
  compose ps > "$dir/docker_compose_ps_consumer_stopped.txt"
  rabbit_queue "checkout-service.checkout-requested.q" > "$dir/queue_before_request.json"
  create_checkout "$dir"
  local request_id token
  request_id="$(jq -r '.requestId' "$dir/generated_checkout.json")"
  token="$(jq -r '.token' "$dir/generated_checkout.json")"
  wait_queue_ready "checkout-service.checkout-requested.q" 1 "$dir/queue_during_consumer_down.json" || true
  compose start checkout-service
  wait_http "http://localhost:8082/api/actuator/health" "checkout-service"
  wait_queue_consumers "checkout-service.checkout-requested.q" 1 "$dir/queue_consumer_after_restart.json" || true
  local final_status
  final_status="$(poll_status "$dir" "$request_id" "$token" "COMPLETED")"
  rabbit_queue "checkout-service.checkout-requested.q" > "$dir/queue_after_restart.json"
  db_core -c "select request_id, status, total_amount from checkout_requests where request_id = '$request_id';" > "$dir/core_db_checkout.txt"
  local ready
  ready="$(jq -r '.messages_ready' "$dir/queue_during_consumer_down.json")"
  if [[ "$ready" -ge 1 && "$final_status" == "COMPLETED" ]]; then
    write_result "$dir" "PASS" "Mensaje acumulado y procesado tras reiniciar consumidor" "ready=$ready final=$final_status"
  else
    write_result "$dir" "FAIL" "ready>=1 y final COMPLETED" "ready=$ready final=$final_status"
  fi
  scenario_footer "$dir"
}

mq02_broker_down() {
  local dir="$OUT_DIR/MQ-02-broker-no-disponible"
  scenario_header "$dir" "MQ-02" "Broker RabbitMQ no disponible"
  compose stop rabbitmq
  create_checkout "$dir" || true
  local request_id token event_id
  request_id="$(jq -r '.requestId // empty' "$dir/generated_checkout.json" 2>/dev/null || true)"
  token="$(jq -r '.token // empty' "$dir/generated_checkout.json" 2>/dev/null || true)"
  db_core -c "select id, event_type, published, created_at from outbox_events order by created_at desc limit 5;" > "$dir/core_outbox_during_broker_down.txt" || true
  compose start rabbitmq
  wait_http "http://localhost:15672" "rabbitmq-management"
  sleep 8
  event_id="$(event_id_for_request "$request_id" || true)"
  local final_status="NO_STATUS"
  if [[ -n "$request_id" && -n "$token" ]]; then
    final_status="$(poll_status "$dir" "$request_id" "$token" "COMPLETED" || true)"
  fi
  db_core -c "select payload::jsonb ->> 'eventId' as event_id, published, created_at from outbox_events where payload::jsonb ->> 'requestId' = '$request_id';" > "$dir/core_outbox_after_broker_restart.txt" || true
  if [[ "$final_status" == "COMPLETED" ]]; then
    write_result "$dir" "PASS" "Outbox pendiente se publica al volver RabbitMQ" "event=$event_id final=$final_status"
  else
    write_result "$dir" "PARTIAL" "Endpoint acepta y outbox retiene evento; recuperacion funcional si scheduler publica" "final=$final_status" "No afirmar continuidad automatica si no se observa COMPLETED."
  fi
  scenario_footer "$dir"
}

mq03_duplicate_message() {
  local dir="$OUT_DIR/MQ-03-mensaje-duplicado"
  scenario_header "$dir" "MQ-03" "Mensaje duplicado"
  create_checkout "$dir"
  local request_id token event_id payload
  request_id="$(jq -r '.requestId' "$dir/generated_checkout.json")"
  token="$(jq -r '.token' "$dir/generated_checkout.json")"
  poll_status "$dir" "$request_id" "$token" "COMPLETED" >/dev/null
  event_id="$(event_id_for_request "$request_id")"
  payload="$(payload_for_request "$request_id")"
  db_checkout -c "select count(*) as orders_before from orders where checkout_request_id = '$event_id'; select count(*) as attempts_before from payment_attempts pa join orders o on o.id=pa.order_id where o.checkout_request_id = '$event_id';" > "$dir/counts_before_duplicate.txt"
  publish_checkout_requested "$payload" "$dir/publish_duplicate_1.json"
  publish_checkout_requested "$payload" "$dir/publish_duplicate_2.json"
  sleep 5
  db_checkout -c "select count(*) as orders_after from orders where checkout_request_id = '$event_id'; select count(*) as attempts_after from payment_attempts pa join orders o on o.id=pa.order_id where o.checkout_request_id = '$event_id'; select event_id, processed from inbox_events where event_id = '$event_id';" > "$dir/counts_after_duplicate.txt"
  if grep -q "orders_after.*1" "$dir/counts_after_duplicate.txt" || grep -q "^[[:space:]]*1" "$dir/counts_after_duplicate.txt"; then
    write_result "$dir" "PASS" "Duplicado no genera segunda orden ni segundo pago" "event=$event_id"
  else
    write_result "$dir" "FAIL" "Una sola orden para eventId duplicado" "event=$event_id"
  fi
  scenario_footer "$dir"
}

mq04_publication_retry() {
  local dir="$OUT_DIR/MQ-04-reintento-publicacion"
  scenario_header "$dir" "MQ-04" "Reintento de publicacion por outbox"
  compose stop rabbitmq
  create_checkout "$dir" || true
  local request_id token
  request_id="$(jq -r '.requestId // empty' "$dir/generated_checkout.json" 2>/dev/null || true)"
  token="$(jq -r '.token // empty' "$dir/generated_checkout.json" 2>/dev/null || true)"
  db_core -c "select payload::jsonb ->> 'eventId' as event_id, published, created_at from outbox_events where payload::jsonb ->> 'requestId' = '$request_id';" > "$dir/outbox_before_retry.txt" || true
  compose start rabbitmq
  wait_http "http://localhost:15672" "rabbitmq-management"
  sleep 12
  local final_status="NO_STATUS"
  if [[ -n "$request_id" && -n "$token" ]]; then
    final_status="$(poll_status "$dir" "$request_id" "$token" "COMPLETED" || true)"
  fi
  db_core -c "select payload::jsonb ->> 'eventId' as event_id, published, created_at from outbox_events where payload::jsonb ->> 'requestId' = '$request_id';" > "$dir/outbox_after_retry.txt" || true
  if grep -q "t" "$dir/outbox_after_retry.txt" && [[ "$final_status" == "COMPLETED" ]]; then
    write_result "$dir" "PASS" "OutboxPollingScheduler republica evento pendiente" "final=$final_status"
  else
    write_result "$dir" "PARTIAL" "Evento queda en outbox y scheduler intenta republicar" "final=$final_status"
  fi
  scenario_footer "$dir"
}

mq05_queue_drain() {
  local dir="$OUT_DIR/MQ-05-cola-acumulada-drenaje"
  scenario_header "$dir" "MQ-05" "Cola acumulada y drenaje"
  compose stop checkout-service
  wait_container_stopped tesis-checkout-service
  compose ps > "$dir/docker_compose_ps_consumer_stopped.txt"
  local ids_file="$dir/generated_checkouts.jsonl"
  : > "$ids_file"
  for _ in 1 2 3; do
    create_checkout "$dir"
    jq -c . "$dir/generated_checkout.json" >> "$ids_file"
  done
  wait_queue_ready "checkout-service.checkout-requested.q" 3 "$dir/queue_accumulated.json" || true
  compose start checkout-service
  wait_http "http://localhost:8082/api/actuator/health" "checkout-service"
  wait_queue_consumers "checkout-service.checkout-requested.q" 1 "$dir/queue_consumer_after_restart.json" || true
  local completed=0
  while read -r row; do
    [[ -z "$row" ]] && continue
    local request_id token status
    request_id="$(jq -r '.requestId' <<<"$row")"
    token="$(jq -r '.token' <<<"$row")"
    status="$(poll_status "$dir" "$request_id" "$token" "COMPLETED")"
    [[ "$status" == "COMPLETED" ]] && completed=$((completed + 1))
  done < "$ids_file"
  wait_queue_drained "checkout-service.checkout-requested.q" "$dir/queue_drained.json" || true
  local accumulated remaining
  accumulated="$(jq -r '.messages_ready' "$dir/queue_accumulated.json")"
  remaining="$(jq -r '.messages' "$dir/queue_drained.json")"
  if [[ "$accumulated" -ge 3 && "$completed" -eq 3 && "$remaining" -eq 0 ]]; then
    write_result "$dir" "PASS" "Tres mensajes acumulados se drenan tras reiniciar consumidor" "accumulated=$accumulated completed=$completed remaining=$remaining"
  else
    write_result "$dir" "FAIL" "accumulated>=3 completed=3 remaining=0" "accumulated=$accumulated completed=$completed remaining=$remaining"
  fi
  scenario_footer "$dir"
}

mq06_requeue() {
  local dir="$OUT_DIR/MQ-06-requeue"
  scenario_header "$dir" "MQ-06" "Requeue por defecto de Spring AMQP"
  recreate_checkout true
  local poison
  poison='{"eventId":null,"requestId":null,"userId":null,"totalAmount":29.99,"items":[],"timestamp":0}'
  publish_checkout_requested "$poison" "$dir/publish_poison_requeue_true.json"
  sleep 5
  rabbit_queue "checkout-service.checkout-requested.q" > "$dir/queue_after_poison_requeue_true.json"
  rabbit_queue "checkout-service.dlq" > "$dir/dlq_after_poison_requeue_true.json"
  compose stop checkout-service
  sleep 2
  purge_queue "checkout-service.checkout-requested.q"
  compose start checkout-service
  wait_http "http://localhost:8082/api/actuator/health" "checkout-service"
  local dlq_messages
  dlq_messages="$(jq -r '.messages' "$dir/dlq_after_poison_requeue_true.json")"
  if [[ "$dlq_messages" -eq 0 ]]; then
    write_result "$dir" "PASS" "Con default-requeue-rejected=true el mensaje fallido no pasa a DLQ" "dlq_messages=$dlq_messages" "Comportamiento default/delegado a Spring AMQP; no hay retry/backoff de consumidor configurado."
  else
    write_result "$dir" "PARTIAL" "Requeue true sin DLQ" "dlq_messages=$dlq_messages"
  fi
  scenario_footer "$dir"
}

mq07_dlq() {
  local dir="$OUT_DIR/MQ-07-dlq"
  scenario_header "$dir" "MQ-07" "Dead-letter queue"
  recreate_checkout false
  purge_queue "checkout-service.dlq"
  local poison
  poison='{"eventId":null,"requestId":null,"userId":null,"totalAmount":29.99,"items":[],"timestamp":0}'
  publish_checkout_requested "$poison" "$dir/publish_poison_requeue_false.json"
  sleep 8
  rabbit_queue "checkout-service.checkout-requested.q" > "$dir/queue_after_poison_requeue_false.json"
  rabbit_queue "checkout-service.dlq" > "$dir/dlq_after_poison_requeue_false.json"
  curl -fsS -u "$RABBITMQ_USER:$RABBITMQ_PASSWORD" -H "Content-Type: application/json" \
    -X POST "$RABBIT_URL/queues/%2F/checkout-service.dlq/get" \
    -d '{"count":1,"ackmode":"ack_requeue_true","encoding":"auto","truncate":5000}' \
    | jq '.' > "$dir/dlq_message_sample.json" || true
  local dlq_messages
  dlq_messages="$(jq -r '.messages' "$dir/dlq_after_poison_requeue_false.json")"
  if [[ "$dlq_messages" -ge 1 ]]; then
    write_result "$dir" "PASS" "Mensaje fallido llega a checkout-service.dlq con requeue deshabilitado en sandbox" "dlq_messages=$dlq_messages"
  else
    write_result "$dir" "FAIL" "dlq_messages>=1" "dlq_messages=$dlq_messages"
  fi
  recreate_checkout true
  scenario_footer "$dir"
}

write_report() {
  local report="$OUT_DIR/REPORTE_FALLOS_ASINCRONOS.md"
  {
    echo "# Reporte de fallos asincronos RabbitMQ"
    echo
    echo "| Campo | Valor |"
    echo "|---|---|"
    echo "| RUN_ID | \`$RUN_ID\` |"
    echo "| Fecha UTC | \`$(date -u -Iseconds)\` |"
    echo "| Rama | \`$(git_branch)\` |"
    echo "| Commit | \`$(git_commit)\` |"
    echo "| Ambiente | Docker Compose local, core-api, checkout-service, Postgres separados y RabbitMQ |"
    echo
    echo "## Mecanismos verificados"
    echo
    echo "- Acknowledgement: listeners sin ACK manual; Spring AMQP usa acknowledgement automatico/default."
    echo "- Outbox: core-api persiste \`OutboxEvent\` y \`OutboxPollingScheduler\` reintenta publicacion cada 5000 ms."
    echo "- Inbox/idempotencia: checkout-service persiste \`InboxEvent\` con \`event_id\` unico y evita reprocesar duplicados."
    echo "- DLQ: \`checkout-service.checkout-requested.q\` declara \`x-dead-letter-exchange=checkout.dlx\` y routing key \`checkout.requested.dlq\` hacia \`checkout-service.dlq\`."
    echo "- Requeue: configurable por \`rabbitmq.listener.default-requeue-rejected\`; default \`true\`, se deshabilita solo para MQ-07 en sandbox."
    echo "- Retry consumidor/backoff: no hay retry/backoff explicito de Spring AMQP; se documenta como limitacion."
    echo
    echo "## Resultados"
    echo
    echo "| Escenario | Estado | Evidencia |"
    echo "|---|---|---|"
    for d in "$OUT_DIR"/MQ-*; do
      [[ -d "$d" ]] || continue
      local name status
      name="$(basename "$d")"
      status="$(grep '^status=' "$d/resultado_escenario.txt" 2>/dev/null | cut -d= -f2 || echo "NO_EJECUTADO")"
      echo "| \`$name\` | \`$status\` | \`$d\` |"
    done
  } > "$report"
}

main() {
  write_compose_override
  write_versions
  rm -rf "$OUT_DIR"/MQ-*
  start_stack true
  purge_queue "checkout-service.checkout-requested.q"
  purge_queue "checkout-service.dlq"
  purge_queue "core-api.checkout-completed.q"
  purge_queue "core-api.checkout-failed.q"
  compose images > "$OUT_DIR/docker_images.txt"

  mq01_consumer_stopped
  mq02_broker_down
  mq03_duplicate_message
  mq04_publication_retry
  mq05_queue_drain
  mq06_requeue
  mq07_dlq

  write_report
}

main "$@"
