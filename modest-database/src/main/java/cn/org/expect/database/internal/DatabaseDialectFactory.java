package cn.org.expect.database.internal;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import javax.sql.DataSource;

import cn.org.expect.database.DatabaseDialect;
import cn.org.expect.ioc.EasyBeanFactory;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

/**
 * 数据库方言工厂
 *
 * @author jeremy8551@gmail.com
 * @createtime 2012-03-06
 */
@EasyBean
public class DatabaseDialectFactory implements EasyBeanFactory<DatabaseDialect> {
    private final static Log log = LogFactory.getLog(DatabaseDialectFactory.class);

    /** 数据库方言仓库 */
    private final DatabaseDialectCollection collection;

    /**
     * 初始化
     */
    public DatabaseDialectFactory(EasyContext context) {
        this.collection = (DatabaseDialectCollection) context.getBeanEntryCollection(DatabaseDialect.class);
    }

    public DatabaseDialect build(EasyContext context, Object... args) throws Exception {
        String[] array = this.getDatabaseInfo(args);
        String name = array[0];
        String major = array[1];
        String minor = array[2];

        Class<?> type = this.collection.get(name, major, minor);
        return context.newInstance(type);
    }

    /**
     * 将参数转为数据库方言实现类注解参数 kind mode major minor 属性
     *
     * @param args 外部参数数组
     * @return 数据库信息数组，第一个元素是数据库简称，第二个元素是数据库大版本号，第三个元素是数据库小版本号
     */
    private String[] getDatabaseInfo(Object[] args) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            Object obj = args[i];

            // 数据库连接
            if (obj instanceof Connection) {
                Connection conn = (Connection) obj;
                String[] array = this.parse(conn);
                if (array != null) {
                    return array;
                } else {
                    continue;
                }
            }

            // 数据库连接池
            if (obj instanceof DataSource) {
                Connection conn = ((DataSource) obj).getConnection();
                try {
                    String[] array = this.parse(conn);
                    if (array != null) {
                        return array;
                    } else {
                        continue;
                    }
                } finally {
                    IO.closeQuiet(conn);
                    IO.closeQuietly(conn);
                }
            }
        }

        String major = ArrayUtils.indexOf(args, String.class, 0);
        String minor = major == null ? null : ArrayUtils.indexOf(args, String.class, Arrays.binarySearch(args, major) + 1);
        String[] array = new String[3];
        array[0] = this.collection.parseName(StringUtils.join(args, " "));
        array[1] = StringUtils.coalesce(major, "");
        array[2] = StringUtils.coalesce(minor, "");
        return array;
    }

    /**
     * 从数据库连接中解析: 数据库简称，大版本号，小版本号
     *
     * @param conn 数据库连接
     * @return 数据库信息数组，第一个元素是数据库简称，第二个元素是数据库大版本号，第三个元素是数据库小版本号
     */
    private String[] parse(Connection conn) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            String[] array = new String[3];
            array[0] = collection.parseName(metaData.getURL());
            array[1] = String.valueOf(metaData.getDatabaseMajorVersion());
            array[2] = String.valueOf(metaData.getDatabaseMinorVersion());
            return array;
        } catch (Throwable e) {
            log.error("parse()", e);
            return null;
        }
    }
}
