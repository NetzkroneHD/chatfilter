package de.netzkronehd.chatfilter.database.impl;

import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.dependency.Dependency;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDriver extends Database {

    public MySQLDriver() {
    }

    @Override
    public Connection createConnection(String host, int port, String database, String user, String password) throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
    }

    @Override
    public String getName() {
        return "MySQL";
    }

    @Override
    public String getClassName() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    public Dependency getDependency() {
        return Dependency.MYSQL;
    }
}
