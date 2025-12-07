#!/bin/sh
year=$1
day=$2
filter=$3
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk/
JAR=$year/build/benchmarks/main/jars/aoc-$year-main-jmh-JMH.jar
PERFASM='perfasm:intelSyntax=true;hotThreshold=0.1;tooBigThreshold=2000;ecycles,branch-misses'
./gradlew :aoc-$year:mainBenchmarkJar && $JAVA_HOME/bin/java --add-modules jdk.incubator.vector -jar $JAR ".*$year.*Day$day.*$filter" -prof $PERFASM -prof perfnorm -f 1 -wi 3 | tee log-asm-$year-$day.txt
