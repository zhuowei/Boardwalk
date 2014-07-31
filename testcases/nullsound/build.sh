rm -r bin
mkdir bin
javac -d bin -classpath ../boardwalk/dexin/soundsystem-20120107.jar paulscode/sound/libraries/LibraryLWJGLOpenAL.java
cd bin
7z -tzip a ../../boardwalk/dexin/fakeopenal.jar .
