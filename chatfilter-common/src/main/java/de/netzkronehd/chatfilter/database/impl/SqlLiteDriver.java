package de.netzkronehd.chatfilter.database.impl;

import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.dependency.Dependency;
import org.sqlite.JDBC;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class SqlLiteDriver extends Database {

    public SqlLiteDriver() {
    }

    @Override
    public Connection createConnection(String host, int port, String database, String user, String password) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(driverClass == null) {
            throw new IllegalStateException("ClassLoader is not set.");
        }
        final Method createConnection = driverClass.getMethod("createConnection", String.class, Properties.class);
        createConnection.setAccessible(true);
        return (Connection) createConnection.invoke(driverClass, JDBC.PREFIX+database, new Properties());
    }

    @Override
    public void createTables() throws SQLException {
        connection.prepareStatement("""
                CREATE TABLE IF NOT EXISTS chatfilter_players
                (
                    player_uniqueId VARCHAR(36) PRIMARY KEY,
                    player_name     VARCHAR(16) NOT NULL
                )
                """).executeUpdate();
        connection.prepareStatement("""
                CREATE TABLE IF NOT EXISTS chatfilter_violations
                (
                     id              INTEGER PRIMARY KEY,
                     player_uniqueId VARCHAR(36),
                     filter_name     TEXT NOT NULL,
                     message_text    TEXT NOT NULL,
                     message_state   VARCHAR(8),
                     message_time    LONG,
                     FOREIGN KEY (player_uniqueId) REFERENCES chatfilter_players(player_uniqueId)
                 )
                """).executeUpdate();
    }

    @Override
    public String getName() {
        return "SQLite";
    }

    @Override
    public String getClassName() {
        return "org.sqlite.JDBC";
    }

    @Override
    public Dependency getDependency() {
        return Dependency.SQLITE;
    }
}
