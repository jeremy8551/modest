package cn.org.expect.ioc.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyBeanEntryCollection;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.impl.DefaultBeanEntryCollection;
import cn.org.expect.util.Ensure;

/**
 * 组件仓库
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/25
 */
public class BeanRepository {

    /** 类或接口 与 实现类 的映射关系 */
    private final LinkedHashMap<Class<?>, EasyBeanEntryCollection> map;

    /** 类或接口 使用哪个 {@linkplain EasyBeanEntryCollection} 接口的实现类存储组件信息 */
    private final Map<Class<?>, EasyBeanEntryCollection> bean2Collection;

    public BeanRepository(EasyContext context) {
        this.map = new LinkedHashMap<Class<?>, EasyBeanEntryCollection>(50);
        this.bean2Collection = new HashMap<Class<?>, EasyBeanEntryCollection>();
        for (EasyBeanEntryCollection collection : context.loadBean(EasyBeanEntryCollection.class)) {
            this.bean2Collection.put(collection.getBeanClass(), collection);
        }
    }

    /**
     * 清空管理器
     */
    public void clear() {
        this.map.clear();
    }

    /**
     * 移除某个组件的所有实现类
     *
     * @param type 组件的类信息
     * @return 组件的所有实现类
     */
    public EasyBeanEntryCollection remove(Class<?> type) {
        return this.map.remove(type);
    }

    /**
     * 查询类或接口的实现类 <br>
     * 查询配置注解的类信息
     *
     * @param type 类或接口、注解
     * @return 组件仓库
     */
    public EasyBeanEntryCollection get(Class<?> type) {
        Ensure.notNull(type);

        // 查询配置注解的类
        if (Annotation.class.isAssignableFrom(type)) {
            @SuppressWarnings("unchecked")
            Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) type;
            return this.getAnnotation(annotationClass);
        }

        // 查询类或接口的实现类
        EasyBeanEntryCollection collection = this.map.get(type);
        if (collection == null) {
            collection = this.bean2Collection.get(type);
            if (collection == null) {
                collection = new DefaultBeanEntryCollection(type);
            }
            this.map.put(type, collection);
        }
        return collection;
    }

    /**
     * 查询配置注解的类
     *
     * @param type 注解类信息
     * @return 查询结果
     */
    public EasyBeanEntryCollection getAnnotation(final Class<? extends Annotation> type) {
        final DefaultBeanEntryCollection repository = new DefaultBeanEntryCollection(type);
        Collection<EasyBeanEntryCollection> values = this.map.values();
        for (EasyBeanEntryCollection collection : values) {
            collection.filter(new EasyBeanEntryCollection.Filter() {
                public boolean accept(EasyBeanEntry entry) {
                    if (entry.getType().isAnnotationPresent(type)) {
                        repository.add(entry);
                    }
                    return false;
                }
            });
        }
        return repository;
    }

    /**
     * 返回组件类的集合
     *
     * @return 类信息集合
     */
    public Set<Class<?>> getBeanClass() {
        return this.map.keySet();
    }

    /**
     * 对同名的组件, 按优先级排序
     */
    public void sort() {
        Collection<EasyBeanEntryCollection> values = this.map.values();
        for (EasyBeanEntryCollection collection : values) {
            collection.sort();
        }
    }

    /**
     * 查找所有<b>非延迟</b>加载的组件信息
     *
     * @return 组件信息集合
     */
    public List<EasyBeanEntry> getQuickEntryList() {
        List<EasyBeanEntry> list = new ArrayList<EasyBeanEntry>();
        for (EasyBeanEntryCollection collection : this.map.values()) {
            for (EasyBeanEntry entry : collection.values()) {
                if (!entry.lazy()) {
                    list.add(entry);
                }
            }
        }
        return list;
    }
}
