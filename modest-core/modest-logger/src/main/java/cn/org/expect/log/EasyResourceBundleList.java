package cn.org.expect.log;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.JUL;
import cn.org.expect.util.SPI;

public class EasyResourceBundleList implements EasyResourceBundle {

    private final List<EasyResourceBundle> list;

    public EasyResourceBundleList() {
        this.list = new ArrayList<EasyResourceBundle>();
        this.load(null);
    }

    /**
     * 加载SPI机制配置的国际化信息
     */
    public synchronized void load(ClassLoader classLoader) {
        try {
            this.list.clear();
            this.list.addAll(SPI.load(ClassUtils.getClassLoader(classLoader), EasyResourceBundle.class));
        } catch (Throwable e) {
            JUL.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean contains(String key) {
        for (int i = 0; i < this.list.size(); i++) {
            EasyResourceBundle lp = this.list.get(i);
            if (lp.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public String get(String key) {
        for (int i = 0; i < this.list.size(); i++) {
            EasyResourceBundle lp = this.list.get(i);
            if (lp.contains(key)) {
                return lp.get(key);
            }
        }
        return null;
    }

    public Set<String> getKeys() {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        for (int i = 0; i < this.list.size(); i++) {
            EasyResourceBundle lp = this.list.get(i);
            set.addAll(lp.getKeys());
        }
        return set;
    }
}