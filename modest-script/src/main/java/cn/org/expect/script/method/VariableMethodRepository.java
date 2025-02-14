package cn.org.expect.script.method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.script.method.inernal.ClassMethodCollection;
import cn.org.expect.script.method.inernal.ClassTreeNode;
import cn.org.expect.script.method.inernal.MethodLoader;
import cn.org.expect.util.CharTable;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringComparator;
import cn.org.expect.util.StringUtils;

/**
 * 变量方法的仓库
 *
 * @author jeremy8551@gmail.com
 */
@EasyBean(singleton = true)
public class VariableMethodRepository {
    private final static Log log = LogFactory.getLog(VariableMethodRepository.class);

    /** 变量类信息 与 变量方法 的映射关系, 变量方法名全部为大写字母 */
    private final Map<Class<?>, ClassMethodCollection> map;

    public VariableMethodRepository(EasyContext ioc) {
        this.map = new HashMap<Class<?>, ClassMethodCollection>();
        new MethodLoader(ioc, this);
    }

    /**
     * 判断变量方法是否存在
     *
     * @param entry 变量方法的类
     * @return 返回true表示存在
     */
    public boolean contains(VariableMethodEntry entry) {
        for (ClassMethodCollection collection : this.map.values()) {
            if (collection.contains(entry)) {
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
        this.computeIfAbsent(entry.getVariableClass()).add(entry);
    }

    /**
     * 返回变量方法信息
     *
     * @param variable   变量
     * @param name       方法名
     * @param parameters 参数
     * @return 变量方法
     */
    public VariableMethodEntry get(Object variable, String name, UniversalScriptVariableMethodParameters parameters) {
        return this.computeIfAbsent(variable.getClass()).get(name, parameters);
    }

    /**
     * 返回变量方法仓库
     *
     * @param type 变量的类信息
     * @return 变量方法仓库
     */
    protected ClassMethodCollection computeIfAbsent(Class<?> type) {
        ClassMethodCollection collection = this.map.get(type);
        if (collection == null) {
            collection = new ClassMethodCollection(type);
            this.map.put(type, collection);

            if (log.isDebugEnabled()) {
                log.debug("script.stdout.message003", type.getName());
            }
            this.refresh();
        }
        return collection;
    }

    public void refresh() {
        if (log.isDebugEnabled()) {
            log.debug("script.stdout.message004");
        }

        List<Class<?>> list = new ArrayList<Class<?>>(this.map.keySet());
        ClassTreeNode root = new ClassTreeNode(Object.class);
        for (int i = 0; i < list.size(); i++) {
            Class<?> type = list.get(i);
            if (!type.equals(Object.class)) {
                if (log.isDebugEnabled()) {
                    log.debug("script.stdout.message005", type.getName());
                }

                root.add(type);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("script.stdout.message006");
        }
        this.updateParent(root);
    }

    /**
     * 根据最新类继承的树形结构，更新父集合
     *
     * @param node 节点
     */
    public void updateParent(ClassTreeNode node) {
        ClassMethodCollection repository = this.map.get(node.getValue());
        for (ClassTreeNode child : node.getChildren()) {
            ClassMethodCollection childRepo = this.map.get(child.getValue());
            childRepo.setParent(repository);
            this.updateParent(child);
        }
    }

    /**
     * 返回所有脚本变量方法的使用说明
     *
     * @return 使用说明
     */
    public CharTable toCharTable() {
        CharTable table = new CharTable(CharsetUtils.get());

        // 标题
        String[] titles = StringUtils.split(ResourcesUtils.getMessage("script.stdout.message002"), ',');
        table.addTitle(titles[0], CharTable.ALIGN_LEFT);
        table.addTitle(titles[1], CharTable.ALIGN_LEFT);

        // 方法名的集合
        List<String> names = new ArrayList<String>();
        for (ClassMethodCollection repository : this.map.values()) {
            names.addAll(repository.getNames());
        }
        Collections.sort(names, new StringComparator());

        for (String name : names) {
            // 变量方法的实现类
            List<VariableMethodEntry> methods = new ArrayList<VariableMethodEntry>();
            for (ClassMethodCollection repository : this.map.values()) {
                methods.addAll(repository.get(name));
            }

            table.addCell(this.toMethodString(methods));

            StringBuilder buf = new StringBuilder();
            for (VariableMethodEntry entry : methods) { // 同名、不同参数的方法
                buf.append(entry.getMethodInfo());
                buf.append(Settings.LINE_SEPARATOR);
            }
            table.addCell(StringUtils.rtrimBlank(buf));
        }

        return table;
    }

    public String toStandardString() {
        return this.toCharTable().toString(CharTable.Style.STANDARD);
    }

    private String toMethodString(List<VariableMethodEntry> methods) {
        StringBuilder buf = new StringBuilder();
        for (VariableMethodEntry entry : methods) { // 同名、不同参数的方法
            buf.append(entry.toStandardString());
            buf.append(Settings.LINE_SEPARATOR);
        }
        return StringUtils.rtrimBlank(buf);
    }

    /**
     * 判断是否已加载脚本引擎变量方法
     *
     * @return 返回 true 表示未加载任何脚本引擎变量方法
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * 清空所有信息
     */
    public void clear() {
        this.map.clear();
    }

    /**
     * 变量的类信息与变量方法的映射
     *
     * @return 集合
     */
    public Map<Class<?>, ClassMethodCollection> values() {
        return map;
    }
}
