package cn.org.expect.javax.script;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.script.Bindings;

import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.util.Ensure;

public class BindingsImpl implements Bindings {

    protected UniversalScriptVariable variable;

    public BindingsImpl(UniversalScriptVariable variable) {
        this.variable = Ensure.notNull(variable);
    }

    public Object put(String name, Object value) {
        return this.variable.put(name, value);
    }

    public void putAll(Map<? extends String, ?> toMerge) {
        this.variable.putAll(toMerge);
    }

    public void clear() {
        this.variable.clear();
    }

    public Set<String> keySet() {
        return this.variable.keySet();
    }

    public Collection<Object> values() {
        return this.variable.values();
    }

    public Set<Entry<String, Object>> entrySet() {
        return this.variable.entrySet();
    }

    public int size() {
        return this.variable.size();
    }

    public boolean isEmpty() {
        return this.variable.isEmpty();
    }

    public boolean containsKey(Object key) {
        return this.variable.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.variable.containsValue(value);
    }

    public Object get(Object key) {
        return this.variable.get(key);
    }

    public Object remove(Object key) {
        return this.variable.remove(key);
    }
}
