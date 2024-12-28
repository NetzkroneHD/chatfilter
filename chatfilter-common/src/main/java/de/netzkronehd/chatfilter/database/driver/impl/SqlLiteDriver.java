package de.netzkronehd.chatfilter.database.driver.impl;

import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.database.driver.DatabaseDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlLiteDriver extends Database implements DatabaseDriver {

    public SqlLiteDriver() {
    }

    @Override
    public Connection createConnection(String host, int port, String database, String user, String password) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + database);
    }

    @Override
    public String getName() {
        return "SQLite";
    }

    @Override
    public String getClassName() {
        return "de.netzkronehd.chatfilter.lib.org.sqlite.JDBC";
    }
}
