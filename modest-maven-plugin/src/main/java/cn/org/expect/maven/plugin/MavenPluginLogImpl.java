package cn.org.expect.maven.plugin;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.util.Logs;
import cn.org.expect.util.StringUtils;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.logging.Log;

public class MavenPluginLogImpl implements MavenPluginLog {

    private final Log log;

    private final Mojo mojo;

    public MavenPluginLogImpl(Mojo mojo) {
        this.mojo = mojo;
        this.log = mojo.getLog();
    }

    public String getName() {
        return this.mojo.getClass().getName();
    }

    public boolean isTraceEnabled() {
        return false;
    }

    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return log.isErrorEnabled();
    }

    public void trace(Object message, Object... args) {
    }

    public void debug(Object message, Object... args) {
        List<String> list = new ArrayList<String>();
        StringUtils.splitLines(Logs.toString(message, args), list);
        for (int i = 0; i < list.size(); i++) {
            log.debug(list.get(i));
        }
    }

    public void info(Object message, Object... args) {
        List<String> list = new ArrayList<String>();
        StringUtils.splitLines(Logs.toString(message, args), list);
        for (int i = 0; i < list.size(); i++) {
            log.info(list.get(i));
        }
    }

    public void warn(Object message, Object... args) {
        List<String> list = new ArrayList<String>();
        StringUtils.splitLines(Logs.toString(message, args), list);
        for (int i = 0; i < list.size(); i++) {
            log.warn(list.get(i));
        }
    }

    public void error(Object message, Object... args) {
        List<String> list = new ArrayList<String>();
        StringUtils.splitLines(Logs.toString(message, args), list);
        for (int i = 0; i < list.size(); i++) {
            log.error(list.get(i));
        }
    }

    public void fatal(Object message, Object... args) {
        List<String> list = new ArrayList<String>();
        StringUtils.splitLines(Logs.toString(message, args), list);
        for (int i = 0; i < list.size(); i++) {
            log.error(list.get(i));
        }
    }
}
