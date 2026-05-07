# DEVOPS-DRP-02 Expanded DRP Runbook

## Scope

DEVOPS-DRP-02 extends the already observed AWS DRP/F6 path with a staged recovery strategy:

1. AWS alternate-region recovery.
2. Azure recovery as the primary alternative cloud design.
3. Minimal fallback to own/degraded infrastructure as a documentary and technical fallback.

This is not an active-active design, not productive multicloud, and not a new backend.

## Classification

| Layer | Status | What can be claimed |
| --- | --- | --- |
| AWS same-region rebuild from previous F6 | Observed | Use only the already measured RTO/RPO/availability from DRP-01/F6 evidence |
| AWS alternate-region recovery | Designed and runbook-ready | Infrastructure is region-parameterized; no new RTO/RPO observed in DEVOPS-DRP-02 |
| Azure recovery | Designed and Terraform-validated | Azure VM recovery target is syntactically valid; no Azure apply/restore metrics observed |
| Own/degraded infrastructure fallback | Documentary fallback | Manual low-cost recovery path; no cloud SLA or measured metrics |
| Google Cloud | Documented alternative only | No implementation in this block |

## Current AWS State Audit

- Current reachable AWS Academy host observed during this block: `44.195.21.215`.
- `core-api` health endpoint: `http://44.195.21.215:8080/actuator/health`.
- `checkout-service` health endpoint: `http://44.195.21.215:8082/api/actuator/health`.
- Local Terraform state may become stale after AWS Academy lab restarts.
- AWS CLI credentials can expire independently from the running EC2 environment.

## AWS Alternate-Region Runbook

Purpose: recover the same Docker Compose demo stack in a different AWS region after a regional
or account/lab reset event.

Prerequisites:

- AWS Academy credentials refreshed.
- Target region has an EC2 key pair matching `key_name`.
- Operator public IP known.
- Database backups available for `core_db` and `checkout_db`.
- Compose bundle available under `deploy/compose`.
- Docker images available from registry or rebuilt locally.

Execution:

1. Copy `infra/environments/demo/terraform.alternate-region.tfvars.example` to a temporary tfvars file.
2. Set `aws_region` to the target region.
3. Ensure `key_name` exists in that region.
4. Run:

   ```bash
   terraform -chdir=infra/environments/demo init
   terraform -chdir=infra/environments/demo plan -var-file=terraform.alternate-region.tfvars
   terraform -chdir=infra/environments/demo apply -var-file=terraform.alternate-region.tfvars
   ```

5. SSH into the new EC2.
6. Start the stack:

   ```bash
   cd ~/tesis-ecommerce/deploy/compose
   docker compose -f docker-compose.ec2.yml up -d
   ```

7. Restore `core_db` and `checkout_db` from the selected backups.
8. Validate:

   ```bash
   curl http://NEW_AWS_IP:8080/actuator/health
   curl http://NEW_AWS_IP:8082/api/actuator/health
   ```

9. Execute the mobile/API checkout smoke flow.
10. Record new evidence. Only then may new RTO/RPO be called observed.

Rollback:

- Destroy only the alternate-region stack if the drill fails:

  ```bash
  terraform -chdir=infra/environments/demo destroy -var-file=terraform.alternate-region.tfvars
  ```

## Azure Recovery Runbook

Purpose: recover the Docker Compose demo stack on Azure as the primary alternative cloud design.

Prerequisites:

- Azure subscription and credentials.
- Terraform Azure provider initialized.
- Quota for `Standard_B2s` or chosen VM size.
- Valid SSH public key.
- Operator CIDR.
- Compose bundle and database backups.

Designed infrastructure:

- Resource group.
- Virtual network/subnet.
- Static public IP.
- Network security group with only required demo ports.
- Linux VM with Docker and Compose installed by cloud-init.

Validation without deployment:

```bash
terraform -chdir=infra/environments/azure-drp init -backend=false
terraform -chdir=infra/environments/azure-drp validate
```

Execution when credentials/quota are available:

1. Copy `infra/environments/azure-drp/terraform.tfvars.example` to `terraform.tfvars`.
2. Fill `admin_ssh_public_key` and `operator_cidr`.
3. Run:

   ```bash
   terraform -chdir=infra/environments/azure-drp plan
   terraform -chdir=infra/environments/azure-drp apply
   ```

4. Copy `deploy/compose` to `/opt/tesis-ecommerce/deploy/compose` on the VM.
5. Copy database backups to `/opt/tesis-ecommerce/backups`.
6. Start the compose stack and restore databases.
7. Validate health endpoints and checkout flow.
8. Record observed RTO/RPO only after this drill is performed.

Rollback:

```bash
terraform -chdir=infra/environments/azure-drp destroy
```

## Own/Degraded Infrastructure Fallback

Purpose: preserve a minimum demonstration path if cloud accounts are unavailable.

Minimum path:

1. Provision a single Linux host with Docker and Docker Compose.
2. Copy `deploy/compose`.
3. Restore `core_db` and `checkout_db` backups.
4. Expose the same demo ports only to the operator network.
5. Update mobile `.env.local` with the host IP.
6. Demonstrate login, catalog, cart, checkout and confirmation.

Limitations:

- No cloud SLA.
- No measured RTO/RPO unless a real drill is timed.
- Suitable only as degraded academic fallback.

## What Must Not Be Claimed

- Do not claim active-active.
- Do not claim productive multicloud.
- Do not claim Azure RTO/RPO as observed until Azure apply and restore are executed.
- Do not claim Google Cloud implementation in DEVOPS-DRP-02.
- Do not replace the observed F6 metrics with designed targets.
