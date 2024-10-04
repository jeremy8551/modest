package cn.org.expect.log.console;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogBuilder;
import cn.org.expect.log.LogContext;

public class ConsoleLogBuilder implements LogBuilder {

    public Log create(LogContext context, Class<?> type, String fqcn, boolean dynamicCategory) throws Exception {
        return new ConsoleLog(context, type, context.getLevel(type));
    }
}
