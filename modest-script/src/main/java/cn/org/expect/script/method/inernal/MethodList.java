package cn.org.expect.script.method.inernal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.script.method.VariableMethodEntry;

/**
 * 变量方法集合（方法名、参数个数相等，但参数类型不同）
 */
public class MethodList {
    protected final static Log log = LogFactory.getLog(MethodList.class);

    /** 变量方法的集合 */
    private final List<VariableMethodEntry> list;

    public MethodList() {
        this.list = new ArrayList<VariableMethodEntry>();
    }

    public boolean contains(VariableMethodEntry entry) {
        for (VariableMethodEntry methodEntry : this.list) {
            if (methodEntry.equals(entry)) {
                return true;
            }
        }
        return false;
    }

    public void add(VariableMethodEntry entry) {
        this.list.add(entry);
    }

    public VariableMethodEntry getEntry(UniversalScriptVariableMethodParameters parameters) {
        for (VariableMethodEntry entry : this.list) {
            if (parameters.size() == entry.getParameters().length && parameters.startsWith(entry.getParameters())) {
                return entry;
            }

            if (log.isDebugEnabled()) {
                log.debug("script.stdout.message052", parameters.toStandardString(), entry.toStandardString());
            }
        }
        return null;
    }

    public VariableMethodEntry getVarargEntry(UniversalScriptVariableMethodParameters parameters) {
        for (VariableMethodEntry entry : this.list) {
            Class<?>[] array = entry.getParameters();
            if (parameters.size() >= array.length && parameters.startsWith(array) && parameters.startsWith(entry.getVarargClass(), array.length)) {
                return entry;
            }

            if (log.isDebugEnabled()) {
                log.debug("script.stdout.message052", parameters.toStandardString(), entry.toStandardString());
            }
        }
        return null;
    }

    /**
     * 对可变参数参数方法排序
     */
    public void sort() {
        Collections.sort(this.list, new Comparator<VariableMethodEntry>() {
            public int compare(VariableMethodEntry o1, VariableMethodEntry o2) {
                return o2.getParameters().length - o1.getParameters().length; // 参数个数从大到小
            }
        });
    }

    public List<VariableMethodEntry> values() {
        return list;
    }
}
