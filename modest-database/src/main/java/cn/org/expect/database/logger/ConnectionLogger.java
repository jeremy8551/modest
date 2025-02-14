package cn.org.expect.database.logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.TimeWatch;
import cn.org.expect.util.UniqueSequenceGenerator;

/**
 * 数据库连接日志接口，使用代理方式打印数据库连接上的关键操作信息
 *
 * @author jeremy8551@gmail.com
 * @createtime 2011-09-23
 */
public class ConnectionLogger implements InvocationHandler {
    private final static Log log = LogFactory.getLog(ConnectionLogger.class);

    /** 代理数据库连接的序号生成器 */
    protected final static UniqueSequenceGenerator UNIQUE = new UniqueSequenceGenerator("{}", 1);

    /** 被代理的 Connection 对象 */
    private final Connection conn;

    /** 数据库连接编号 */
    private final long number;

    /** 超时时间（单位：秒） */
    private final int warnTimeout;

    /**
     * 初始化
     *
     * @param conn        数据库连接
     * @param warnTimeout 超时提醒时间，单位秒
     */
    public ConnectionLogger(Connection conn, int warnTimeout) {
        this.number = UNIQUE.next();
        this.conn = Ensure.notNull(conn);
        this.warnTimeout = Ensure.fromZero(warnTimeout);
    }

    /**
     * 返回 Connection 对象的代理
     *
     * @return 数据库连接
     */
    public Connection getProxy() {
        Connection conn = (Connection) Proxy.newProxyInstance(this.conn.getClass().getClassLoader(), new Class[]{Connection.class}, this);
        if (log.isInfoEnabled()) {
            log.info("database.stdout.message017", this.getName(), this.conn.getClass().getName());
        }
        return conn;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String id = this.getName();
        if (log.isInfoEnabled()) {
            log.info("database.stdout.message018", id, this.conn.getClass().getName(), StringUtils.toString(method), StringUtils.toString(args));
        }

        TimeWatch watch = new TimeWatch();
        Object value = method.invoke(this.conn, args);
        try {
            if (value instanceof Statement) {
                return new StatementLogger(id, value, method.getReturnType()).getProxy(); // 生成 Statement 代理
            } else {
                return value;
            }
        } finally {
            if (log.isInfoEnabled()) {
                log.info("database.stdout.message019", id, this.conn.getClass().getName(), StringUtils.toString(method), value, watch.useTime());
            }
        }
    }

    /**
     * 判断是否设置了超时时间
     *
     * @return 返回 true 表示存在超时警告秒数
     */
    public boolean haveOvertimeWarn() {
        return this.warnTimeout > 0;
    }

    /**
     * 返回超时警告秒数
     *
     * @return 超时时间，单位：秒
     */
    public int getOvertimeWarn() {
        return this.warnTimeout;
    }

    /**
     * 返回数据库连接说明信息
     *
     * @return 数据库说明信息
     */
    public String getName() {
        return ResourcesUtils.getMessage("database.stdout.message020", this.number);
    }
}
