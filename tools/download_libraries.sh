#!/bin/bash
set -e
wget -nv -O repacklwjgl3/lwjgl-3.2.3.zip https://build.lwjgl.org/release/3.2.3/lwjgl-3.2.3.zip
(cd repacklwjgl3 && ./repack.sh)
# TODO: Boardwalk LWJGL natives
# TODO: java
# TODO: busybox
