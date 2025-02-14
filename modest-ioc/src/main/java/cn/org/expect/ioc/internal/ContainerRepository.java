package cn.org.expect.ioc.internal;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.ioc.EasyContainer;
import cn.org.expect.util.StringUtils;

/**
 * 第三方容器集合
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/26
 */
public class ContainerRepository {

    /** 容器集合 */
    private final List<EasyContainer> list;

    /**
     * 容器上下文信息管理器
     */
    public ContainerRepository() {
        this.list = new ArrayList<EasyContainer>();
    }

    /**
     * 查找容器的位置信息
     *
     * @param name 容器名
     * @return 位置信息
     */
    protected int indexOf(String name) {
        for (int i = 0; i < this.list.size(); i++) {
            EasyContainer ioc = this.list.get(i);
            if (ioc.getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 添加容器
     *
     * @param ioc 容器
     * @return 被替换的容器
     */
    public EasyContainer add(EasyContainer ioc) {
        if (ioc == null || StringUtils.isBlank(ioc.getName())) {
            throw new IllegalArgumentException();
        }

        int index = this.indexOf(ioc.getName());
        if (index == -1) {
            this.list.add(ioc);
            return null;
        } else {
            EasyContainer value = this.list.get(index);
            this.list.set(index, ioc);
            return value;
        }
    }

    /**
     * 移除容器
     *
     * @param name 容器名
     * @return 容器
     */
    public EasyContainer remove(String name) {
        int index = this.indexOf(name);
        if (index == -1) {
            return null;
        } else {
            return this.list.remove(index);
        }
    }

    /**
     * 查询组件
     *
     * @param <E>  组件类
     * @param type 组件的类信息
     * @param args 组件的参数
     * @return 实例对象
     */
    public <E> E getBean(Class<E> type, Object[] args) {
        E bean;
        for (int i = 0; i < this.list.size(); i++) {
            if ((bean = this.list.get(i).getBean(type, args)) != null) {
                return bean;
            }
        }
        return null;
    }
}
