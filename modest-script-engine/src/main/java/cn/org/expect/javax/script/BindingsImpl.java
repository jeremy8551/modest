package cn.org.expect.javax.script;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.script.Bindings;

import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.util.Ensure;

/**
 * 提供 JSR 223 变量绑定的默认实现
 */
public class BindingsImpl implements Bindings {

    protected UniversalScriptVariable variable;

    /**
     * 初始化 BindingsImpl
     */
    public BindingsImpl(UniversalScriptVariable variable) {
        this.variable = Ensure.notNull(variable);
    }

    /** {@inheritDoc} */
    public Object put(String name, Object value) {
        return this.variable.put(name, value);
    }

    /** {@inheritDoc} */
    public void putAll(Map<? extends String, ?> toMerge) {
        this.variable.putAll(toMerge);
    }

    /** {@inheritDoc} */
    public void clear() {
        this.variable.clear();
    }

    /** {@inheritDoc} */
    public Set<String> keySet() {
        return this.variable.keySet();
    }

    /** {@inheritDoc} */
    public Collection<Object> values() {
        return this.variable.values();
    }

    public Set<Entry<String, Object>> entrySet() {
        return this.variable.entrySet();
    }

    /** {@inheritDoc} */
    public int size() {
        return this.variable.size();
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return this.variable.isEmpty();
    }

    /** {@inheritDoc} */
    public boolean containsKey(Object key) {
        return this.variable.containsKey(key);
    }

    /** {@inheritDoc} */
    public boolean containsValue(Object value) {
        return this.variable.containsValue(value);
    }

    public Object get(Object key) {
        return this.variable.get(key);
    }

    /** {@inheritDoc} */
    public Object remove(Object key) {
        return this.variable.remove(key);
    }
}
