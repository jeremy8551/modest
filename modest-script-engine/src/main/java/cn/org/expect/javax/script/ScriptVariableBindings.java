package cn.org.expect.javax.script;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.script.Bindings;

import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.util.Ensure;

public class ScriptVariableBindings implements UniversalScriptVariable {

    protected Bindings bindings;

    public ScriptVariableBindings(Bindings bindings) {
        this.bindings = Ensure.notNull(bindings);
    }

    public Object put(String name, Object value) {
        return this.bindings.put(name, value);
    }

    public int size() {
        return this.bindings.size();
    }

    public boolean isEmpty() {
        return this.bindings.isEmpty();
    }

    public boolean containsKey(Object key) {
        return this.bindings.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.bindings.containsValue(value);
    }

    public Object get(Object key) {
        return this.bindings.get(key);
    }

    public void clear() {
        this.bindings.clear();
    }

    public Set<String> keySet() {
        return this.bindings.keySet();
    }

    public Collection<Object> values() {
        return this.bindings.values();
    }

    public Set<Entry<String, Object>> entrySet() {
        return this.bindings.entrySet();
    }

    public Object remove(Object key) {
        return this.bindings.remove(key);
    }

    public void putAll(Map<? extends String, ?> m) {
        this.bindings.putAll(m);
    }

    public void putAll(Properties properties) {
        Ensure.notNull(properties);
        for (Object key : properties.keySet()) {
            if (key instanceof String) {
                String name = (String) key;
                String value = properties.getProperty(name);
                this.put(name, value);
            }
        }
    }
}
