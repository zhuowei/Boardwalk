mkdir predex_out
cd testcases
./genlist.sh >../predex_out/jarjarrules.txt
cd ..
rm -r predex_out/override_libraries
cp -r override_libs predex_out/override_libraries
export CLASSPATH=predex_bin:libs/dx-renamed.jar:libs/jarjar-1.4.jar:libs/gson-2.2.4.jar
java net.zhuoweizhang.boardwalk.downloader.MinecraftLaunch predex_out $1
