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
        return "de.netzkronehd.chatfilter.lib.org.sqlite.JDBC";
    }
}
