package cn.org.expect.database.db2;

import java.sql.Connection;
import java.util.Properties;

import cn.org.expect.database.DatabaseDialect;
import cn.org.expect.util.Dates;

public class TestThread extends Thread {

    private DatabaseDialect dialect;
    private Connection conn;
    private boolean error;
    private Properties attrs;

    public TestThread(DatabaseDialect dialect, Connection conn) {
        super();
        this.dialect = dialect;
        this.conn = conn;
        this.error = false;
        this.attrs = this.dialect.getAttributes(this.conn);
    }

    public void start() {
        super.start();
    }

    public void run() {
        Dates.sleep(2 * 1000);

        try {
            if (!this.dialect.terminate(this.conn, this.attrs)) {
                System.err.println("终止数据库连接失败!");
                this.error = true;
            } else {
                this.error = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isError() {
        return error;
    }

}
