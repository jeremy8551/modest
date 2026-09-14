package cn.org.expect.script.internal;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import cn.org.expect.util.CollectionUtils;
import cn.org.expect.util.Ensure;

/**
 * 维护脚本会话中的数据库编目信息
 */
public class ScriptCatalog extends Hashtable<String, Properties> {
    private final static long serialVersionUID = 1L;

    /**
     * 初始化 ScriptCatalog
     */
    public ScriptCatalog() {
        super();
    }

    public synchronized Properties put(String name, Properties value) {
        Ensure.notBlank(name);
        return super.put(name, value);
    }

    public void addAll(ScriptCatalog catalog) {
        if (catalog != null) {
            Set<String> names = catalog.keySet();
            for (String name : names) {
                Properties src = catalog.get(name);
                Properties copy = CollectionUtils.cloneProperties(src, new Properties());
                this.put(name, copy);
            }
        }
    }
}
