package cn.org.expect.log;

import cn.org.expect.log.internal.LogContextImpl;
import cn.org.expect.log.internal.LogPattern;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.Logs;
import cn.org.expect.util.Settings;

/**
 * 日志模块
 *
 * @author jeremy8551@gmail.com
 * @createtime 2012-06-28
 */
public class LogFactory {

    /** JVM参数名，日志级别，详见 {@linkplain LogLevel} */
    public final static String PROPERTY_LOGGER = Settings.getPropertyName("log");

    /** JVM参数名，控制台输出日志, 参数值就是日志格式，详见{@linkplain LogPattern} */
    public final static String PROPERTY_LOG_SOUT = Settings.getPropertyName("log.sout");

    /** sout 的日志格式, 详见{@linkplain LogPattern} */
    public static String SOUT_PATTERN = "%m%ex%n";

    /** sout+ 的日志格式, 详见{@linkplain LogPattern} */
    public static String SOUT_PLUS_PATTERN = "%d|%-5.5p|%30.30c|%50.50l|%m%ex%n";

    /** 单例模式 */
    private static volatile LogContext context;

    /** 锁 */
    private final static Object lock = new Object();

    /**
     * 初始化
     */
    private LogFactory() {
    }

    /**
     * 返回单例模式的日志上下文信息
     *
     * @return 日志上下文信息
     */
    public static LogContext getContext() {
        if (context == null) {
            synchronized (lock) {
                if (context == null) {
                    context = new LogContextImpl();
                    Logs.setLogger(LogFactory.getLog(context, Logs.class, Logs.class.getName(), true));
                }
            }
        }
        return context;
    }

    /**
     * 返回一个日志接口
     *
     * @param type 日志归属的类
     * @return 日志接口
     */
    public static Log getLog(Class<?> type) {
        return LogFactory.getLog(LogFactory.getContext(), type, null, false);
    }

    /**
     * 返回一个日志接口
     *
     * @param context 日志模块上下文信息
     * @param type    日志归属的类
     * @return 日志接口
     */
    public static Log getLog(LogContext context, Class<?> type) {
        return LogFactory.getLog(context, type, null, false);
    }

    /**
     * 返回一个日志接口
     *
     * @param context         日志模块上下文信息
     * @param type            日志归属的类
     * @param fqcn            用于定位输出日志的代码位置信息的标识符，详见 {@linkplain FQCNAware#setFQCN(String)}
     * @param dynamicCategory true表示使用 StackTraceElement 动态生成日志归属的类名, false表示使用 {@code type} 作为日志接口归属的类名
     * @return 日志接口
     */
    public synchronized static Log getLog(LogContext context, Class<?> type, String fqcn, boolean dynamicCategory) {
        Ensure.notNull(context);
        Ensure.notNull(type);

        try {
            return context.getBuilder().create(context, type, fqcn, dynamicCategory);
        } catch (Throwable e) {
            throw new LogException(type.getName(), e);
        }
    }

    /**
     * 设置全限定类名（Fully Qualified Class Name）用于跟踪日志的来源
     *
     * @param object 日志接口
     * @param value  FQCN值
     */
    public static void setFQCN(Object object, Object value) {
        if (object instanceof FQCNAware) {
            String fqcn = value instanceof String ? value.toString() : value instanceof Class ? ((Class<?>) value).getName() : value.getClass().getName();
            ((FQCNAware) object).setFQCN(fqcn);
        }
    }

    /**
     * 设置日志参数
     *
     * @param args 参数数组，详见 {@linkplain LogSettings#load(String[])}
     * @return 返回参数数组中与日志配置无关的配置信息
     */
    public static synchronized String[] load(String... args) {
        return new LogSettings(LogFactory.getContext()).load(args);
    }
}
