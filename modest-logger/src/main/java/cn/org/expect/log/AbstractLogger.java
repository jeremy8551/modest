package cn.org.expect.log;

import cn.org.expect.util.Ensure;
import cn.org.expect.util.StackTraceUtils;

/**
 * 抽象日志
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/22
 */
public abstract class AbstractLogger extends LevelLogger {

    /** 日志所属类 */
    protected Class<?> type;

    /** 日志所属类的名字 */
    protected String name;

    /** 创建日志的堆栈信息 */
    protected StackTraceElement stackTrace;

    /** 用于定位输出日志的代码位置信息的标识符 */
    private volatile String fqcn;

    /**
     * 抽象日志
     *
     * @param context 日志上下文信息
     * @param type    日志关联类
     * @param level   日志级别
     */
    public AbstractLogger(LogContext context, Class<?> type, LogLevel level) {
        super(context);
        this.setLevel(level);
        this.type = Ensure.notNull(type);
        this.name = type.getName();
        this.stackTrace = StackTraceUtils.get(LogFactory.class.getName());
    }

    public String getName() {
        return name;
    }

    public String getFqcn() {
        return this.fqcn;
    }

    public void setFQCN(String fqcn) {
        this.fqcn = fqcn;
    }

    public String toString() {
        String str = this.getClass().getName() + "{level=";
        if (trace) {
            str += "trace";
        } else if (debug) {
            str += "debug";
        } else if (info) {
            str += "info";
        } else if (warn) {
            str += "warn";
        } else if (error) {
            str += "error";
        } else if (fatal) {
            str += "fatal";
        } else {
            str += "off";
        }
        str += ", class=" + this.name;
        str += ", fqcn=" + this.fqcn;
        str += ", line=(" + stackTrace.getFileName() + ":" + this.stackTrace.getLineNumber() + ")";
        return str;
    }
}
