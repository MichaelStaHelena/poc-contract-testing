#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/lib.sh"
PROJECT_DIR="$ROOT_DIR/consumer-notification-python"
if [ ! -x "$PROJECT_DIR/.venv/bin/python" ]; then
  python3 -m venv "$PROJECT_DIR/.venv"
  "$PROJECT_DIR/.venv/bin/python" -m pip install --upgrade pip
  "$PROJECT_DIR/.venv/bin/python" -m pip install -e "$PROJECT_DIR[test]"
fi
"$PROJECT_DIR/.venv/bin/python" -m pytest "$PROJECT_DIR/tests"
broker publish consumer-notification-python/pacts \
  --consumer-app-version "$(version)" \
  --branch "$(branch)"
