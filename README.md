kerberos connect to hiveserver2
================================

## compile
    mvn package    
## run
    tar -zxf hive-kerberos-exec-1.0-job.tar.gz
    cd hive-kerberos-exec-1.0
    cd bin
     ./run_jdbc_local.sh
     
## note
    print help message
    ./run_jdbc_help.sh 