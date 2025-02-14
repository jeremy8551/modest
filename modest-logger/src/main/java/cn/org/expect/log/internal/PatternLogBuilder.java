package cn.org.expect.log.internal;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogBuilder;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.LogLevel;

/**
 * 默认的日志工厂
 *
 * @author jeremy8551@gmail.com
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
