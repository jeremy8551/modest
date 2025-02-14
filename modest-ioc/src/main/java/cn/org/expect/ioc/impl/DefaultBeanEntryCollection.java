package cn.org.expect.ioc.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.ioc.BeanRepeatDefineException;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyBeanEntryCollection;
import cn.org.expect.ioc.EasyBeanInstance;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.CharTable;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.StringUtils;

/**
 * 组件的实现类集合
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/24
 */
public class DefaultBeanEntryCollection implements EasyBeanEntryCollection {
    private final static Log log = LogFactory.getLog(DefaultBeanEntryCollection.class);

    public final static Comparator<EasyBeanEntry> COMPARATOR = Collections.reverseOrder(new Comparator<EasyBeanEntry>() {
        public int compare(EasyBeanEntry entry1, EasyBeanEntry entry2) {
            int v = entry1.getName().compareTo(entry2.getName());
            if (v != 0) {
                return v;
            } else {
                return entry1.getOrder() - entry2.getOrder(); // 倒序排序
            }
        }
    });

    /** 类或接口 **/
    protected Class<?> type;

    /** 组件信息集合 **/
    protected List<EasyBeanEntry> list;

    /**
     * 初始化
     *
     * @param type 集合中元素所属的组件类信息
     */
    public DefaultBeanEntryCollection(Class<?> type) {
        this.list = new ArrayList<EasyBeanEntry>();
        this.type = type;
    }

    public Class<?> getBeanClass() {
        return this.type;
    }

    public boolean add(EasyBeanEntry entry) {
        if (log.isDebugEnabled()) {
            log.debug("ioc.stdout.message005", type.getName(), entry.getType().getName());
        }
        return this.list.add(entry);
    }

    public boolean contains(EasyBeanEntry entry) {
        return this.contains(entry.getType());
    }

    public boolean contains(Class<?> type) {
        for (EasyBeanEntry entry : this.list) {
            if (ClassUtils.equals(entry.getType(), type)) {
                return true;
            }
        }
        return false;
    }

    public List<EasyBeanEntry> remove(Class<?> type) {
        List<EasyBeanEntry> list = new ArrayList<EasyBeanEntry>();
        for (EasyBeanEntry entry : this.list) {
            if (ClassUtils.equals(entry.getType(), type)) {
                list.add(entry);
            }
        }
        return list;
    }

    public DefaultBeanEntryCollection get(String name) {
        if (StringUtils.isBlank(name)) {
            return this;
        }

        DefaultBeanEntryCollection repository = new DefaultBeanEntryCollection(this.type);
        for (int i = 0, size = this.list.size(); i < size; i++) {
            EasyBeanEntry entry = this.list.get(i);
            if (entry.getName().equalsIgnoreCase(name)) {
                repository.add(entry);
            }
        }
        return repository;
    }

    public DefaultBeanEntryCollection filter(EasyBeanEntryCollection.Filter filter) {
        DefaultBeanEntryCollection repository = new DefaultBeanEntryCollection(this.type);
        for (int i = 0, size = this.list.size(); i < size; i++) {
            EasyBeanEntry entry = this.list.get(i);
            if (filter.accept(entry)) {
                repository.add(entry);
            }
        }
        return repository;
    }

    public EasyBeanEntry head() {
        int size = this.list.size();
        if (size == 0) {
            return null;
        }

        EasyBeanEntry first = this.list.get(0); // 第一个元素
        if (size == 1) {
            return first;
        }

        // 因为集合元素是按优先级从大到小排序的，所以直接判断第一个元素与第二个元素的排序值是否相等，即可判断出来是否有重复组件
        if (first.getOrder() == this.list.get(1).getOrder()) {
            throw new BeanRepeatDefineException(this.type, first.getName(), this.list);
        } else {
            return first;
        }
    }

    public void sort() {
        this.sort(COMPARATOR);
    }

    public void sort(Comparator<EasyBeanEntry> comparator) {
        if (!this.list.isEmpty()) {
            Collections.sort(this.list, comparator);
        }
    }

    public List<EasyBeanEntry> values() {
        return list;
    }

    public String toString() {
        return this.toString(this.list);
    }

    public String toString(List<EasyBeanEntry> list) {
        CharTable table = new CharTable();
        table.addTitle("Type");
        table.addTitle("Name");
        table.addTitle("Priority");
        table.addTitle("singleton");
        table.addTitle("Lazy");
        table.addTitle("Description");
        table.addTitle("bean");

        for (EasyBeanEntry entry : list) {
            table.addCell(entry.getType().getName());
            table.addCell(entry.getName());
            table.addCell(entry.getOrder());
            table.addCell(entry.singleton());
            table.addCell(entry.lazy());
            table.addCell(entry.getDescription());

            if (entry instanceof EasyBeanInstance) {
                table.addCell(((EasyBeanInstance) entry).getBean());
            }
        }

        return table.toString(CharTable.Style.DB2);
    }
}
