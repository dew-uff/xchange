@echo off
java -cp lib/java-getopt-1.0.8.jar;lib/xercesImpl-2.4.0.jar;lib/3dm.jar tdm.tool.TreeDiffMerge -c 0 -e%4 --conflictlog=%5 -m %1 %2 %3
