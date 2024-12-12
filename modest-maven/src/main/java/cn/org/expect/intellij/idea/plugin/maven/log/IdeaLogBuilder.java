package cn.org.expect.intellij.idea.plugin.maven.log;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogBuilder;
import cn.org.expect.log.LogContext;

/**
 * 适配 Idea 日志
 *
 * @author jeremy8551@qq.com
 * @createtime 2023-09-13
 */
public class IdeaLogBuilder implements LogBuilder {

    public Log create(LogContext context, Class<?> type, String fqcn, boolean dynamicCategory) throws Exception {
        IdeaLog log = new IdeaLog(context, type);
        context.addLog(log);
        return log;
    }
}