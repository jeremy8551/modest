package cn.org.expect.message;

import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import cn.org.expect.ModestException;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

public class ResourceMessageInternalBundle implements ResourceMessageBundle {

    /** 资源文件名 */
    public final static String RESOURCE_NAME = (ModestException.class.getPackage().getName() + ".Messages").replace('.', '/');

    /** 属性集合 */
    protected final Properties properties = new Properties();

    public void load(ClassLoader classLoader) {
        ResourceScanner scanner1 = new ResourceScanner(classLoader, RESOURCE_NAME + ".properties");
        while (scanner1.hasNext()) {
            Properties properties = FileUtils.loadProperties(scanner1.next());
            this.properties.putAll(properties);
        }

        // 国际化资源
        String locale = StringUtils.trimBlank(System.getProperty(ResourcesUtils.PROPERTY_LOCALE), '_'); // zh_CN
        if (StringUtils.isNotBlank(locale)) {
            ResourceScanner scanner2 = new ResourceScanner(classLoader, RESOURCE_NAME + "_" + locale + ".properties");
            while (scanner2.hasNext()) {
                Properties properties = FileUtils.loadProperties(scanner2.next());
                this.properties.putAll(properties);
            }
        }
    }

    public boolean contains(String key) {
        return this.properties.containsKey(key);
    }

    public String get(String key) {
        return this.properties.getProperty(key);
    }

    public Set<String> getKeys() {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        Enumeration<?> enumeration = this.properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            Object element = enumeration.nextElement();
            if (element instanceof String) {
                set.add((String) element);
            }
        }
        return set;
    }
}
