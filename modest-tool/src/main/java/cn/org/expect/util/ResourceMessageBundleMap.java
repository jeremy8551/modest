package cn.org.expect.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ResourceMessageBundleMap implements ResourceMessageBundle {

    /** 类加载器 */
    private ClassLoader classLoader;

    /** 国际化资源信息集合 */
    private final List<ResourceMessageBundle> list;

    public ResourceMessageBundleMap(ClassLoader classLoader) {
        this.list = new ArrayList<ResourceMessageBundle>();
        this.classLoader = classLoader;
        this.load(classLoader);
    }

    /**
     * 设置（SPI加载国际化资源信息时使用的）类加载器
     *
     * @param classLoader 类加载器
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * SPI机制加载国际化资源信息
     */
    public void load() {
        this.load(this.classLoader);
    }

    /**
     * SPI机制加载国际化资源信息
     */
    public synchronized void load(ClassLoader classLoader) {
        ClassLoader loader = ClassUtils.getClassLoader(classLoader);

        // SPI机制加载
        List<ResourceMessageBundle> list = null;
        try {
            list = SPI.load(loader, ResourceMessageBundle.class);
        } catch (Throwable e) {
            JUL.error(e.getLocalizedMessage(), e);
        }

        if (list != null) {
            this.list.clear();
            this.list.addAll(list);
        }
    }

    public boolean contains(String key) {
        for (int i = 0; i < this.list.size(); i++) {
            ResourceMessageBundle lp = this.list.get(i);
            if (lp.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public String get(String key) {
        for (int i = 0; i < this.list.size(); i++) {
            ResourceMessageBundle lp = this.list.get(i);
            if (lp.contains(key)) {
                return lp.get(key);
            }
        }
        return null;
    }

    public Set<String> getKeys() {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        for (int i = 0; i < this.list.size(); i++) {
            ResourceMessageBundle lp = this.list.get(i);
            set.addAll(lp.getKeys());
        }
        return set;
    }
}
