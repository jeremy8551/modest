package cn.org.expect.ioc.impl;

import cn.org.expect.concurrent.ExecutorServiceFactory;
import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyBeanFactory;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;

/**
 * 线程池构建工厂，单例模式
 */
@EasyBean
public class ThreadSourceFactory implements EasyBeanFactory<ThreadSource> {

    /** 线程池单例模式 */
    private volatile ThreadSource threadSource;

    public synchronized ThreadSource build(EasyContext context, Object... args) throws Exception {
        if (this.threadSource == null) {
            synchronized (this) {
                if (this.threadSource == null) {
                    EasyBeanEntry entry = context.getBeanEntry(ThreadSource.class);
                    this.threadSource = context.newInstance(entry.getType());

                    // 自动添加外部线程池工厂
                    EasyBeanEntry service = context.getBeanEntry(ExecutorServiceFactory.class);
                    if (service != null) {
                        ExecutorServiceFactory serviceFactory = context.newInstance(service.getType());
                        if (serviceFactory != null) {
                            this.threadSource.setExecutorsFactory(serviceFactory);
                        }
                    }
                }
            }
        }

        return this.threadSource;
    }
}
