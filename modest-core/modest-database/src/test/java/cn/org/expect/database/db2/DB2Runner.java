package cn.org.expect.database.db2;

import java.sql.Connection;

import cn.org.expect.database.annotation.DatabaseRunner;
import cn.org.expect.database.Jdbc;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

public class DB2Runner extends DatabaseRunner {

    public DB2Runner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    protected DB2Runner(TestClass testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected String[] enableProperties() {
        return new String[]{"db2.url", "db2.username", "db2.password"};
    }

    @Override
    protected Connection createConnection() {
        String url = this.properties.getProperty("db2.url");
        String username = this.properties.getProperty("db2.username");
        String password = this.properties.getProperty("db2.password");
        return Jdbc.getConnection(url, username, password);
    }
}
