package cn.org.expect.log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.org.expect.message.ResourceMessageBundle;
import com.google.auto.service.AutoService;

@AutoService(ResourceMessageBundle.class)
public class ResourceMessageBundleImpl implements ResourceMessageBundle {

    private final Map<String, String> map;

    public ResourceMessageBundleImpl() {
        this.map = new HashMap<String, String>();
        this.map.put("a.b", "test ab");
        this.map.put("a.b.c", "test abc");
        this.map.put("a.b.c.d", "{0} {1}");
        this.map.put("a.b.c.d.e", "{}");
    }

    public void load(ClassLoader classLoader) {
    }

    public boolean contains(String key) {
        return this.map.containsKey(key);
    }

    public String get(String key) {
        return this.map.get(key);
    }

    public Set<String> getKeys() {
        return this.map.keySet();
    }
}
