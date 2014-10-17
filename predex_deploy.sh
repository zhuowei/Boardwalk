cd testcases
./genlist.sh >../predex_out/jarjarrules.txt
cd ..
cp -r predex_out/dexed_libraries assets/
cp predex_out/jarjarrules.txt assets/
cp assets/jarjarrules.txt assets/jarjarrules_minecraft.txt
echo "rule java.util.PriorityQueue net.zhuoweizhang.makeshift.java.util.PriorityQueue" >>assets/jarjarrules_minecraft.txt
echo "rule java.util.regex.** jdkregex.@1" >>assets/jarjarrules_minecraft.txt
