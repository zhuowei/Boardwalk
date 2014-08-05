cd testcases
./genlist.sh >../predex_out/jarjarrules.txt
cd ..
cp -r predex_out/dexed_libraries assets/
cp predex_out/jarjarrules.txt assets/
