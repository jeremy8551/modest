package cn.org.expect.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.util.CharTable;
import cn.org.expect.util.Ensure;

/**
 * 日志级别管理器
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/21
 */
public class LogLevelManager {

    /**
     * 判断字符串参数是否是根日志级别
     *
     * @param name 字符串
     * @return 返回true表示字符串参数是根日志级别
     */
    public static boolean isRoot(String name) {
        return "*".equals(name) || "root".equalsIgnoreCase(name) || name.length() == 0;
    }

    /** 日志级别配置信息 */
    private final List<Entry> list;

    /** 默认日志级别 */
    private LogLevel root;

    /**
     * 初始化
     */
    public LogLevelManager() {
        this.list = new ArrayList<Entry>();
        this.root = LogLevel.INFO;
    }

    /**
     * 重置
     */
    public void reset() {
        this.list.clear();
        this.root = LogLevel.INFO;
    }

    /**
     * 返回集合
     *
     * @return 集合
     */
    public List<Entry> getEntryList() {
        return Collections.unmodifiableList(this.list);
    }

    /**
     * 添加日志级别配置信息
     *
     * @param name  包名或类名
     * @param level 日志级别
     */
    public synchronized void put(String name, LogLevel level) {
        Ensure.notNull(name);
        Ensure.notNull(level);

        if (isRoot(name)) {
            this.root = level;
        } else {
            for (Entry entry : this.list) {
                if (entry.name.equals(name)) {
                    entry.level = level;
                    return;
                }
            }
            this.list.add(new Entry(name, level));

            // 排序
            Collections.sort(this.list, new Comparator<Entry>() {
                public int compare(Entry o1, Entry o2) {
                    return -o1.name.compareTo(o2.name);
                }
            });
        }
    }

    /**
     * 查询日志级别
     *
     * @param name 包名或类名
     * @return 日志级别
     */
    public synchronized LogLevel get(String name) {
        if (name == null) {
            return this.root;
        }

        for (Entry entry : this.list) {
            if (name.startsWith(entry.name)) {
                return entry.level;
            }
        }
        return this.root; // 默认日志级别
    }

    public String toString() {
        CharTable ct = new CharTable();
        ct.addTitle(super.toString());
        ct.addTitle("");

        ct.addCell("default");
        ct.addCell(this.root);

        for (Entry entry : this.list) {
            ct.addCell(entry.name);
            ct.addCell(entry.level);
        }
        return ct.toString(CharTable.Style.DB2);
    }

    /**
     * 实体类
     */
    public static class Entry {

        /** 包名或类名 */
        private final String name;

        /** 日志级别 */
        private LogLevel level;

        public Entry(String name, LogLevel level) {
            this.name = name;
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public LogLevel getLevel() {
            return level;
        }
    }
}
