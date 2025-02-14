package cn.org.expect.log.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.org.expect.log.Appender;
import cn.org.expect.log.Console;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogAliveSet;
import cn.org.expect.log.LogBuilder;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogLevel;
import cn.org.expect.log.LogLevelAware;
import cn.org.expect.log.LogLevelManager;
import cn.org.expect.log.LogSettings;
import cn.org.expect.log.slf4j.Slf4jLogBuilder;
import cn.org.expect.message.ResourceMessageBundle;
import cn.org.expect.util.CharTable;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;

/**
 * 日志模块的上下文信息
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/22
 */
public class LogContextImpl implements LogContext {

    /** 日志系统启动的时间 */
    private long startTimeMillis;

    /** 日志工厂 */
    private LogBuilder builder;

    /** 已注册的日志接口 */
    private final LogAliveSet alive;

    /** 日志级别 */
    private final LogLevelManager levelManager;

    /** 日志记录器 */
    private final List<Appender> appenderList;

    /** 国际化信息 */
    private volatile ResourceMessageBundle resourceBundle;

    /**
     * 日志模块的上下文信息
     */
    public LogContextImpl() {
        this.setStartMillis(System.currentTimeMillis());
        this.alive = new LogAliveSet();
        this.levelManager = new LogLevelManager();
        this.appenderList = new ArrayList<Appender>();
        this.resourceBundle = ResourcesUtils.getRepository();
        this.init();
    }

    public void reset() {
        this.levelManager.reset();
        this.appenderList.clear();
        new PatternConsoleAppender(LogSettings.getPattern()).setup(this);
    }

    /**
     * 初始化日志系统
     */
    public void init() {
        // 安装控制台日志记录器
        new PatternConsoleAppender(LogSettings.getPattern()).setup(this);

        // 强制使用默认的日志系统输出日志
        if (PatternLogBuilder.support()) {
            this.builder = new PatternLogBuilder();
            return;
        }

        // 判断是否能使用 slf4j-api
        if (Slf4jLogBuilder.support()) {
            this.builder = new Slf4jLogBuilder();
            return;
        }

        // 默认的日志输出接口
        this.builder = new PatternLogBuilder();
    }

    public synchronized void updateLevel(String name, LogLevel level) {
        this.levelManager.put(name, level); // 设置日志级别
        List<Log> list = this.alive.get(name);
        for (Log log : list) {
            if (log instanceof LogLevelAware) {
                LogLevel logLevel = this.levelManager.get(name);
                ((LogLevelAware) log).setLevel(logLevel);
            }
        }
    }

    public void setResourceBundle(ResourceMessageBundle resourceBundle) {
        this.resourceBundle = Ensure.notNull(resourceBundle);
    }

    public ResourceMessageBundle getResourceBundle() {
        return this.resourceBundle;
    }

    public void setStartMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public long getStartMillis() {
        return startTimeMillis;
    }

    public synchronized void setBuilder(LogBuilder builder) {
        this.builder = Ensure.notNull(builder);
    }

    public LogBuilder getBuilder() {
        return builder;
    }

    @SuppressWarnings("unchecked")
    public <E> E findAppender(Class<E> type) {
        for (Appender next : this.appenderList) {
            if (next.getClass().equals(type)) {
                return (E) next;
            }
        }
        return null;
    }

    public void addAppender(Appender appender) {
        this.appenderList.add(Ensure.notNull(appender));
    }

    public List<Appender> removeAppender(Object object) {
        ArrayList<Appender> list = new ArrayList<Appender>();
        if (object instanceof Appender) {
            for (Iterator<Appender> it = this.appenderList.iterator(); it.hasNext(); ) {
                Appender next = it.next();
                if (next.equals(object)) {
                    it.remove();
                    list.add(next);
                    try {
                        next.close();
                    } catch (Exception e) {
                        Console.out.error(String.valueOf(object), e);
                    }
                }
            }
            return list;
        }

        if (object instanceof String) {
            for (Iterator<Appender> it = this.appenderList.iterator(); it.hasNext(); ) {
                Appender next = it.next();
                if (next.getName().equals(object)) {
                    it.remove();
                    list.add(next);
                }
            }
            return list;
        }

        if (object instanceof Class) {
            for (Iterator<Appender> it = this.appenderList.iterator(); it.hasNext(); ) {
                Appender next = it.next();
                if (next.getClass().equals(object)) {
                    it.remove();
                    list.add(next);
                }
            }
            return list;
        }

        throw new UnsupportedOperationException(object == null ? "" : object.getClass().getName());
    }

    public List<Appender> getAppenders() {
        return this.appenderList;
    }

    public LogLevel getLevel(Class<?> type) {
        return this.levelManager.get(type.getName());
    }

    public void addLog(Log log) {
        this.alive.add(log);
    }

    public String toString() {
        CharTable table = new CharTable();
        table.addTitle(super.toString());
        table.addTitle("");

        table.addCell("startTimeMillis");
        table.addCell(Dates.format21(new Date(this.startTimeMillis)));
        table.addCells("", "");

        table.addCell("builder");
        table.addCell(this.builder.getClass().getName());
        table.addCells("", "");

        CharTable ct1 = new CharTable();
        ct1.addTitle(this.appenderList.getClass().getName());
        for (int i = 0; i < this.appenderList.size(); i++) {
            Appender appender = this.appenderList.get(i);
            ct1.addCell(appender.getClass().getName());
        }

        table.addCell("appenders");
        table.addCell(ct1.toString(CharTable.Style.DB2));
        table.addCells("", "");

        table.addCell("alives");
        table.addCell(this.alive.toString());
        table.addCells("", "");

        table.addCell("levelManager");
        table.addCell(this.levelManager.toString());
        table.addCells("", "");
        return table.toString(CharTable.Style.DB2);
    }
}
