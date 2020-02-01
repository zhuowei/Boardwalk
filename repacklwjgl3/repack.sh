#!/bin/bash
set -e
jarlibs="lwjgl lwjgl-glfw lwjgl-jemalloc lwjgl-openal lwjgl-opengl lwjgl-stb"
rm -r extract repack || true
mkdir extract
mkdir repack
cd extract
7z x ../lwjgl-3.2.3.zip $jarlibs
for i in $jarlibs
do
	cp $i/$i.jar ../repack/
done
cd ../repack
tar cJf ../lwjgl3.tar.xz .
cd ..
mkdir ../app/src/main/assets || true
cp lwjgl3.tar.xz ../app/src/main/assets/
