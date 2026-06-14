import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

const targetIterations = Number(__ENV.TARGET_ITERATIONS || '200');
const vus = Number(__ENV.VUS || '10');
const runId = __ENV.RUN_ID || 'cap4-load-local';
const scenarioName = __ENV.SCENARIO_NAME || 'CARGA-200';
const baseUrl = (__ENV.BASE_URL || 'http://AWS_PRINCIPAL_HOST:8080').replace(/\/$/, '');
const password = __ENV.TEST_PASSWORD || 'Cap4LoadTest123!';
const pollAttempts = Number(__ENV.POLL_ATTEMPTS || '12');
const pollDelaySeconds = Number(__ENV.POLL_DELAY_SECONDS || '0.5');

export const options = {
  scenarios: {
    checkout_load: {
      executor: 'shared-iterations',
      vus,
      iterations: targetIterations,
      maxDuration: __ENV.MAX_DURATION || '30m',
    },
  },
  summaryTrendStats: ['avg', 'min', 'med', 'p(90)', 'p(95)', 'p(99)', 'max'],
  thresholds: {
    http_req_failed: ['rate<=0.01'],
    functional_checkout_error_rate: ['rate<=0.05'],
  },
};

const checkoutsAttempted = new Counter('checkouts_attempted');
const checkoutsCompleted = new Counter('checkouts_completed');
const functionalCheckoutErrorRate = new Rate('functional_checkout_error_rate');
const checkoutFinalLatency = new Trend('checkout_final_latency_ms', true);

function jsonHeaders(token) {
  const headers = { 'Content-Type': 'application/json' };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  return headers;
}

function parseJson(response) {
  try {
    return response.json();
  } catch (_) {
    return null;
  }
}

export function setup() {
  const email = `${runId.toLowerCase()}-setup-${Date.now()}@example.com`;
  const registerRes = http.post(`${baseUrl}/api/v1/auth/register`, JSON.stringify({
    email,
    password,
    name: `${runId} Setup User`,
  }), { headers: jsonHeaders() });

  check(registerRes, {
    'setup register status 201': (r) => r.status === 201,
  });

  const registerBody = parseJson(registerRes);
  const token = registerBody && registerBody.token;
  const productsRes = http.get(`${baseUrl}/api/v1/products`, { headers: jsonHeaders(token) });

  check(productsRes, {
    'setup products status 200': (r) => r.status === 200,
  });

  const products = parseJson(productsRes) || [];
  const selected = products.find((product) => product && product.active) || products[0];
  if (!selected || !selected.id) {
    throw new Error('No active product was available for the load scenario');
  }

  return {
    productId: selected.id,
    productName: selected.name || selected.sku || 'selected-product',
    productPrice: selected.price,
  };
}

export default function (data) {
  const started = Date.now();
  const unique = `${runId.toLowerCase()}-${scenarioName.toLowerCase()}-${__VU}-${__ITER}-${Date.now()}`;
  let checkoutId = null;
  let finalStatus = null;
  let token = null;
  let cartId = null;
  let failed = false;

  const registerRes = http.post(`${baseUrl}/api/v1/auth/register`, JSON.stringify({
    email: `${unique}@example.com`,
    password,
    name: `${scenarioName} User ${__VU}-${__ITER}`,
  }), { headers: jsonHeaders() });

  const registerOk = check(registerRes, {
    'register status 201': (r) => r.status === 201,
  });
  const registerBody = parseJson(registerRes);
  token = registerBody && registerBody.token;
  if (!registerOk || !token) {
    failed = true;
  }

  if (!failed) {
    const productsRes = http.get(`${baseUrl}/api/v1/products`, { headers: jsonHeaders(token) });
    const productsOk = check(productsRes, {
      'products status 200': (r) => r.status === 200,
    });
    if (!productsOk) {
      failed = true;
    }
  }

  if (!failed) {
    const cartRes = http.post(`${baseUrl}/api/v1/cart/items`, JSON.stringify({
      productId: data.productId,
      quantity: 1,
    }), { headers: jsonHeaders(token) });

    const cartOk = check(cartRes, {
      'cart add status 201': (r) => r.status === 201,
    });
    const cartBody = parseJson(cartRes);
    cartId = cartBody && cartBody.id;
    if (!cartOk || !cartId) {
      failed = true;
    }
  }

  if (!failed) {
    checkoutsAttempted.add(1);
    const checkoutRes = http.post(`${baseUrl}/api/v1/checkout`, JSON.stringify({
      cartId,
    }), { headers: jsonHeaders(token) });

    const checkoutOk = check(checkoutRes, {
      'checkout status 202': (r) => r.status === 202,
    });
    const checkoutBody = parseJson(checkoutRes);
    checkoutId = checkoutBody && (checkoutBody.requestId || checkoutBody.checkoutId || checkoutBody.id);
    if (!checkoutOk || !checkoutId) {
      failed = true;
    }
  }

  if (!failed) {
    for (let attempt = 1; attempt <= pollAttempts; attempt += 1) {
      const statusRes = http.get(`${baseUrl}/api/v1/checkout/${checkoutId}`, { headers: jsonHeaders(token) });
      const statusOk = check(statusRes, {
        'checkout status poll 200': (r) => r.status === 200,
      });
      const statusBody = parseJson(statusRes);
      finalStatus = statusBody && statusBody.status;
      if (!statusOk) {
        failed = true;
        break;
      }
      if (finalStatus === 'COMPLETED') {
        break;
      }
      sleep(pollDelaySeconds);
    }
  }

  const completed = !failed && finalStatus === 'COMPLETED';
  if (completed) {
    checkoutsCompleted.add(1);
    checkoutFinalLatency.add(Date.now() - started);
  }
  functionalCheckoutErrorRate.add(!completed);

  console.log(`TRACE|${JSON.stringify({
    scenario: scenarioName,
    checkoutId,
    status: finalStatus || 'NOT_COMPLETED',
    completed,
  })}`);
}
