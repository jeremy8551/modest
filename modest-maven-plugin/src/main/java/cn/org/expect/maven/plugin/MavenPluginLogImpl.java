package cn.org.expect.maven.plugin;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.util.Logs;
import cn.org.expect.util.StringUtils;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.logging.Log;

/**
 * 将模块日志输出适配到 Maven 插件日志
 */
public class MavenPluginLogImpl implements MavenPluginLog {

    private final Log log;

    private final Mojo mojo;

    /**
     * 初始化 MavenPluginLogImpl
     */
    public MavenPluginLogImpl(Mojo mojo) {
        this.mojo = mojo;
        this.log = mojo.getLog();
    }

    public String getName() {
        return this.mojo.getClass().getName();
    }

    /** {@inheritDoc} */
    public boolean isTraceEnabled() {
        return false;
    }

    /** {@inheritDoc} */
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    /** {@inheritDoc} */
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    /** {@inheritDoc} */
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    /** {@inheritDoc} */
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    /** {@inheritDoc} */
    public boolean isFatalEnabled() {
        return log.isErrorEnabled();
    }

    /** {@inheritDoc} */
    public void trace(Object message, Object... args) {
    }

    /** {@inheritDoc} */
    public void debug(Object message, Object... args) {
        List<String> list = new ArrayList<String>();
        StringUtils.splitLines(Logs.toString(message, args), list);
        for (int i = 0; i < list.size(); i++) {
            log.debug(list.get(i));
        }
    }

    /** {@inheritDoc} */
    public void info(Object message, Object... args) {
        List<String> list = new ArrayList<String>();
        StringUtils.splitLines(Logs.toString(message, args), list);
        for (int i = 0; i < list.size(); i++) {
            log.info(list.get(i));
        }
    }

    /** {@inheritDoc} */
    public void warn(Object message, Object... args) {
        List<String> list = new ArrayList<String>();
        StringUtils.splitLines(Logs.toString(message, args), list);
        for (int i = 0; i < list.size(); i++) {
            log.warn(list.get(i));
        }
    }

    /** {@inheritDoc} */
    public void error(Object message, Object... args) {
        List<String> list = new ArrayList<String>();
        StringUtils.splitLines(Logs.toString(message, args), list);
        for (int i = 0; i < list.size(); i++) {
            log.error(list.get(i));
        }
    }

    /** {@inheritDoc} */
    public void fatal(Object message, Object... args) {
        List<String> list = new ArrayList<String>();
        StringUtils.splitLines(Logs.toString(message, args), list);
        for (int i = 0; i < list.size(); i++) {
            log.error(list.get(i));
        }
    }
}
