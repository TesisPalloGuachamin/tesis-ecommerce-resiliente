# DRP Azure 01 Evidence

RUN_ID: `drp-azure-20260518T233644Z`

This folder contains commit-friendly evidence for the Azure DRP scenario:

- Terraform plan/apply evidence for blocked regions and successful `canadacentral` recovery.
- Restore, health checks, smoke checkout and Prometheus/Grafana observations.
- RTO/RPO/MTTR metrics in `drp-azure-metrics.json`.
- Cleanup evidence showing Azure resources were destroyed after evidence capture.

Large runtime artifacts, concrete `.tfvars`, raw dumps and local operator details are intentionally kept out of git under `_local-not-for-commit/`.
