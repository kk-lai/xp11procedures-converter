package com.fg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Db {
    protected static final String dbFile = "nav.sqlite";
    protected static Connection conn = null;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected void connect() {
        if (Db.conn==null) {
            logger.info("Connect sqlite db");
            String url = "jdbc:sqlite:"+dbFile;
            // create a connection to the database
            try {
                Db.conn = DriverManager.getConnection(url);
            } catch (SQLException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
