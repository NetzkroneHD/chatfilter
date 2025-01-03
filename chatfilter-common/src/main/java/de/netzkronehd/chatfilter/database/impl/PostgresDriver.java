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
