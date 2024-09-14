package cn.org.expect.ioc;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.ioc.impl.EasyetlContainerContextImpl;
import cn.org.expect.util.StringUtils;

/**
 * IOC容器管理器
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/26
 */
public class EasyetlContainerContextManager {

    /** 容器上下文的集合 */
    private List<EasyetlContainerContext> list;

    /**
     * 容器上下文信息管理器
     *
     * @param context 容器上下文信息
     */
    public EasyetlContainerContextManager(EasyetlContext context) {
        this.list = new ArrayList<EasyetlContainerContext>();
        this.add(new EasyetlContainerContextImpl(context));
    }

    /**
     * 查找容器的位置信息
     *
     * @param name 容器名
     * @return 位置信息
     */
    protected int indexOf(String name) {
        for (int i = 0; i < this.list.size(); i++) {
            EasyetlContainerContext ioc = this.list.get(i);
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
    public EasyetlContainerContext add(EasyetlContainerContext ioc) {
        if (ioc == null || StringUtils.isBlank(ioc.getName())) {
            throw new IllegalArgumentException();
        }

        int index = this.indexOf(ioc.getName());
        if (index == -1) {
            this.list.add(ioc);
            return null;
        } else {
            EasyetlContainerContext old = this.list.get(index);
            this.list.set(index, ioc);
            return old;
        }
    }

    /**
     * 移除容器
     *
     * @param name 容器名
     * @return 容器
     */
    public EasyetlContainerContext remove(String name) {
        int index = this.indexOf(name);
        if (index == -1) {
            return null;
        } else {
            return this.list.remove(index);
        }
    }

    /**
     * 在容器中查找组件
     *
     * @param type 组件的类信息
     * @param args 组件的参数
     * @param <E>  组件类
     * @return 组件实例
     */
    public <E> E getBean(Class<E> type, Object[] args) {
        E bean;
        for (int i = 0; i < this.list.size(); i++) {
            EasyetlContainerContext ioc = this.list.get(i);
            if ((bean = ioc.getBean(type, args)) != null) {
                return bean;
            }
        }
        return null;
    }

}
