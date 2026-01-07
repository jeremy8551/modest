package cn.org.expect.script.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.util.Ensure;

/**
 * 脚本引擎变量集合
 */
public class UniversalScriptVariableImpl implements UniversalScriptVariable {

    private final Map<String, Object> map;

    /**
     * 初始化
     */
    public UniversalScriptVariableImpl() {
        this.map = new HashMap<String, Object>();
    }

    /**
     * 转为变量名
     *
     * @param key 变量名
     * @return 变量名
     */
    private String toName(String key) {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException();
        } else {
            return key.toLowerCase();
        }
    }

    public boolean containsKey(Object key) {
        String name = this.toName((String) key);
        return this.map.containsKey(name);
    }

    public Object get(Object key) {
        String name = this.toName((String) key);
        return this.map.get(name);
    }

    public Object put(String key, Object value) {
        String name = this.toName(key);
        return this.map.put(name, value);
    }

    public void clear() {
        this.map.clear();
    }

    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return this.map.entrySet();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public Set<String> keySet() {
        return this.map.keySet();
    }

    public Object remove(Object key) {
        String name = this.toName((String) key);
        return this.map.remove(name);
    }

    public void putAll(Map<? extends String, ?> map) {
        Ensure.notNull(map);
        for (Entry<? extends String, ?> entry : map.entrySet()) {
            String key = this.toName(entry.getKey());
            this.put(key, entry.getValue());
        }
    }

    public int size() {
        return this.map.size();
    }

    public Collection<Object> values() {
        return this.map.values();
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
