#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/lib.sh"
cd "$ROOT_DIR"
./mvnw -pl consumer-order-java test
broker publish consumer-order-java/target/pacts \
  --consumer-app-version "$(version)" \
  --branch "$(branch)"
