package cn.org.expect.javax.script;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.script.Bindings;

import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.util.Ensure;

/**
 * 在 JSR 223 绑定接口与脚本变量之间提供适配
 */
public class ScriptVariableBindings implements UniversalScriptVariable {

    protected Bindings bindings;

    /**
     * 初始化 ScriptVariableBindings
     */
    public ScriptVariableBindings(Bindings bindings) {
        this.bindings = Ensure.notNull(bindings);
    }

    /** {@inheritDoc} */
    public Object put(String name, Object value) {
        return this.bindings.put(name, value);
    }

    /** {@inheritDoc} */
    public int size() {
        return this.bindings.size();
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return this.bindings.isEmpty();
    }

    /** {@inheritDoc} */
    public boolean containsKey(Object key) {
        return this.bindings.containsKey(key);
    }

    /** {@inheritDoc} */
    public boolean containsValue(Object value) {
        return this.bindings.containsValue(value);
    }

    public Object get(Object key) {
        return this.bindings.get(key);
    }

    /** {@inheritDoc} */
    public void clear() {
        this.bindings.clear();
    }

    /** {@inheritDoc} */
    public Set<String> keySet() {
        return this.bindings.keySet();
    }

    /** {@inheritDoc} */
    public Collection<Object> values() {
        return this.bindings.values();
    }

    public Set<Entry<String, Object>> entrySet() {
        return this.bindings.entrySet();
    }

    /** {@inheritDoc} */
    public Object remove(Object key) {
        return this.bindings.remove(key);
    }

    /** {@inheritDoc} */
    public void putAll(Map<? extends String, ?> m) {
        this.bindings.putAll(m);
    }

    /** {@inheritDoc} */
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
