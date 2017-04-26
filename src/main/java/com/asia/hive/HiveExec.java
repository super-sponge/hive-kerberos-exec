package com.asia.hive;

import org.apache.commons.cli.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;

/**
 * Created by sponge on 2017/4/19 0019.
 */
public class HiveExec {
    private static  final  Logger LOG = LoggerFactory.getLogger(HiveExec.class);

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

                    if (keytab !=null && principal != null) {
                        LOG.info("Kerberos login  with keytable " + keytab + " principal " + principal);
                        LoginKerberos(keytab, principal);
                    }

                    if (jdbc != null && cmd != null) {
                        ExeSQL(jdbc, cmd);
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

    private static void ExeSQL(String jdbc, String sql) throws SQLException {
        Connection con = DriverManager.getConnection(jdbc);

        Statement stmt = con.createStatement();
        for(String sub_sql: sql.split(";")) {
            execute_statement(stmt, sub_sql);
        }
        con.close();
    }
    private static void execute_statement(Statement stmt, String sql) throws SQLException {
        LOG.info("execute  " + sql);
        ResultSet res = null;
        stmt.execute(sql);

        res = stmt.getResultSet();
        if (res == null) {
            return;
        }

        ResultSetMetaData rsmd = res.getMetaData() ;
        int columnCount = rsmd.getColumnCount();

        LOG.debug("Columns is " + columnCount);

        while (res.next()) {
            String row = "";
            for(int i = 1; i <= columnCount; i ++) {
                row += res.getString(i) + ";";
            }
            LOG.info(row);
        }
    }
}
