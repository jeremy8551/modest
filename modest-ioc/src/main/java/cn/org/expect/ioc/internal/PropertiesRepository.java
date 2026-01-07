package cn.org.expect.ioc.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import cn.org.expect.ioc.EasyPropertyProvider;

/**
 * 属性仓库
 */
public class PropertiesRepository implements EasyPropertyProvider {

    private final List<Properties> list;

    public PropertiesRepository() {
        this.list = new ArrayList<Properties>();
    }

    public boolean hasProperty(String name) {
        for (int i = 0; i < this.list.size(); i++) {
            Properties properties = this.list.get(i);
            if (properties.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean addProperties(Properties properties) {
        if (this.list.contains(properties)) {
            return false;
        }

        this.list.add(0, properties);
        return true;
    }

    public String getProperty(String name) {
        for (int i = 0; i < this.list.size(); i++) {
            Properties properties = this.list.get(i);
            if (properties.containsKey(name)) {
                return properties.getProperty(name);
            }
        }
        return null;
    }

    public List<Properties> getProperties() {
        return Collections.unmodifiableList(this.list);
    }
}
