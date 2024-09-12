package cn.org.expect.log.cxt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.WeakHashMap;

import cn.org.expect.log.Log;
import cn.org.expect.util.CharTable;

/**
 * 日志池
 * 用于保存所有已注册还存活的日志接口
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/11/21
 */
public class LogPool {

    /** 还存活的日志接口 */
    private WeakHashMap<String, Log> map;

    /** 按创建日志的顺序保存日志接口归属的类名 */
    private LinkedHashSet<String> names;

    /**
     * 日志池
     */
    public LogPool() {
        this.map = new WeakHashMap<String, Log>(40);
        this.names = new LinkedHashSet<String>(this.map.size());
    }

    /**
     * 添加日志
     *
     * @param log 日志接口
     */
    public synchronized void add(Log log) {
        String name = log.getName();
        this.map.put(name, log);
        this.names.add(name);
    }

    /**
     * 查询指定包下的日志接口
     *
     * @param name 包名或类名
     * @return 日志接口集合
     */
    public List<Log> get(String name) {
        Collection<Log> values = this.map.values();
        ArrayList<Log> list = new ArrayList<Log>(values.size());
        if (LogLevelManager.isRoot(name)) {
            list.addAll(values);
        } else {
            for (Log log : values) {
                if (log.getName() != null && log.getName().startsWith(name)) {
                    list.add(log);
                }
            }
        }
        return list;
    }

    public String toString() {
        CharTable ct = new CharTable();
        ct.addTitle(super.toString());

        for (String name : this.names) {
            Log log = this.map.get(name);
            if (log != null) {
                ct.addCell(log.toString());
            }
        }
        return ct.toString(CharTable.Style.db2);
    }
}
