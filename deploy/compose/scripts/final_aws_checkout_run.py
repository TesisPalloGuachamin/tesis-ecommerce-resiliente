#!/usr/bin/env python3
"""Generate a traceable checkout run for the final AWS drill.

The script uses only the public core-api REST contract:
register -> products -> cart -> checkout -> checkout status polling.
It does not require direct database access and does not create new backend APIs.
"""

from __future__ import annotations

import argparse
import json
import sys
import time
from datetime import datetime, timezone
from pathlib import Path
from typing import Any
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen


def utc_now() -> str:
    return datetime.now(timezone.utc).isoformat()


def request_json(
    method: str,
    base_url: str,
    path: str,
    body: dict[str, Any] | None = None,
    token: str | None = None,
    timeout: int = 20,
) -> tuple[int, dict[str, Any] | list[Any] | str, float]:
    url = f"{base_url.rstrip('/')}{path}"
    data = None
    headers = {"Accept": "application/json"}
    if body is not None:
        data = json.dumps(body).encode("utf-8")
        headers["Content-Type"] = "application/json"
    if token:
        headers["Authorization"] = f"Bearer {token}"

    started = time.monotonic()
    req = Request(url, data=data, headers=headers, method=method)
    try:
        with urlopen(req, timeout=timeout) as response:
            raw = response.read().decode("utf-8")
            elapsed = time.monotonic() - started
            if not raw:
                return response.status, {}, elapsed
            try:
                return response.status, json.loads(raw), elapsed
            except json.JSONDecodeError:
                return response.status, raw, elapsed
    except HTTPError as exc:
        elapsed = time.monotonic() - started
        raw = exc.read().decode("utf-8")
        try:
            payload: dict[str, Any] | list[Any] | str = json.loads(raw)
        except json.JSONDecodeError:
            payload = raw
        return exc.code, payload, elapsed
    except URLError as exc:
        elapsed = time.monotonic() - started
        return 0, {"error": str(exc.reason)}, elapsed


def require_dict(payload: Any, context: str) -> dict[str, Any]:
    if not isinstance(payload, dict):
        raise RuntimeError(f"{context} did not return a JSON object: {payload!r}")
    return payload


def run_purchase(args: argparse.Namespace, index: int, product_id: str) -> dict[str, Any]:
    email = f"{args.run_id}-{index:04d}@example.com"
    password = args.password
    event: dict[str, Any] = {
        "runId": args.run_id,
        "index": index,
        "email": email,
        "startedAt": utc_now(),
        "status": "STARTED",
        "steps": [],
    }

    def step(name: str, method: str, path: str, body: dict[str, Any] | None = None, token: str | None = None) -> Any:
        status, payload, elapsed = request_json(method, args.api_base_url, path, body=body, token=token, timeout=args.http_timeout)
        event["steps"].append({
            "name": name,
            "httpStatus": status,
            "elapsedMs": round(elapsed * 1000, 2),
        })
        if status < 200 or status >= 300:
            raise RuntimeError(f"{name} failed with HTTP {status}: {payload!r}")
        return payload

    try:
        auth_payload = step(
            "register",
            "POST",
            "/api/v1/auth/register",
            {"email": email, "password": password, "name": f"Final AWS Drill {index:04d}"},
        )
        token = require_dict(auth_payload, "register").get("token")
        if not token:
            raise RuntimeError("register did not return a token")

        cart_payload = step("get_cart", "GET", "/api/v1/cart", token=token)
        cart_id = require_dict(cart_payload, "get_cart").get("id")
        if not cart_id:
            raise RuntimeError("get_cart did not return cart id")

        cart_payload = step(
            "add_cart_item",
            "POST",
            "/api/v1/cart/items",
            {"productId": product_id, "quantity": args.quantity},
            token=token,
        )
        cart_id = require_dict(cart_payload, "add_cart_item").get("id") or cart_id

        checkout_payload = step(
            "create_checkout",
            "POST",
            "/api/v1/checkout",
            {"cartId": cart_id},
            token=token,
        )
        checkout_id = require_dict(checkout_payload, "create_checkout").get("requestId")
        if not checkout_id:
            raise RuntimeError("create_checkout did not return requestId")

        event["checkoutRequestId"] = checkout_id
        deadline = time.monotonic() + args.poll_timeout_seconds
        final_status = None
        while time.monotonic() < deadline:
            status_payload = step("get_checkout_status", "GET", f"/api/v1/checkout/{checkout_id}", token=token)
            final_status = require_dict(status_payload, "get_checkout_status").get("status")
            if final_status in {"COMPLETED", "FAILED", "CANCELLED"}:
                break
            time.sleep(args.poll_interval_seconds)

        event["finalCheckoutStatus"] = final_status
        event["status"] = "COMPLETED" if final_status == "COMPLETED" else "FAILED"
    except Exception as exc:  # noqa: BLE001 - this is a drill runner, every failure must be recorded.
        event["status"] = "FAILED"
        event["error"] = str(exc)
    finally:
        event["finishedAt"] = utc_now()

    return event


