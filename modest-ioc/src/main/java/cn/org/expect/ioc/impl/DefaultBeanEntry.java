package cn.org.expect.ioc.impl;

import java.util.concurrent.locks.ReentrantLock;

import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

/**
 * 接口实现类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-02-08
 */
public class DefaultBeanEntry implements EasyBeanEntry {

    /** 锁 */
    protected ReentrantLock lock;

    /** 组件的类信息 */
    protected Class<?> type;

    /** 组件管理的模式 */
    protected boolean singleton;

    /** 组件名 */
    protected String name;

    /** 排序优先级 */
    protected int order;

    /** 是否延迟加载 */
    protected boolean lazy;

    /** 说明信息 */
    protected String description;

    /** 组件的实例对象 */
    protected Object instance;

    /**
     * 初始化
     *
     * @param type 组件类信息
     */
    public DefaultBeanEntry(Class<?> type) {
        this.lock = new ReentrantLock();
        this.type = Ensure.notNull(type);
        this.setName("");
        this.setSingleton(false);
        this.setOrder(0);
        this.setLazy(true);
        this.setDescription("");
        this.setBean(null);
    }

    @SuppressWarnings("unchecked")
    public <E> Class<E> getType() {
        return (Class<E>) this.type;
    }

    public String getName() {
        return this.name;
    }

    public boolean singleton() {
        return singleton;
    }

    public int getOrder() {
        return order;
    }

    public boolean lazy() {
        return lazy;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 设置是否使用单例模式
     *
     * @param singleton true表示使用单例模式 false表示原型模式
     */
    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    /**
     * 设置组件名
     *
     * @param name 组件名（忽略大小写）
     */
    public void setName(String name) {
        this.name = StringUtils.coalesce(StringUtils.trimBlank(name), "");
    }

    /**
     * 设置排序序号，按序号从大到小排序 <br>
     * 如果注册了多个同名的组件导致冲突，容器使用序号值最大的组件
     *
     * @param order 权重
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * 设置是否延迟加载
     *
     * @param lazy true表示是 false表示否
     */
    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    /**
     * 设置说明信息
     *
     * @param description 说明信息
     */
    public void setDescription(String description) {
        this.description = StringUtils.coalesce(description, "");
    }

    @SuppressWarnings("unchecked")
    public <E> E getBean() {
        return (E) this.instance;
    }

    public void setBean(Object instance) {
        this.instance = instance;
    }

    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }

    /**
     * 复制属性（不包括类信息）
     *
     * @param entry 元数据
     */
    public void copy(EasyBeanEntry entry) {
        if (entry == null) {
            return;
        }

        this.setName(entry.getName());
        this.setDescription(entry.getDescription());
        this.setLazy(entry.lazy());
        this.setOrder(entry.getOrder());
        this.setSingleton(entry.singleton());
        this.setBean(entry.getBean());
    }
}
