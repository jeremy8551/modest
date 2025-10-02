package cn.org.expect.database.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.org.expect.database.DatabaseDialect;
import cn.org.expect.database.DatabaseException;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyBeanEntryCollection;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.EasyContextAware;
import cn.org.expect.ioc.impl.DefaultBeanEntry;
import cn.org.expect.ioc.impl.DefaultBeanEntryCollection;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.StringUtils;
import com.google.auto.service.AutoService;

@AutoService(EasyBeanEntryCollection.class)
public class DatabaseDialectCollection implements EasyBeanEntryCollection, EasyContextAware {
    private final static Log log = LogFactory.getLog(DatabaseDialectCollection.class);

    /** 容器上下文信息 */
    private EasyContext context;

    /** 数据库名与方言仓库的映射关系 */
    private final Map<String, DefaultBeanEntryCollection> map;

    public DatabaseDialectCollection() {
        this.map = new LinkedHashMap<String, DefaultBeanEntryCollection>();
    }

    public Class<?> getBeanClass() {
        return DatabaseDialect.class;
    }

    public boolean contains(EasyBeanEntry entry) {
        for (DefaultBeanEntryCollection repository : this.map.values()) {
            if (repository.contains(entry)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Class<?> type) {
        for (DefaultBeanEntryCollection repository : this.map.values()) {
            if (repository.contains(type)) {
                return true;
            }
        }
        return false;
    }

    public boolean add(EasyBeanEntry entry) {
        if (log.isDebugEnabled()) {
            log.debug("database.stdout.message002", entry.getName(), entry.getType().getName()); // 注册数据库方言类 {0} -> {1}
        }

        // 创建一个数据库方言的实例
        DatabaseDialect dialect = context.newInstance(entry.getType());
        entry.setBean(dialect);

        String key = entry.getName().toUpperCase();
        DefaultBeanEntryCollection collection = this.map.get(key);
        if (collection == null) {
            collection = new DefaultBeanEntryCollection(DatabaseDialect.class);
            this.map.put(key, collection);
        }
        return collection.add(new DialectEntry(entry, dialect.getDatabaseMajorVersion(), dialect.getDatabaseMinorVersion()));
    }

    public List<EasyBeanEntry> remove(Class<?> type) {
        List<EasyBeanEntry> list = new ArrayList<EasyBeanEntry>();
        for (DefaultBeanEntryCollection collection : this.map.values()) {
            list.addAll(collection.remove(type));
        }
        return list;
    }

    public EasyBeanEntryCollection get(String name) {
        String key = name.toUpperCase();
        return this.map.get(key);
    }

    public EasyBeanEntryCollection filter(Filter filter) {
        DefaultBeanEntryCollection collection = new DefaultBeanEntryCollection(this.getBeanClass());
        for (DefaultBeanEntryCollection entryCollection : this.map.values()) {
            DefaultBeanEntryCollection filterRepo = entryCollection.filter(filter);
            for (EasyBeanEntry entry : filterRepo.values()) {
                collection.add(entry);
            }
        }
        return collection;
    }

    public EasyBeanEntry head() {
        throw new UnsupportedOperationException();
    }

    public Class<?> get(String name, final String major, final String minor) {
        String key = name.toUpperCase();
        EasyBeanEntryCollection collection = this.map.get(key);
        if (collection == null) {
            throw new DatabaseException("database.stdout.message003", name);
        }

        // 根据版本号进行过滤
        if (StringUtils.isNotBlank(major) || StringUtils.isNotBlank(minor)) {
            EasyBeanEntry entry = collection.filter(new EasyBeanEntryCollection.Filter() {
                public boolean accept(EasyBeanEntry entry) {
                    DialectEntry dialectEntry = (DialectEntry) entry;
                    return dialectEntry.getMajor().equals(major) && dialectEntry.getMinor().equals(minor);
                }
            }).head();

            if (entry != null) {
                return entry.getType();
            }
        }

        // 如果没有与数据库版本匹配的方言类，则取版本号为空的作为默认
        EasyBeanEntry entry = collection.filter(new EasyBeanEntryCollection.Filter() {
            public boolean accept(EasyBeanEntry entry) {
                DialectEntry dialectBeanEntry = (DialectEntry) entry;
                return dialectBeanEntry.getMajor().length() == 0 && dialectBeanEntry.getMinor().length() == 0;
            }
        }).head();

        if (entry == null) {
            throw new DatabaseException("database.stdout.message003", name);
        } else {
            return entry.getType();
        }
    }

    public void sort() {
        Comparator<EasyBeanEntry> comparator = new Comparator<EasyBeanEntry>() {
            public int compare(EasyBeanEntry metadata1, EasyBeanEntry metadata2) {
                return this.compareTo((DialectEntry) metadata1, (DialectEntry) metadata2);
            }

            public int compareTo(DialectEntry o1, DialectEntry o2) {
                int nv = o1.getName().compareTo(o2.getName());
                if (nv != 0) {
                    return nv;
                }

                int mv = o1.getMajor().compareTo(o2.getMajor());
                if (mv != 0) {
                    return mv;
                }

                int iv = o1.getMinor().compareTo(o2.getMinor());
                if (iv != 0) {
                    return iv;
                }

                return o1.getOrder() - o2.getOrder(); // 倒序排序
            }
        };

        for (DefaultBeanEntryCollection collection : this.map.values()) {
            collection.sort(comparator);
        }
    }

    public List<EasyBeanEntry> values() {
        List<EasyBeanEntry> list = new ArrayList<EasyBeanEntry>();
        for (DefaultBeanEntryCollection collection : this.map.values()) {
            list.addAll(collection.values());
        }
        return list;
    }

    /**
     * 根据字符串参数 {@code str} 解析数据库名
     *
     * @param str 字符串
     * @return 组件名, 如：db2 oracle mysql
     */
    public String parseName(String str) {
        String key = str.toUpperCase();
        Set<String> names = this.map.keySet();
        for (Iterator<String> it = names.iterator(); it.hasNext(); ) {
            String name = it.next();
            if (key.contains(name.toUpperCase())) {
                return name;
            }
        }
        throw new DatabaseException("database.stdout.message003", str);
    }

    public void setContext(EasyContext context) {
        this.context = context;
    }

    public static class DialectEntry extends DefaultBeanEntry {
        private final String major;

        private final String minor;

        public DialectEntry(EasyBeanEntry entry, String major, String minor) {
            super(entry.getType());
            this.copy(entry);
            this.major = StringUtils.coalesce(major, "");
            this.minor = StringUtils.coalesce(minor, "");
        }

        public String getMajor() {
            return major;
        }

        public String getMinor() {
            return minor;
        }
    }
}
