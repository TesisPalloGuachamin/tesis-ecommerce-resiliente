# DEVOPS-DRP-02 Backup And RPO Matrix

This matrix separates observed recovery evidence from designed recovery targets. Do not
report designed targets as observed metrics.

| Asset | Recovery role | Backup artifact | Minimum frequency | Designed RPO | Observed in previous F6 | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| `core_db` PostgreSQL | Users, products, carts, checkout requests | `pg_dump` custom or SQL dump | Before demo, before destructive tests, and after relevant data changes | Last successful backup | Yes, in F6/DRP-01 only | Use restore validation before declaring successful DRP |
| `checkout_db` PostgreSQL | Orders, payment attempts, inbox/idempotency | `pg_dump` custom or SQL dump | Before demo, before destructive tests, and after relevant checkout tests | Last successful backup | Yes, in F6/DRP-01 only | Required to preserve simulated payment/order evidence |
| RabbitMQ definitions | Exchanges, queues, bindings | definitions export or compose-declared topology | On topology change | Last exported definitions or redeploy from code | Partially covered by compose/Rabbit config | Current topology is recreated by services and config |
| Docker Compose stack | Runtime topology | `deploy/compose` directory and env templates | On deployment change | Last committed compose bundle | Yes, via reconstruction path | Do not store secrets in committed `.env` |
| Docker images | Runtime binaries | Registry tags or rebuilt images | On service release | Last published/rebuildable image | Yes, via existing images/build path | AWS/Azure recovery needs image availability |
| Terraform state | Infrastructure identity | Remote backend or state backup | After every apply | Last state snapshot | Existing local state only | AWS Academy resets can stale local state |
| Mobile `.env.local` | Current API endpoint | Local operator configuration | Every lab restart/IP change | Immediate manual update | Not a recoverable backend asset | Must not hardcode AWS IP in source |
| Evidence files | Thesis validation trail | `evidencias/*` | After each validation block | Last generated evidence | Yes for completed blocks | Evidence is not an operational dependency |

RPO statement for DEVOPS-DRP-02:

- Observed RPO remains only the RPO already measured during DRP-01/F6.
- AWS alternate-region and Azure RPO are designed as "last successful backup" until a
  measured drill is executed in those targets.
- Fallback own-infrastructure RPO is documentary/manual and must not be reported as observed.
