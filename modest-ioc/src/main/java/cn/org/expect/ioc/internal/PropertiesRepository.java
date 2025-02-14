package cn.org.expect.ioc.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public synchronized boolean add(Properties properties, Comparator<Properties> comparator) {
        if (this.list.contains(properties)) {
            return false;
        }

        if (comparator != null) {
            this.list.add(properties);
            Collections.sort(this.list, comparator);
        } else {
            this.list.add(0, properties);
        }
        return true;
    }

    public String getProperty(String name) {
        String value;
        for (int i = 0; i < this.list.size(); i++) {
            Properties properties = this.list.get(i);
            value = properties.getProperty(name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public List<Properties> getProperties() {
        return Collections.unmodifiableList(this.list);
    }
}
