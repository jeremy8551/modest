package cn.org.expect.log;

/**
 * 单例模式
 *
 * @author jeremy8551@gmail.com
 * @createtime 2025-12-01
 */
public class SingletonLogBuilder implements LogBuilder {

    protected Log log;

    public SingletonLogBuilder(Log log) {
        this.log = log;
    }

    public Log create(LogContext context, Class<?> type, String fqcn, boolean dynamicCategory) throws Exception {
        return this.log;
    }
}
