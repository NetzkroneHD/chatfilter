package de.netzkronehd.chatfilter.database.impl;

import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.dependency.Dependency;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class SqlLiteDriver extends Database {

    private final Path databasePath;

    public SqlLiteDriver(Path databasePath) {
        this.databasePath = databasePath;
    }

    @Override
    public Connection createConnection(String host, int port, String database, String user, String password) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        if(driverClass == null) {
            throw new IllegalStateException("ClassLoader is not set.");
        }
        if(!Files.exists(databasePath)) {
            Files.createFile(databasePath);
        }
        final Method createConnection = driverClass.getMethod("createConnection", String.class, Properties.class);
        createConnection.setAccessible(true);
        return (Connection) createConnection.invoke(driverClass, "jdbc:sqlite:"+databasePath.toFile().getAbsolutePath(), new Properties());
    }

    @Override
    public void createTables() throws SQLException {
        connection.prepareStatement("""
                CREATE TABLE IF NOT EXISTS chatfilter_players
                (
                    player_uniqueId    VARCHAR(36) PRIMARY KEY,
                    player_name        VARCHAR(16) NOT NULL,
                    blocked_broadcast  VARCHAR(8) NOT NULL DEFAULT 'DEFAULT',
                    filtered_broadcast VARCHAR(8) NOT NULL DEFAULT 'DEFAULT'
                )
                """).executeUpdate();
        connection.prepareStatement("""
                CREATE TABLE IF NOT EXISTS chatfilter_violations
                (
                     id              INTEGER PRIMARY KEY AUTOINCREMENT,
                     player_uniqueId VARCHAR(36),
                     filter_name     TEXT NOT NULL,
                     message_text    TEXT NOT NULL,
                     message_state   VARCHAR(8),
                     message_time    INTEGER,
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
