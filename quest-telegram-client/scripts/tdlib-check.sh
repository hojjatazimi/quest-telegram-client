#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

if ! compgen -G "app/libs/tdlib-jars/*.jar" > /dev/null; then
  cat <<'EOF'
Missing TDLib Java binding JAR.

Place the official generated TDLib Java JAR at:
  app/libs/tdlib-jars/tdlib.jar

Then rerun:
  ./scripts/tdlib-check.sh
EOF
  exit 1
fi

if ! find app/src/tdlibFlavor/jniLibs -name 'libtdjni.so' -print -quit | grep -q .; then
  cat <<'EOF'
Missing TDLib native library.

Place official TDLib Android native libraries by ABI, for example:
  app/src/tdlibFlavor/jniLibs/arm64-v8a/libtdjni.so

Then rerun:
  ./scripts/tdlib-check.sh
EOF
  exit 1
fi

./gradlew :app:assembleTdlibDebug
