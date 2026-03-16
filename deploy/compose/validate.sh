#!/bin/bash

# DevOps Local Validation Script
# Valida que la base DevOps local esté lista para levantar

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

echo "=================================================="
echo "DevOps Local Validation Script"
echo "=================================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

check_file() {
  local file="$1"
  local description="$2"

  if [ -f "$file" ]; then
    echo -e "${GREEN}✓${NC} $description"
    return 0
  else
    echo -e "${RED}✗${NC} $description - NOT FOUND: $file"
    return 1
  fi
}

check_dir() {
  local dir="$1"
  local description="$2"

  if [ -d "$dir" ]; then
    echo -e "${GREEN}✓${NC} $description"
    return 0
  else
    echo -e "${RED}✗${NC} $description - NOT FOUND: $dir"
    return 1
  fi
}

echo "1. Checking DevOps Structure..."
echo "--------------------------------"
check_dir "$PROJECT_ROOT/deploy/compose" "deploy/compose directory"
check_dir "$PROJECT_ROOT/deploy/compose/prometheus" "prometheus configuration directory"
check_dir "$PROJECT_ROOT/deploy/compose/grafana/provisioning" "grafana provisioning directory"
check_file "$PROJECT_ROOT/deploy/compose/.env.example" ".env.example file"
check_file "$PROJECT_ROOT/deploy/compose/docker-compose.dev.yml" "docker-compose.dev.yml file"
check_file "$PROJECT_ROOT/deploy/compose/prometheus/prometheus.yml" "prometheus.yml file"

echo ""
echo "2. Checking Backend Application Files..."
echo "----------------------------------------"
check_file "$PROJECT_ROOT/apps/core-api/pom.xml" "core-api/pom.xml"
check_file "$PROJECT_ROOT/apps/core-api/Dockerfile" "core-api/Dockerfile"
check_file "$PROJECT_ROOT/apps/core-api/src/main/resources/application.yml" "core-api/application.yml"
check_file "$PROJECT_ROOT/apps/checkout-service/pom.xml" "checkout-service/pom.xml"
check_file "$PROJECT_ROOT/apps/checkout-service/Dockerfile" "checkout-service/Dockerfile"
check_file "$PROJECT_ROOT/apps/checkout-service/src/main/resources/application.yml" "checkout-service/application.yml"

echo ""
echo "3. Checking .dockerignore Files..."
echo "----------------------------------"
check_file "$PROJECT_ROOT/apps/core-api/.dockerignore" "core-api/.dockerignore"
check_file "$PROJECT_ROOT/apps/checkout-service/.dockerignore" "checkout-service/.dockerignore"

echo ""
echo "4. Pre-Execution Checklist..."
echo "----------------------------"

# Check if Docker is available
if command -v docker &> /dev/null; then
  echo -e "${GREEN}✓${NC} Docker is installed"
else
  echo -e "${YELLOW}⚠${NC} Docker is NOT installed (required to run compose)"
fi

# Check if Docker Compose V2 is available
if docker compose version &> /dev/null 2>&1; then
  echo -e "${GREEN}✓${NC} Docker Compose V2 is available"
elif command -v docker-compose &> /dev/null; then
  echo -e "${GREEN}✓${NC} Docker Compose (standalone) is available"
else
  echo -e "${YELLOW}⚠${NC} Docker Compose is NOT available (required to run compose)"
fi

# Check if Docker daemon is running
if docker ps &> /dev/null; then
  echo -e "${GREEN}✓${NC} Docker daemon is running"
else
  echo -e "${RED}✗${NC} Docker daemon is NOT running"
fi

# Check for .env file
if [ -f "$SCRIPT_DIR/.env" ]; then
  echo -e "${GREEN}✓${NC} .env file exists"
else
  echo -e "${YELLOW}⚠${NC} .env file NOT found (will use defaults from .env.example)"
fi

echo ""
echo "=================================================="
echo "Next Steps:"
echo "=================================================="
echo ""
echo "1. If you haven't already, copy the template:"
echo "   cp deploy/compose/.env.example deploy/compose/.env"
echo ""
echo "2. Start the DevOps stack:"
echo "   docker compose -f deploy/compose/docker-compose.dev.yml up -d"
echo ""
echo "3. Verify services are running:"
echo "   docker compose -f deploy/compose/docker-compose.dev.yml ps"
echo ""
echo "4. Test endpoints:"
echo "   - Core API health: curl http://localhost:8080/actuator/health"
echo "   - Checkout health: curl http://localhost:8082/actuator/health"
echo "   - RabbitMQ UI: http://localhost:15672 (guest/guest)"
echo "   - Prometheus: http://localhost:9090"
echo "   - Grafana: http://localhost:3000 (admin/admin123)"
echo ""
echo "5. For Prometheus metrics support, see:"
echo "   deploy/compose/PROMETHEUS_SETUP.md"
echo ""
echo "=================================================="

