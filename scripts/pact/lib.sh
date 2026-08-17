#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
export PACT_BROKER_BASE_URL="${PACT_BROKER_BASE_URL:-http://localhost:9292}"
export PACT_ENVIRONMENT="${PACT_ENVIRONMENT:-development}"

version() {
  git -C "$ROOT_DIR" rev-parse HEAD
}

branch() {
  git -C "$ROOT_DIR" branch --show-current
}

require_clean_commit() {
  if ! git -C "$ROOT_DIR" diff --quiet || ! git -C "$ROOT_DIR" diff --cached --quiet; then
    echo "Commit the provider change before publishing verification results." >&2
    exit 2
  fi
}

broker() {
  local attempt
  local max_attempts=3

  for attempt in $(seq 1 "$max_attempts"); do
    if docker run --rm \
      --network host \
      -v "$ROOT_DIR:/work" \
      -w /work \
      -e PACT_BROKER_BASE_URL \
      -e PACT_BROKER_USERNAME \
      -e PACT_BROKER_PASSWORD \
      pactfoundation/pact-cli@sha256:72f1df83a7c42abd02e69a0fa21c3adc037d72c2852daa9f2910bb35617b207b pact-broker "$@"; then
      return 0
    fi

    if [ "$attempt" -lt "$max_attempts" ]; then
      echo "Pact Broker unavailable; retrying ($attempt/$max_attempts)..." >&2
      sleep $((attempt * 5))
    fi
  done

  return 1
}
