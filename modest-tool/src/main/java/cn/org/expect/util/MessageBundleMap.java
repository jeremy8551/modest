package cn.org.expect.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MessageBundleMap implements MessageBundle {

    /** 资源集合 */
    private final List<MessageBundle> list;

    public MessageBundleMap() {
        this.list = new ArrayList<MessageBundle>();
        this.load(null);
    }

    /**
     * 加载SPI机制配置的国际化信息
     */
    public synchronized void load(ClassLoader classLoader) {
        try {
            this.list.clear();
            this.list.addAll(SPI.load(ClassUtils.getClassLoader(classLoader), MessageBundle.class));
        } catch (Throwable e) {
            JUL.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean contains(String key) {
        for (int i = 0; i < this.list.size(); i++) {
            MessageBundle lp = this.list.get(i);
            if (lp.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public String get(String key) {
        for (int i = 0; i < this.list.size(); i++) {
            MessageBundle lp = this.list.get(i);
            if (lp.contains(key)) {
                return lp.get(key);
            }
        }
        return null;
    }

    public Set<String> getKeys() {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        for (int i = 0; i < this.list.size(); i++) {
            MessageBundle lp = this.list.get(i);
            set.addAll(lp.getKeys());
        }
        return set;
    }
}
