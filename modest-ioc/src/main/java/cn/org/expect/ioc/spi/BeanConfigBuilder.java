package cn.org.expect.ioc.spi;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

public class BeanConfigBuilder {
    private final static Log log = LogFactory.getLog(BeanConfigBuilder.class);

    public static void main(String[] args) throws IOException {
        BeanConfigBuilder.create();
    }

    public static void create() throws IOException {
        ClassLoader classLoader = ClassUtils.getClassLoader();
        EasyContext context = new DefaultEasyContext(classLoader);
        List<Class<?>> beanClassList = context.getBeanClassList();

        LinkedHashMap<Class<?>, EasyBeanEntry> map = new LinkedHashMap<Class<?>, EasyBeanEntry>();

        String[] javaClassPath = ClassUtils.getClassPath();
        for (String classFilepath : javaClassPath) {
            if (!FileUtils.isDirectory(classFilepath)) {
                continue;
            }

            map.clear();
            StringBuilder buf = new StringBuilder();
            for (Class<?> type : beanClassList) {
                String beanFilepath = FileUtils.joinPath(classFilepath, type.getName().replace('.', '/') + ".class");
                if (!FileUtils.isFile(beanFilepath)) {
                    continue;
                }

                EasyBeanEntry entry = context.getBeanEntry(type);
                if (map.containsKey(entry.getType())) {
                    continue;
                } else {
                    map.put(entry.getType(), entry);
                }

                buf.append("class=");
                buf.append(entry.getType().getName());
                buf.append("|name=");
                buf.append(entry.getName());
                buf.append("|singleton=");
                buf.append(entry.singleton());
                buf.append("|lazy=");
                buf.append(entry.lazy());
                buf.append("|order=");
                buf.append(entry.getOrder());
                buf.append("|description=");
                buf.append(entry.getDescription());
                buf.append(FileUtils.LINE_SEPARATOR_UNIX);
            }

            if (StringUtils.isNotBlank(buf)) {
                String beanConfigFilepath = FileUtils.joinPath(classFilepath, BeanConfigScanner.RESOURCE_NAME); // 获取文件路径（class 文件所在目录）
                File file = new File(beanConfigFilepath);

                log.info("create file {}", file);
                log.info(buf);

                FileUtils.createFile(file);
                FileUtils.write(file, CharsetName.UTF_8, false, buf.toString());
            }
        }
    }
}
