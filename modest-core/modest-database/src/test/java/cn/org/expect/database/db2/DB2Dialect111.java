package cn.org.expect.database.db2;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.database.DatabaseDialect;

/**
 * 关于DB2数据库的 {@linkplain cn.org.expect.database.DatabaseDialect} 数据库方言接口实现类
 *
 * @author jeremy8551@qq.com
 */
@EasyBean(value = "db2")
public class DB2Dialect111 extends DB2Dialect implements DatabaseDialect {

    public String getDatabaseMajorVersion() {
        return "";
    }

    public String getDatabaseMinorVersion() {
        return "1";
    }
}