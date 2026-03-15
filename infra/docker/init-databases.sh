#!/bin/bash
set -e

echo "Creating additional databases..."

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE DATABASE checkout_db;
    GRANT ALL PRIVILEGES ON DATABASE checkout_db TO $POSTGRES_USER;
EOSQL

echo "Databases created successfully."

