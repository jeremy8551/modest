package cn.org.expect.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cn.org.expect.Modest;
import cn.org.expect.message.ResourceScanner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * 国家法定假日类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-04-15
 */
public class NationalHolidays {

    /** 国家法定假日配置文件所在目录 */
    public final static String PROPERTY_HOLIDAY = Settings.getPropertyName("holiday");

    /** 国家法定假日配置文件默认存储目录 */
    public final static File HOLIDAY_CONFIG_DIR = new File(Settings.getProjectHome(), "config");

    /** 默认的国家法定假日配置文件资源名 */
    public final static String RESOURCE_NAME = ClassUtils.getResourceName(Modest.class, "holidays.xml");

    /** 地区信息（zh_CN, en_US）与法定假日的映射关系 */
    private final LocaleMap map;

    /**
     * 初始化
     */
    public NationalHolidays() {
        this.map = new LocaleMap();
        this.reload();
    }

    /**
     * 重新加载所有国家与法定假日映射关系
     */
    public synchronized void reload() {
        this.map.clear();

        // 加载默认配置文件
        ResourceScanner scanner = new ResourceScanner(ClassUtils.getClassLoader(), RESOURCE_NAME);
        while (scanner.hasNext()) {
            try {
                this.load(this.map, scanner.next());
            } catch (Throwable e) {
                Logs.error("load {} error!", RESOURCE_NAME, e);
            }
        }

        // 扫描用户根目录下的配置文件
        this.loadChildFiles(this.map, HOLIDAY_CONFIG_DIR);

        // 使用外部配置的国家法定假日配置文件目录
        String filepath = StringUtils.trimBlank(Settings.getProperty(PROPERTY_HOLIDAY));
        if (StringUtils.isNotBlank(filepath)) {
            if (FileUtils.isDirectory(filepath)) {
                this.loadChildFiles(this.map, new File(filepath));
            } else {
                Logs.error("-D{}={}, The filepath is illegal!", PROPERTY_HOLIDAY, filepath);
            }
        }
    }

    /**
     * 加载目录中所有以 holiday 开头的 xml 文件
     *
     * @param map 国家与法定假映射关系
     * @param dir 目录
     */
    protected synchronized void loadChildFiles(LocaleMap map, File dir) {
        if (FileUtils.isDirectory(dir)) {
            File[] files = FileUtils.array(dir.listFiles());
            for (File file : files) {
                if (file.isFile()) {
                    String filename = file.getName();
                    String filenameExt = FileUtils.getFilenameExt(filename);
                    if (filename.startsWith("holiday") && "xml".equalsIgnoreCase(filenameExt)) {
                        try {
                            this.load(map, new FileInputStream(file));
                        } catch (IOException e) {
                            Logs.error("load {} error!", file.getAbsolutePath(), e);
                        }
                    }
                }
            }
        }
    }

    /**
     * 加载默认的国家法定假日配置
     *
     * @param map 国家与法定假日的映射关系
     * @param in  XML文件输入流
     */
    protected synchronized void load(LocaleMap map, InputStream in) {
        if (in == null) {
            return;
        }

        Document doc = XMLUtils.newDocument(in);
        Node root = XMLUtils.getChildNode(doc, "holidays");
        if (root == null) {
            return;
        }

        List<Node> locales = XMLUtils.getChildNodes(root, "locale");
        for (Node localeNode : locales) {
            Locale locale = new Locale();
            locale.setName(XMLUtils.getAttribute(localeNode, "name"));

            List<Node> dateNodes = XMLUtils.getChildNodes(localeNode, "date");
            for (Node node : dateNodes) {
                String value = XMLUtils.getAttribute(node, "value");
                String reset = XMLUtils.getAttribute(node, "reset", "true");

                if (StringUtils.parseBoolean(reset).booleanValue()) {
                    locale.getRest().add(Dates.parse(value));
                } else {
                    locale.getWork().add(Dates.parse(value));
                }
            }

            map.add(locale);
        }
    }

    /**
     * 根据地区信息查询对应的集合
     *
     * @param locale 国家信息
     * @return 法定假日集合
     */
    public synchronized Locale get(String locale) {
        return this.map.get(locale);
    }

    /**
     * 判断日期是否是国家法定假日
     *
     * @param locale 语言与国家地区信息，如: zh_CN, en_US
     * @param date   日期
     * @return 返回true表示日期是休息日 false表示日期是工作日
     */
    public synchronized boolean isRestDay(String locale, Date date) {
        Locale holidays = this.map.get(locale);
        return holidays != null && holidays.getRest().contains(date);
    }

    /**
     * 判断日期是否是国家法定工作日
     *
     * @param locale 语言与国家地区信息，如: zh_CN, en_US
     * @param date   日期
     * @return 返回true表示日期是休息日 false表示日期是工作日
     */
    public synchronized boolean isWorkDay(String locale, Date date) {
        Locale holidayLocale = this.map.get(locale);
        return holidayLocale != null && holidayLocale.getWork().contains(date);
    }

    /**
     * 国家与法定假日的映射关系
     */
    public static class LocaleMap {

        /** 国家地区与法定假日的映射关系 */
        private final Map<String, Locale> map;

        public LocaleMap() {
            this.map = new ConcurrentHashMap<String, Locale>();
        }

        public Locale get(String locale) {
            String key = locale.toUpperCase();
            return this.map.get(key);
        }

        public void add(Locale locale) {
            String key = locale.getName().toUpperCase();
            Locale localeConfig = this.map.get(key);
            if (localeConfig == null) {
                this.map.put(key, locale);
            } else {
                localeConfig.getWork().addAll(locale.getWork());
                localeConfig.getRest().addAll(locale.getRest());
            }
        }

        public void clear() {
            this.map.clear();
        }
    }

    /**
     * 法定假日集合
     */
    public static class Locale {

        /** 国家信息 */
        private String name;

        /** 工作日 */
        private final Set<Date> work;

        /** 休息日 */
        private final Set<Date> rest;

        public Locale() {
            this.work = new HashSet<Date>();
            this.rest = new HashSet<Date>();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<Date> getWork() {
            return work;
        }

        public Set<Date> getRest() {
            return rest;
        }
    }
}
