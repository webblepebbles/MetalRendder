#!/usr/bin/env bash
set -euo pipefail
mkdir -p build/native
clang -fobjc-arc -shared -o build/native/libmetalrender.dylib src/main/native/metalrender.m -framework Cocoa -framework Metal -framework QuartzCore
cp build/native/libmetalrender.dylib src/client/resources/