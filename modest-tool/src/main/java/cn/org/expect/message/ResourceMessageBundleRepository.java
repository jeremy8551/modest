package cn.org.expect.message;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cn.org.expect.util.SPI;

/**
 * 国际化资源仓库
 */
public class ResourceMessageBundleRepository implements ResourceMessageBundle {

    /** 类加载器 */
    private volatile ClassLoader classLoader;

    /** 国际化资源信息集合 */
    private final List<ResourceMessageBundle> list;

    public ResourceMessageBundleRepository(ClassLoader classLoader) {
        this.list = new ArrayList<ResourceMessageBundle>();
        this.load(classLoader);
    }

    public void load() {
        this.load(this.classLoader);
    }

    public synchronized void load(ClassLoader classLoader) {
        if (classLoader == null) {
            throw new NullPointerException();
        }

        this.classLoader = classLoader;
        List<ResourceMessageBundle> list = SPI.load(this.classLoader, ResourceMessageBundle.class); // SPI 机制加载
        this.list.clear();
        for (ResourceMessageBundle bundle : list) {
            bundle.load(this.classLoader);
            this.list.add(bundle);
        }
    }

    public boolean contains(String key) {
        if (key != null) {
            for (int i = 0; i < this.list.size(); i++) {
                if (this.list.get(i).contains(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String get(String key) {
        if (key != null) {
            for (int i = 0; i < this.list.size(); i++) {
                ResourceMessageBundle bundle = this.list.get(i);
                if (bundle.contains(key)) {
                    return bundle.get(key);
                }
            }
        }
        return null;
    }

    public Set<String> getKeys() {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        for (int i = 0; i < this.list.size(); i++) {
            ResourceMessageBundle bundle = this.list.get(i);
            set.addAll(bundle.getKeys());
        }
        return set;
    }

    /**
     * 返回国际化资源文件集合
     *
     * @return 国际化资源
     */
    public List<ResourceMessageBundle> getBundleList() {
        return list;
    }
}