def write_json(path: Path, payload: Any) -> None:
    path.write_text(json.dumps(payload, indent=2, sort_keys=True) + "\n", encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser(description="Run traceable checkout purchases against core-api.")
    parser.add_argument("--api-base-url", default="http://localhost:8080")
    parser.add_argument("--run-id", default=f"final-aws-{datetime.now(timezone.utc).strftime('%Y%m%dT%H%M%SZ')}")
    parser.add_argument("--purchases", type=int, default=1000)
    parser.add_argument("--quantity", type=int, default=1)
    parser.add_argument("--password", default="FinalAwsDrill2026!")
    parser.add_argument("--evidence-dir", default=None)
    parser.add_argument("--poll-timeout-seconds", type=int, default=45)
    parser.add_argument("--poll-interval-seconds", type=float, default=1.0)
    parser.add_argument("--http-timeout", type=int, default=20)
    args = parser.parse_args()

    evidence_dir = Path(args.evidence_dir or f"evidencias/final-aws-drill/{args.run_id}")
    evidence_dir.mkdir(parents=True, exist_ok=True)
    events_path = evidence_dir / "purchase-events.jsonl"

    started_at = utc_now()
    catalog_email = f"{args.run_id}-catalog@example.com"
    catalog_status, catalog_auth, catalog_elapsed = request_json(
        "POST",
        args.api_base_url,
        "/api/v1/auth/register",
        body={"email": catalog_email, "password": args.password, "name": "Final AWS Drill Catalog Probe"},
        timeout=args.http_timeout,
    )
    if catalog_status < 200 or catalog_status >= 300:
        write_json(evidence_dir / "failed-catalog-auth-response.json", {
            "httpStatus": catalog_status,
            "payload": catalog_auth,
            "elapsedMs": round(catalog_elapsed * 1000, 2),
        })
        print(f"Cannot start drill: catalog auth returned HTTP {catalog_status}", file=sys.stderr)
        return 2

    catalog_token = require_dict(catalog_auth, "catalog auth").get("token")
    if not catalog_token:
        print("Cannot start drill: catalog auth did not return a token", file=sys.stderr)
        return 2

    products_status, products_payload, products_elapsed = request_json(
        "GET",
        args.api_base_url,
        "/api/v1/products",
        token=catalog_token,
        timeout=args.http_timeout,
    )
    if products_status != 200 or not isinstance(products_payload, list) or not products_payload:
        write_json(evidence_dir / "failed-products-response.json", {
            "httpStatus": products_status,
            "payload": products_payload,
            "elapsedMs": round(products_elapsed * 1000, 2),
        })
        print(f"Cannot start drill: products endpoint returned HTTP {products_status}", file=sys.stderr)
        return 2

    product_id = products_payload[0].get("id")
    if not product_id:
        print("Cannot start drill: first product has no id", file=sys.stderr)
        return 2

    completed = 0
    failed = 0
    checkout_ids: list[str] = []
    with events_path.open("a", encoding="utf-8") as events_file:
        for index in range(1, args.purchases + 1):
            event = run_purchase(args, index, product_id)
            events_file.write(json.dumps(event, sort_keys=True) + "\n")
            events_file.flush()
            if event["status"] == "COMPLETED":
                completed += 1
                if "checkoutRequestId" in event:
                    checkout_ids.append(event["checkoutRequestId"])
            else:
                failed += 1
            print(f"{index}/{args.purchases} {event['status']} {event.get('checkoutRequestId', '-')}")

    finished_at = utc_now()
    summary = {
        "runId": args.run_id,
        "apiBaseUrl": args.api_base_url,
        "startedAt": started_at,
        "finishedAt": finished_at,
        "requestedPurchases": args.purchases,
        "completedPurchases": completed,
        "failedPurchases": failed,
        "catalogProbeEmail": catalog_email,
        "productId": product_id,
        "eventsFile": str(events_path),
        "checkoutIdsFile": str(evidence_dir / "checkout-ids.txt"),
    }
    write_json(evidence_dir / "run-summary.json", summary)
    (evidence_dir / "checkout-ids.txt").write_text("\n".join(checkout_ids) + ("\n" if checkout_ids else ""), encoding="utf-8")

    return 0 if failed == 0 and completed == args.purchases else 1


if __name__ == "__main__":
    raise SystemExit(main())
