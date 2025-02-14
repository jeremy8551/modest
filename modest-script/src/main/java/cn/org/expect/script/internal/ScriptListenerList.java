package cn.org.expect.script.internal;

import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import cn.org.expect.script.UniversalCommandResultSet;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptListener;
import cn.org.expect.script.UniversalScriptListenerList;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.util.Ensure;

public class ScriptListenerList implements UniversalScriptListenerList {

    /** 监听器类信息与监听器映射集合 */
    private final LinkedHashMap<Class<?>, UniversalScriptListener> map;

    /** 监听器集合（为了加快遍历速度） */
    private final ArrayList<UniversalScriptListener> list;

    public ScriptListenerList() {
        this.map = new LinkedHashMap<Class<?>, UniversalScriptListener>();
        this.list = new ArrayList<UniversalScriptListener>();
        this.addListener(new DefaultUniversalScriptListener());
    }

    protected synchronized void addListener(UniversalScriptListener listener) {
        Ensure.notNull(listener);
        this.map.put(listener.getClass(), listener);

        // 先清空，再重新添加
        this.list.clear();
        Set<Class<?>> set = this.map.keySet();
        for (Class<?> cls : set) {
            UniversalScriptListener next = this.map.get(cls);
            this.list.add(next);
        }
    }

    public void add(UniversalScriptListener listener) {
        if (listener instanceof DefaultUniversalScriptListener) { // 不能添加默认监听器
            return;
        }

        this.addListener(listener);
    }

    public void addAll(UniversalScriptListenerList list) {
        if (list != null) {
            for (UniversalScriptListener listener : list.values()) {
                this.add(listener);
            }
        }
    }

    public boolean remove(Class<? extends UniversalScriptListener> cls) {
        UniversalScriptListener listener = this.map.remove(cls);
        return listener != null && this.list.remove(listener);
    }

    public UniversalScriptListener get(Class<? extends UniversalScriptListener> cls) {
        return this.map.get(cls);
    }

    public List<UniversalScriptListener> values() {
        return this.list;
    }

    public boolean contains(Class<? extends UniversalScriptListener> cls) {
        return this.map.containsKey(cls);
    }

    public boolean beforeCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptCommand command) throws Exception {
        boolean value = true;
        for (int i = 0, size = this.list.size(); i < size; i++) {
            UniversalScriptListener listener = this.list.get(i);
            if (!listener.beforeCommand(session, context, stdout, stderr, command)) {
                value = false;
            }
        }
        return value;
    }

    public void afterCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) throws Exception {
        for (int i = 0, size = this.list.size(); i < size; i++) {
            UniversalScriptListener listener = this.list.get(i);
            listener.afterCommand(session, context, stdout, stderr, forceStdout, command, result);
        }
    }

    public void catchCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Exception e) throws Exception {
        boolean value = false;
        for (int i = 0, size = this.list.size(); i < size; i++) {
            UniversalScriptListener listener = this.list.get(i);
            if (listener.catchCommand(session, context, stdout, stderr, forceStdout, command, result, e)) {
                value = true;
            }
        }

        // 没有执行任何业务逻辑时，抛出异常信息
        if (!value) {
            String script = (command == null) ? "" : command.getScript();
            throw new UniversalScriptException("script.stderr.message108", script, e);
        }
    }

    public void startEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, Reader in) throws Exception {
        for (int i = 0, size = this.list.size(); i < size; i++) {
            UniversalScriptListener listener = this.list.get(i);
            listener.startEvaluate(session, context, stdout, stderr, forceStdout, in);
        }
    }

    public void exitEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result) throws Exception {
        for (int i = 0, size = this.list.size(); i < size; i++) {
            UniversalScriptListener listener = this.list.get(i);
            listener.exitEvaluate(session, context, stdout, stderr, forceStdout, command, result);
        }
    }

    public void catchEvaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, UniversalScriptCommand command, UniversalCommandResultSet result, Exception e) throws Exception {
        String script = (command == null) ? "" : command.getScript();
        if (this.list.isEmpty()) {
            throw new UniversalScriptException("script.stderr.message108", script, e);
        }

        boolean value = false;
        for (int i = 0, size = this.list.size(); i < size; i++) {
            UniversalScriptListener listener = this.list.get(i);
            if (listener.catchEvaluate(session, context, stdout, stderr, forceStdout, command, result, e)) {
                value = true;
            }
        }

        if (!value) { // 没有执行任何业务逻辑时，抛出异常信息
            throw e;
        }
    }
}
