package com.asia.hive;

import org.apache.commons.cli.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sponge on 2017/4/26 0026.
 */
public class HiveThreadDemo {
    private static  final Logger LOG = LoggerFactory.getLogger(HiveThreadDemo.class);

    public static void main(String[] args) throws IOException {
        try {
            ExecuteCommandLine(args);
        } catch (SQLException e) {
            LOG.error("sql error " + e.getMessage());
        }
    }

    public static void ExecuteCommandLine(String[] args ) throws IOException, SQLException {
        Options opts = new Options();
        opts.addOption("h", false, "Help description");
        opts.addOption("k", true, "keytable file");
        opts.addOption("p", true, "principal");
        opts.addOption("j", true, "jdbc connect string");
        opts.addOption("c", true, "sql to execute");
        opts.addOption("t", true, "thread num");
        opts.addOption("n", true, "thread sql execute times");

        CommandLineParser parser = new DefaultParser();
        CommandLine cl;
        try {
            cl = parser.parse(opts, args);
            if (cl.getOptions().length > 0) {
                if (cl.hasOption('h')) {
                    HelpFormatter hf = new HelpFormatter();
                    hf.printHelp("May Options", opts);
                } else {
                    String keytab = cl.getOptionValue("k");
                    String principal = cl.getOptionValue("p");
                    String jdbc = cl.getOptionValue("j");
                    String cmd = cl.getOptionValue("c");
                    String threads = cl.getOptionValue("t");
                    String times = cl.getOptionValue("n");

                    if (keytab !=null && principal != null) {
                        LOG.info("Kerberos login  with keytable " + keytab + " principal " + principal);
                        LoginKerberos(keytab, principal);
                    }

                    if (jdbc != null && cmd != null) {
                        int thrds = (threads == null? 1 : Integer.parseInt(threads));
                        int num  = (times == null? 1 : Integer.parseInt(times));

                        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(thrds);

                        for (int i = 0; i < thrds; i ++) {
                            fixedThreadPool.execute(new ExecThread(jdbc, cmd, num));
                        }

                        fixedThreadPool.shutdown();
                    }
                }
            } else {
                System.err.println("ERROR_NOARGS");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void LoginKerberos(String keytab, String principal) throws IOException {
        Configuration conf = new Configuration();
        conf.set("hadoop.security.authentication", "Kerberos");
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation.loginUserFromKeytab(principal, keytab);
    }
}
