package cn.org.expect.script.method.inernal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.script.method.VariableMethodEntry;

/**
 * 方法集合（方法名相同、参数个数不同）
 */
public class MethodSizeMap {

    /** 参数个数 与 固定参数方法集合 的映射关系 */
    private final Map<Integer, MethodList> map;

    /** 可变参数方法集合（最右侧参数是 String... 类型的） */
    private final MethodList varargs;

    public MethodSizeMap() {
        this.map = new HashMap<Integer, MethodList>();
        this.varargs = new MethodList();
    }

    public boolean contains(VariableMethodEntry entry) {
        for (MethodList methodList : this.map.values()) {
            if (methodList.contains(entry)) {
                return true;
            }
        }
        return this.varargs.contains(entry);
    }

    public void add(VariableMethodEntry entry) {
        Integer key = entry.getParameters().length;
        MethodList list = this.map.get(key);
        if (list == null) {
            list = new MethodList();
            this.map.put(key, list);
        }

        if (entry.isVarArgs()) {
            this.varargs.add(entry);
            this.varargs.sort();
        } else {
            list.add(entry);
        }
    }

    /**
     * 查询固定参数的方法集合
     *
     * @param parameterSize 参数个数
     * @return 方法集合
     */
    public MethodList get(Integer parameterSize) {
        return this.map.get(parameterSize);
    }

    /**
     * 查询可变参数的方法集合
     *
     * @param parameters 参数集合
     * @return 方法信息
     */
    public VariableMethodEntry getVararg(UniversalScriptVariableMethodParameters parameters) {
        return this.varargs.getVarargEntry(parameters);
    }

    /**
     * 方法集合
     *
     * @return 方法集合
     */
    public List<VariableMethodEntry> values() {
        List<VariableMethodEntry> list = new ArrayList<VariableMethodEntry>();
        for (MethodList methodList : this.map.values()) {
            list.addAll(methodList.values());
        }
        list.addAll(this.varargs.values());
        return list;
    }
}
