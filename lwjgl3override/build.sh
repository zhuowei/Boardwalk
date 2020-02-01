#!/bin/bash
set -e
cd "$(dirname $0)"
rm -r bin lwjgl_override.jar || true
mkdir bin
find src -name "*.java" | xargs -- javac -classpath "src:lib/*" -target 1.8 -source 1.8 -d bin
cd bin
7z a ../lwjgl_override.jar .
cd ..
cp lwjgl_override.jar ../app/src/main/assets/
