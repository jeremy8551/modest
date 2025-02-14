package cn.org.expect.script.method.inernal;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.UniqueSequenceGenerator;

public class ClassTreeNode {
    private final static Log log = LogFactory.getLog(ClassTreeNode.class);

    final static UniqueSequenceGenerator generator = new UniqueSequenceGenerator("{}", 1);

    private final String id;

    private final Class<?> value;

    private ClassTreeNode parent;

    private final ArrayList<ClassTreeNode> children;

    public ClassTreeNode(Class<?> value) {
        this.id = generator.nextString();
        this.value = value;
        this.children = new ArrayList<ClassTreeNode>();
    }

    public List<ClassTreeNode> getChildren() {
        return children;
    }

    public Class<?> getValue() {
        return value;
    }

    /**
     * 添加子节点
     *
     * @param type 子节点
     */
    public void add(Class<?> type) {
        ClassTreeNode node = new ClassTreeNode(type);

        // 在2个节点中间插入节点
        if (ClassUtils.isAssignableFrom(type, this.value)) {
            ClassTreeNode parent = this.parent;
            node.parent = parent;
            this.parent = node;

            for (int i = parent.children.size() - 1; i >= 0; i--) {
                ClassTreeNode child = parent.children.get(i);
                if (ClassUtils.isAssignableFrom(type, child.value)) {
                    child.parent = node;
                    node.children.add(child);
                    parent.children.remove(child);
                }
            }

            parent.children.add(node);
            parent.children.remove(this);
            return;
        }

        // 遍历子节点
        for (ClassTreeNode child : this.children) {

            // 节点在子节点以下
            if (ClassUtils.isAssignableFrom(child.value, type)) {
                child.add(type);
                return;
            }

            if (ClassUtils.isAssignableFrom(type, child.value)) {
                child.add(type);
                return;
            }
        }

        // 在当前节点下添加一个子节点
        this.children.add(node);
        node.parent = this;

        if (log.isDebugEnabled()) {
            while (node.parent != null) {
                node = node.parent;
            }

            this.print(node, 0);
            log.debug("\n");
        }
    }

    /**
     * 打印树形结构
     *
     * @param node  节点
     * @param level 树的层高
     */
    public void print(ClassTreeNode node, int level) {
        log.debug(StringUtils.left("", level * 4, ' ') + node.getValue().getName());
        for (ClassTreeNode child : node.getChildren()) {
            print(child, level + 1);
        }
    }

    public boolean equals(Object obj) {
        return obj instanceof ClassTreeNode && ((ClassTreeNode) obj).id.equals(this.id);
    }
}
