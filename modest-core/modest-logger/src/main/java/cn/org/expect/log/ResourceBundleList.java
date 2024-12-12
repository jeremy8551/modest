package cn.org.expect.log;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.JUL;
import cn.org.expect.util.SPI;

public class ResourceBundleList implements ResourceBundle {

    private final List<ResourceBundle> list;

    public ResourceBundleList() {
        this.list = new ArrayList<ResourceBundle>();
        this.load(null);
    }

    /**
     * 加载SPI机制配置的国际化信息
     */
    public synchronized void load(ClassLoader classLoader) {
        try {
            this.list.clear();
            this.list.addAll(SPI.load(ClassUtils.getClassLoader(classLoader), ResourceBundle.class));
        } catch (Throwable e) {
            JUL.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean contains(String key) {
        for (int i = 0; i < this.list.size(); i++) {
            ResourceBundle lp = this.list.get(i);
            if (lp.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public String get(String key) {
        for (int i = 0; i < this.list.size(); i++) {
            ResourceBundle lp = this.list.get(i);
            if (lp.contains(key)) {
                return lp.get(key);
            }
        }
        return null;
    }

    public Set<String> getKeys() {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        for (int i = 0; i < this.list.size(); i++) {
            ResourceBundle lp = this.list.get(i);
            set.addAll(lp.getKeys());
        }
        return set;
    }
}