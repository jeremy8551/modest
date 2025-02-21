package cn.org.expect.message;

import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

public class ResourceMessageInternalBundle implements ResourceMessageBundle {

    /** 资源文件名 */
    public final static String RESOURCE_NAME = (Settings.getPackageName() + ".Messages").replace('.', '/');

    /** 属性集合 */
    protected final Properties properties = new Properties();

    public void load(ClassLoader classLoader) {
        // 默认资源
        this.load(classLoader, RESOURCE_NAME);

        // 自定义资源
        String name = Settings.getProperty(ResourcesUtils.PROPERTY_RESOURCE_NAME);
        if (StringUtils.isNotBlank(name)) {
            this.load(classLoader, name);
        }

        // 国际化资源
        String locale = StringUtils.trimBlank(Settings.getProperty(ResourcesUtils.PROPERTY_RESOURCE_LOCALE), '_'); // zh_CN
        if (StringUtils.isNotBlank(locale)) {
            this.load(classLoader, RESOURCE_NAME + "_" + locale);

            // 自定义资源
            if (StringUtils.isNotBlank(name)) {
                this.load(classLoader, name + "_" + locale);
            }
        }
    }

    protected void load(ClassLoader classLoader, String name) {
        ResourceScanner scanner = new ResourceScanner(classLoader, name + ".properties");
        while (scanner.hasNext()) {
            Properties properties = FileUtils.loadProperties(scanner.next());
            this.properties.putAll(properties);
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
