package de.netzkronehd.chatfilter.database.impl;

import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.dependency.Dependency;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLDriver extends Database {

    public MySQLDriver() {
    }

    @Override
    public Connection createConnection(String host, int port, String database, String user, String password) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        if(driverClass == null) {
            throw new IllegalStateException("ClassLoader is not set.");
        }
        final Object driverInstance = driverClass.getConstructor().newInstance();
        final Method connectMethod = driverInstance.getClass().getMethod("connect", String.class, Properties.class);
        connectMethod.setAccessible(true);
        final Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("autoReconnect", "true");
        return (Connection) connectMethod.invoke(driverInstance, "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", properties);
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
