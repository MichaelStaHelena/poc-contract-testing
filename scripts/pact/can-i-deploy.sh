#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/lib.sh"
require_clean_commit
broker can-i-deploy \
  --pacticipant user-service \
  --version "$(version)" \
  --to-environment "$PACT_ENVIRONMENT"
