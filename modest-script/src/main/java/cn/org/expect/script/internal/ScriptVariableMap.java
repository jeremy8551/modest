package cn.org.expect.script.internal;

import java.util.HashMap;

import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;

public class ScriptVariableMap extends HashMap<String, Object> {

    private final UniversalScriptSession session;

    private final UniversalScriptContext context;

    public ScriptVariableMap(UniversalScriptSession session, UniversalScriptContext context) {
        super();
        this.session = session;
        this.context = context;
    }

    public boolean containsKey(Object key) {
        String name = (String) key;
        return this.context.containsVariable(name) || this.session.containsVariable(name);
    }

    public Object get(Object key) {
        String name = (String) key;

        if (this.context.containsLocalVariable(name)) {
            return this.context.getLocalVariable(name);
        }

        if (this.context.containsGlobalVariable(name)) {
            return this.context.getGlobalVariable(name);
        }

        if (this.session.containsVariable(name)) {
            return this.session.getVariable(name);
        }

        if (this.context.containsEnvironmentVariable(name)) {
            return this.context.getEnvironmentVariable(name);
        }

        return null;
    }
}
