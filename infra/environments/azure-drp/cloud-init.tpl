#cloud-config
package_update: true
packages:
  - ca-certificates
  - curl
  - gnupg
  - lsb-release

write_files:
  - path: /opt/tesis-ecommerce/README-DRP.txt
    permissions: "0644"
    content: |
      Azure DRP recovery host prepared by cloud-init.
      Next manual steps:
      1. Copy deploy/compose assets to /opt/tesis-ecommerce/deploy/compose.
      2. Restore database backups into the compose Postgres services.
      3. Run docker compose -f docker-compose.ec2.yml up -d.
      4. Validate health endpoints and checkout flow.

runcmd:
  - install -m 0755 -d /etc/apt/keyrings
  - curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
  - chmod a+r /etc/apt/keyrings/docker.gpg
  - echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo $VERSION_CODENAME) stable" > /etc/apt/sources.list.d/docker.list
  - apt-get update -y
  - apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
  - usermod -aG docker ${admin_username}
  - mkdir -p /opt/tesis-ecommerce/deploy/compose /opt/tesis-ecommerce/backups
  - systemctl enable docker
  - systemctl start docker
