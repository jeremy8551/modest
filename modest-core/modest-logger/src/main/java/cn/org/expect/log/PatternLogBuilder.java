package cn.org.expect.log;

/**
 * 默认的日志工厂
 *
 * @author jeremy8551@qq.com
 * @createtime 2012-06-28
 */
public class PatternLogBuilder implements LogBuilder {

    /**
     * 判断是否支持控制台输出
     *
     * @return 返回true表示支持使用控制台输出日志
     */
    public static boolean support() {
        return System.getProperty(LogFactory.PROPERTY_LOG_SOUT) != null;
    }

    public PatternLogBuilder() {
    }

    public Log create(LogContext context, Class<?> type, String fqcn, boolean dynamicCategory) throws Exception {
        LogLevel level = context.getLevel(type);
        PatternLog log = new PatternLog(context, type, level, fqcn, dynamicCategory);
        context.addLog(log);
        return log;
    }
}
