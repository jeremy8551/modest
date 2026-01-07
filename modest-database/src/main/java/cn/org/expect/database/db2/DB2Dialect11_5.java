package cn.org.expect.database.db2;

import cn.org.expect.database.DatabaseIndex;
import cn.org.expect.ioc.annotation.EasyBean;

/**
 * DB2 11.5+ 正式移除了 DROP INDEX … ON table 的兼容SQL
 *
 * @author jeremy8551@gmail.com
 */
@EasyBean(value = "db2", description = "DB2 11.5")
public class DB2Dialect11_5 extends DB2Dialect {

    public String generateDropIndexDDL(DatabaseIndex index) {
        return "drop index " + index.getFullName();
    }

    public String getDatabaseMajorVersion() {
        return "11";
    }

    public String getDatabaseMinorVersion() {
        return "5";
    }
}
