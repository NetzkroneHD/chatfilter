package de.netzkronehd.chatfilter.database.impl;

import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.dependency.Dependency;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresDriver extends Database {

    @Override
    public Connection createConnection(String host, int port, String database, String user, String password) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("reWriteBatchedInserts", "true");
        properties.setProperty("ssl", "false");
        final Object driverInstance = driverClass.getConstructor().newInstance();
        final Method connectMethod = driverInstance.getClass().getMethod("connect", String.class, Properties.class);
        connectMethod.setAccessible(true);
        return (Connection) connectMethod.invoke(driverInstance, "jdbc:postgresql://" + host + ":" + port + "/" + database + "?reWriteBatchedInserts=true", properties);
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
                     id              SERIAL PRIMARY KEY,
                     player_uniqueId VARCHAR(36),
                     filter_name     TEXT NOT NULL,
                     message_text    TEXT NOT NULL,
                     message_state   VARCHAR(8),
                     message_time    BIGINT,
                     FOREIGN KEY (player_uniqueId) REFERENCES chatfilter_players(player_uniqueId)
                 )
                """).executeUpdate();
    }

    @Override
    public String getName() {
        return "PostgreSQL";
    }

    @Override
    public String getClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    public Dependency getDependency() {
        return Dependency.POSTGRESQL;
    }
}
