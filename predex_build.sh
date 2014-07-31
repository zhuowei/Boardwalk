rm -r predex_bin
mkdir predex_bin
export CLASSPATH=src:libs/dx.jar:libs/jarjar-1.4.jar:libs/gson-2.2.4.jar
javac -d predex_bin src/net/zhuoweizhang/boardwalk/downloader/MinecraftLaunch.java
