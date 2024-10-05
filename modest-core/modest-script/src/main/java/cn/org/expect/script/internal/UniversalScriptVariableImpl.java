package cn.org.expect.script.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

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

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public Object get(Object key) {
        return map.get(key);
    }

    public Object put(String key, Object value) {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException("Variable name can not be empty!");
        }

        String name = StringUtils.trimBlank(key); // 变量名的两端不许有空白字符
        return map.put(name, value);
    }

    public void clear() {
        map.clear();
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public Object remove(Object key) {
        if (key instanceof String) {
            return map.remove(key);
        } else {
            throw new ClassCastException();
        }
    }

    public void putAll(Map<? extends String, ?> map) {
        Ensure.notNull(map);
        for (Entry<? extends String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            this.put(key, entry.getValue());
        }
    }

    public int size() {
        return map.size();
    }

    public Collection<Object> values() {
        return map.values();
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
