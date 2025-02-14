package cn.org.expect.ioc.spi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.impl.DefaultBeanEntry;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.message.ResourceScanner;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.IO;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * 扫描 {@linkplain #RESOURCE_NAME} 文件
 *
 * @author jeremy8551@gmail.com
 * @createtime 2025/1/24
 */
public class BeanConfigScanner {
    private final static Log log = LogFactory.getLog(BeanConfigScanner.class);

    /** 资源文件路径 */
    public final static String RESOURCE_NAME = "META-INF/" + Settings.getProjectName() + "/bean.config";

    public BeanConfigScanner() {
    }

    /**
     * 加在类或接口的实例对象
     *
     * @return 异常集合
     */
    public List<EasyBeanEntry> load(ClassLoader classLoader) {
        List<EasyBeanEntry> list = new ArrayList<EasyBeanEntry>();
        ResourceScanner scanner = new ResourceScanner(classLoader, BeanConfigScanner.RESOURCE_NAME);
        while (scanner.hasNext()) {
            try {
                BufferedReader in = IO.getBufferedReader(new InputStreamReader(scanner.next(), CharsetName.UTF_8));
                String line;
                for (int lineNumber = 1; (line = in.readLine()) != null; lineNumber++) {
                    EasyBeanEntry entry = this.parse(classLoader, lineNumber, line);
                    if (entry != null) {
                        list.add(entry);
                    }
                }
            } catch (Throwable e) {
                this.process(e);
            }
        }
        return list;
    }

    protected EasyBeanEntry parse(ClassLoader classLoader, int lineNumber, String line) {
        String className = null;
        String name = "";
        String singleton = "false";
        String lazy = "false";
        String order = "0";
        String description = "";

        List<String> list = new ArrayList<String>();
        StringUtils.split(line, '|', list);
        for (String field : list) {
            String[] array = StringUtils.splitProperty(field);
            if (array == null) {
                if (log.isErrorEnabled()) {
                    log.error("ioc.stdout.message015", RESOURCE_NAME, lineNumber, line, field);
                }
                return null;
            }

            String key = StringUtils.trimBlank(array[0]);
            String value = StringUtils.trimBlank(array[1]);

            if (StringUtils.isBlank(value)) {
                continue;
            }

            if ("class".equalsIgnoreCase(key)) {
                className = value;
            } else if ("name".equalsIgnoreCase(key)) {
                name = value;
            } else if ("singleton".equalsIgnoreCase(key)) {
                singleton = value;
            } else if ("lazy".equalsIgnoreCase(key)) {
                lazy = value;
            } else if ("order".equalsIgnoreCase(key)) {
                order = value;
            } else if ("description".equalsIgnoreCase(key)) {
                description = value;
            }
        }

        if (StringUtils.isBlank(className)) {
            if (log.isErrorEnabled()) {
                log.error("ioc.stdout.message014", RESOURCE_NAME, lineNumber, line);
            }
            return null;
        }

        // 加载类信息
        Class<?> type = null;
        try {
            type = Class.forName(className, false, classLoader);
        } catch (Throwable e) {
            if (log.isErrorEnabled()) {
                log.error(className, e);
            }
        }

        if (type == null) {
            return null;
        }

        DefaultBeanEntry entry = new DefaultBeanEntry(type);
        entry.setName(name);
        entry.setSingleton(Boolean.parseBoolean(singleton));
        entry.setLazy(Boolean.parseBoolean(lazy));
        entry.setOrder(Integer.parseInt(order));
        entry.setDescription(description);
        return entry;
    }

    /**
     * 发生异常
     *
     * @param e 异常信息
     */
    protected void process(Throwable e) {
        log.error(e.getLocalizedMessage(), e);
    }
}
