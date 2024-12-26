package de.netzkronehd.chatfilter.database.driver;

import de.netzkronehd.chatfilter.database.driver.impl.MySQLDriver;
import de.netzkronehd.chatfilter.database.driver.impl.PostgresDriver;
import de.netzkronehd.chatfilter.database.driver.impl.SqlLiteDriver;

import java.util.List;

public interface DatabaseDriver {

    String getName();
    String getClassName();

    List<DatabaseDriver> DRIVERS = List.of(
            new MySQLDriver(),
            new SqlLiteDriver(),
            new PostgresDriver()
    );

}
