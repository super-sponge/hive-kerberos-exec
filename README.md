kerberos connect to hiveserver2
================================

## compile
    mvn package    
## run
    tar -zxf hive-kerberos-exec-1.0-job.tar.gz
    cd hive-kerberos-exec-1.0
    cd bin
     ./run_jdbc_local.sh "sql statement"
     
     ./run_jdbc_thread.sh "select count(*) from  HI_DWD_DC.DWD_EVT_ECBD_WAP_LOGINS_D where date_no_=20170425"
     
## note
    print help message
    ./run_jdbc_help.sh 