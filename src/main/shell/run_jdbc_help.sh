#!/usr/bin/env bash


base_dir=$(dirname $0)/..


LOG4J=$base_dir/conf/log4j.properties
if [ -f $LOG4J ]; then
    export LOG4J_PARAMS="-Dlog4j.configuration=file:$LOG4J"
fi


jars=$base_dir/hive-kerberos-exec-1.0.jar
for jar in ${base_dir}/lib/*jar
do
jars=$jars:$jar
done

java -cp $jars $LOG4J_PARAMS  com.asia.hive.HiveExec -h