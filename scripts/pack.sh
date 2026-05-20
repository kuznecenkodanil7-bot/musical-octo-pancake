#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
zip -r ../AIAssistantFabric_1.21.11.zip . \
  -x "build/*" ".gradle/*" ".idea/*" "*.iml"
echo "Created ../AIAssistantFabric_1.21.11.zip"
