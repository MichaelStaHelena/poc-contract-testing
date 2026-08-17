#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/lib.sh"
require_clean_commit
cd "$ROOT_DIR"
./mvnw -pl provider-user-java test \
  -Dpactbroker.url="$PACT_BROKER_BASE_URL" \
  -Dpactbroker.auth.username="$PACT_BROKER_USERNAME" \
  -Dpactbroker.auth.password="$PACT_BROKER_PASSWORD" \
  -Dpact.pactbroker.httpclient.usePreemptiveAuthentication=true \
  -Dpact.provider.version="$(version)" \
  -Dpact.verifier.publishResults=true
