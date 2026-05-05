#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

if [[ ! -f "app/src/tdlibFlavor/java/org/drinkless/tdlib/Client.java" || ! -f "app/src/tdlibFlavor/java/org/drinkless/tdlib/TdApi.java" ]]; then
  cat <<'EOF'
Missing TDLib Java binding sources.

Copy the official generated TDLib Java source folder into:
  app/src/tdlibFlavor/java/org/drinkless/tdlib/

Expected files include:
  app/src/tdlibFlavor/java/org/drinkless/tdlib/Client.java
  app/src/tdlibFlavor/java/org/drinkless/tdlib/TdApi.java

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
