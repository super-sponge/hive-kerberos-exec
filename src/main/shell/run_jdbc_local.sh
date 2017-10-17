#!/usr/bin/env bash


if [ ! -n "$1" ] ;then
    echo "you have not input a sql"
    exit
else
    echo "the sql you input is $1"
fi

sql=$1

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

jars=$jars:/usr/hdp/current/hive-client/jdbc/hive-jdbc-1.2.1000.2.6.2.0-205-standalone.jar



#java -cp $jars $LOG4J_PARAMS  com.asia.hive.HiveExec -k /tmp/dcadmin.keytab -p "dcadmin@DC.COM" -j "jdbc:hive2://dcdn026:10000/;principal=hive/dcdn026@DC.COM" -c "show databases"
java -cp $jars $LOG4J_PARAMS  com.asia.hive.HiveExec -k $base_dir/conf/dcadmin.keytab -p "dcadmin@DC.COM" -j "jdbc:hive2://dcdn026:10000/;principal=hive/dcdn026@DC.COM" -c "$sql"