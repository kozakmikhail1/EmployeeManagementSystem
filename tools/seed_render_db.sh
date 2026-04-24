#!/usr/bin/env bash
set -euo pipefail

if [[ -z "${DATABASE_URL:-}" ]]; then
  echo "DATABASE_URL is not set"
  echo "Example: export DATABASE_URL='postgresql://user:pass@host:5432/dbname?sslmode=require'"
  exit 1
fi

SCRIPT_PATH="src/main/resources/db/reset_seed_employee_db.sql"
if [[ ! -f "$SCRIPT_PATH" ]]; then
  echo "Seed script not found: $SCRIPT_PATH"
  exit 1
fi

echo "Seeding database using $SCRIPT_PATH ..."
psql "$DATABASE_URL" -v ON_ERROR_STOP=1 -f "$SCRIPT_PATH"
echo "Done."
