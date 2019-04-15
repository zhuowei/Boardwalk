# Boardwalk: a Minecraft Java Edition launcher for Android

This is the source code for Boardwalk, a Minecraft Java Edition launcher for Android.

# Branches

This branch contains the source for Boardwalk 2.0 (the current development branch).

There are three branches of development:

- `master`: Boardwalk 1.0-1.2. Uses Android's own JVM. The only released version. Doesn't work on Android 7.x and above.
- `boardwalk-1.9`: Boardwalk 1.9 beta. Uses Oracle's JDK. Abandoned.
- `boardwalk-2.0`: Boardwalk 2.0. Uses OpenJDK's Android port. This is the latest version.

# Additional components

Some modified third-party components are in separate repositories:

- [LWJGL Android port](https://github.com/BoardwalkApp/boardwalk-lwjgl)
- [lunixbochs' and ptitSeb's glShim](https://github.com/BoardwalkApp/boardwalk-glshim)

## Building

TODO - the current Boardwalk build process only works on my computer; I will post updated instructions soon.

## Build LWJGL

Build it

copy liblwjgl32.so, liblwjgl_opengl32.so to app/libs/armeabi-v7a

## Build lwjgl3override

cd lwjgl3override
./build

## Build JVM

build it, copy jre.tar.xz to app/src/main/assets/

## Copy a Busybox

Busybox in app/src/main/assets/busybox; should be statically linked.

# License

The source code in this repository is licensed under the Apache License, version 2.0 unless otherwise indicated in the file.

This means you can use those files as long as you credit me.

Some files and libraries are taken from third-party projects and have their own licenses. Please see the header of those files for their licenses.

If you have any questions, please open an issue.

# Code of Conduct

This project is governed by the Contributor Covenant version 1.4(https://www.contributor-covenant.org/version/1/4/code-of-conduct.html).
