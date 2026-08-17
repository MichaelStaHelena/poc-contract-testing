#!/usr/bin/env bash
set -euo pipefail

MAVEN_VERSION=3.9.11
BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
MAVEN_HOME="$BASE_DIR/.mvn/apache-maven-$MAVEN_VERSION"

if ! command -v javac >/dev/null 2>&1; then
  echo "JDK 21 is required. Install a JDK (not only a JRE) and make javac available on PATH." >&2
  exit 1
fi

if [ ! -x "$MAVEN_HOME/bin/mvn" ]; then
  mkdir -p "$BASE_DIR/.mvn"
  archive="$BASE_DIR/.mvn/apache-maven-$MAVEN_VERSION-bin.tar.gz"
  curl --fail --location --silent --show-error \
    "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz" \
    --output "$archive"
  tar -xzf "$archive" -C "$BASE_DIR/.mvn"
  rm -f "$archive"
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
