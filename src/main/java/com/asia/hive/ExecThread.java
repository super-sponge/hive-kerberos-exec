package com.asia.hive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Created by sponge on 2017/4/26 0026.
 */
public class ExecThread extends Thread  {
    private static  final Logger LOG = LoggerFactory.getLogger(ExecThread.class);
    private int i = 0;

    private String jdbc;
    private String sql;
    private int num ;

    public ExecThread(String jdbc_, String sql_, int num_) {
        this.jdbc = jdbc_;
        this.sql = sql_;
        this.num = num_;
    }
    @Override
    public void run() {
        LOG.info("Thread " + Thread.currentThread().getName() + " running ... ");


        try {
            for (int i = 0; i < this.num; i ++) {
                LOG.info("Thread " + Thread.currentThread().getName() + " run " + i );
                ExeSQL(this.jdbc, sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

        while (res.next()) {
            String row = "";
            for(int i = 1; i <= columnCount; i ++) {
                row += res.getString(i) + ";";
            }
            LOG.info(row);
        }
    }
}
