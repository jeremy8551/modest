package cn.org.expect.script.method.inernal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.org.expect.collection.CaseSensitivMap;
import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.script.method.VariableMethodEntry;

/**
 * 变量方法仓库
 */
public class ClassMethodCollection {

    /** 变量的类信息 */
    private final Class<?> variableClass;

    /** 父变量 */
    private ClassMethodCollection parent;

    /** 方法名 与 变量方法集合 的映射关系, 方法名全部为大写字母 */
    private final Map<String, MethodSizeMap> map;

    public ClassMethodCollection(Class<?> variableClass) {
        this.map = new CaseSensitivMap<MethodSizeMap>();
        this.variableClass = variableClass;
    }

    /**
     * 变量类信息
     *
     * @return 变量类信息
     */
    public Class<?> getVariableClass() {
        return variableClass;
    }

    /**
     * 设置 变量方法仓库
     *
     * @param parent 变量方法仓库
     */
    public void setParent(ClassMethodCollection parent) {
        this.parent = parent;
    }

    /**
     * 返回 变量方法仓库
     *
     * @return 变量方法仓库
     */
    public ClassMethodCollection getParent() {
        return parent;
    }

    /**
     * 判断是否已添加变量方法
     *
     * @param entry 变量方法的类信息
     * @return 返回true表示已添加，false表示未添加
     */
    public boolean contains(VariableMethodEntry entry) {
        for (MethodSizeMap map : this.map.values()) {
            if (map.contains(entry)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加变量方法
     *
     * @param entry 变量方法
     */
    public void add(VariableMethodEntry entry) {
        String name = entry.getName();
        MethodSizeMap map = this.map.get(name);
        if (map == null) {
            map = new MethodSizeMap();
            this.map.put(name, map);
        }
        map.add(entry);
    }

    /**
     * 返回变量方法
     *
     * @param name       方法名
     * @param parameters 方法参数
     * @return 变量方法
     */
    public VariableMethodEntry get(String name, UniversalScriptVariableMethodParameters parameters) {
        MethodSizeMap methodMap = this.map.get(name);
        if (methodMap != null) {
            MethodList list = methodMap.get(parameters.size());
            if (list != null) {
                VariableMethodEntry entry = list.getEntry(parameters);
                if (entry != null) {
                    return entry;
                }
            }

            // 查询可变参数方法
            VariableMethodEntry entry = methodMap.getVararg(parameters);
            if (entry != null) {
                return entry;
            }
        }

        return this.parent == null ? null : this.parent.get(name, parameters);
    }

    /**
     * 返回变量方法集合
     *
     * @param name 方法名
     * @return 变量方法集合
     */
    public List<VariableMethodEntry> get(String name) {
        List<VariableMethodEntry> list = new ArrayList<VariableMethodEntry>();
        MethodSizeMap map = this.map.get(name);
        if (map != null) {
            list.addAll(map.values());
        }
        return list;
    }

    /**
     * 返回变量方法名集合
     *
     * @return 方法名集合
     */
    public Set<String> getNames() {
        return this.map.keySet();
    }
}
