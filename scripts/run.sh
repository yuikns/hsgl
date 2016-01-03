#!/bin/sh
MY_JAVA_OPTS="-Xms20g -Xmx20g -XX:+UseConcMarkSweepGC -XX:ParallelCMSThreads=16 -XX:ParallelGCThreads=16 "
env JAVA_OPTS="$MY_JAVA_OPTS" sbt clean compile run

