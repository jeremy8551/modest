package cn.org.expect.springboot.starter.script;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.util.Ensure;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * 将Spring容器的环境信息转为脚本引擎可用的环境信息
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/25
 */
public class SpringEnvironment implements UniversalScriptVariable {

    private final Environment env;

    public SpringEnvironment(ApplicationContext context) {
        this.env = Ensure.notNull(context).getEnvironment();
    }

    public boolean containsKey(Object key) {
        return this.env.getProperty((String) key) != null;
    }

    public String get(Object key) {
        return this.env.getProperty((String) key);
    }

    public Object put(String name, Object value) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map<? extends String, ?> toMerge) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    public String remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public Set<String> keySet() {
        throw new UnsupportedOperationException();
    }

    public Collection<Object> values() {
        throw new UnsupportedOperationException();
    }

    public Set<Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException();
    }

    public void putAll(Properties properties) {
        throw new UnsupportedOperationException();
    }
}
