package cn.org.expect.ioc.impl;

import java.util.List;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.concurrent.ExecutorServiceFactory;
import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.ioc.EasyBeanBuilder;
import cn.org.expect.ioc.EasyBeanInfo;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.util.StringUtils;

/**
 * 线程池构建工厂，单例模式
 */
@EasyBean
public class ThreadSourceBuilder implements EasyBeanBuilder<ThreadSource> {

    /** 线程池单例模式 */
    private ThreadSource threadSource;

    @Override
    public synchronized ThreadSource getBean(EasyContext context, Object... args) throws Exception {
        if (this.threadSource == null) {
            this.threadSource = this.create(context, args);

            // 自动添加外部线程池工厂
            List<EasyBeanInfo> list = context.getBeanInfoList(ExecutorServiceFactory.class);
            if (list.size() == 1) {
                EasyBeanInfo beanInfo = list.get(0);
                ExecutorServiceFactory factory = context.createBean(beanInfo.getType(), args);
                this.threadSource.setExecutorsFactory(factory);
            } else if (list.size() >= 2) {
                throw new UnsupportedOperationException(list.size() + ":" + StringUtils.toString(list));
            }
        }

        return this.threadSource;
    }

    public ThreadSource create(EasyContext context, Object... args) {
        List<EasyBeanInfo> list = context.getBeanInfoList(ThreadSource.class);
        if (list.size() == 1) {
            EasyBeanInfo beanInfo = list.get(0);
            return context.createBean(beanInfo.getType(), args);
        }

        throw new UnsupportedOperationException(StringUtils.toString(args) + ":" + list.size() + ":" + StringUtils.toString(list));
    }
}
