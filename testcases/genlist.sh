#!/bin/sh
echo "rule org.apache.** net.zhuoweizhang.boardwalk.@0"
cd ../../makeshift/src
for f in `find . -type f`
do
	q=`echo $f|sed -e "s/^\.\///" -e "s/\.java$//"`
	echo "rule" `echo $q|sed -e "s/\//./g" -e "s/^net\.zhuoweizhang\.makeshift\.//"` `echo $q|sed -e "s/\//./g"`
done
