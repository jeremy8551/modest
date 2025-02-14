package cn.org.expect.database.pool;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;

import cn.org.expect.database.logger.DataSourceLogger;
import cn.org.expect.database.logger.DataSourceLoggerProxy;
import cn.org.expect.ioc.EasyContext;

/**
 * 即时使用的数据库连接池
 *
 * @author jeremy8551@gmail.com
 * @createtime 2012-03-14
 */
public class SimpleDatasource implements DataSource, java.io.Closeable {

    /** 日志接口 */
    public static Logger logger = Logger.getLogger(SimpleDatasource.class.getName());

    /**
     * 判断参数 dataSource 是否是 {@linkplain SimpleDatasource} 类的实例对象
     *
     * @param dataSource 数据库连接池
     * @return 返回true表示参数是 {@linkplain SimpleDatasource} 类的实例对象 false表示不是 {@linkplain SimpleDatasource} 类的实例对象
     */
    public static boolean instanceOf(DataSource dataSource) {
        if (dataSource instanceof SimpleDatasource) {
            return true;
        } else if (dataSource instanceof DataSourceLoggerProxy) {
            String str = dataSource.toString().substring(DataSourceLogger.class.getName().length());
            return str.startsWith(SimpleDatasource.class.getName());
        } else {
            return false;
        }
    }

    /** 连接池 */
    private Pool pool;

    /**
     * 初始化
     *
     * @param context 容器上下文信息
     * @param p       配置信息
     */
    public SimpleDatasource(EasyContext context, Properties p) {
        super();
        this.pool = new Pool(context, p);
    }

    public EasyContext getContext() {
        return this.pool.getContext();
    }

    public void close() {
        this.pool.close();
    }

    public Connection getConnection() throws SQLException {
        return this.pool.getConnection(null, null);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return this.pool.getConnection(username, password);
    }

    public PrintWriter getLogWriter() {
        return this.pool.getLogWriter();
    }

    public void setLogWriter(PrintWriter out) {
        this.pool.setLogWriter(out);
    }

    public int getLoginTimeout() {
        return this.pool.getTimeout();
    }

    public void setLoginTimeout(int seconds) {
        this.pool.setTimeout(seconds);
    }

    public boolean isWrapperFor(Class<?> iface) {
        return iface != null && iface.isAssignableFrom(ConnectionProxy.class);
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> cls) throws SQLException {
        if (cls != null && cls.isAssignableFrom(Connection.class)) {
            return (T) this.pool.getConnection(null, null).getOrignalConnection();
        } else {
            throw new SQLException(cls == null ? "" : cls.getName());
        }
    }

    public Logger getParentLogger() {
        return logger;
    }

    public String toString() {
        return SimpleDatasource.class.getName() + super.toString();
    }
}
