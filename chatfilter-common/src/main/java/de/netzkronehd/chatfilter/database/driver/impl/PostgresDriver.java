package de.netzkronehd.chatfilter.database.driver.impl;

import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.database.driver.DatabaseDriver;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgresDriver extends Database implements DatabaseDriver {

    @Override
    public Connection createConnection(String host, int port, String database, String user, String password) throws SQLException {
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerNames(new String[]{host});
        dataSource.setPortNumbers(new int[]{port});
        dataSource.setDatabaseName(database);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setReWriteBatchedInserts(true);
        return dataSource.getConnection();
    }

    @Override
    public String getName() {
        return "PostgreSQL";
    }

    @Override
    public String getClassName() {
        return "de.netzkronehd.chatfilter.lib.org.postgresql.Driver";
    }
}
