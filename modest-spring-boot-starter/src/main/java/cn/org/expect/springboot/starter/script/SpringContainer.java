package cn.org.expect.springboot.starter.script;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;

import cn.org.expect.concurrent.ExecutorServiceFactory;
import cn.org.expect.ioc.EasyContainer;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.internal.BeanArgument;
import cn.org.expect.springboot.starter.concurrent.ScriptServiceFactory;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Spring容器上下文的适配器
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/26
 */
public class SpringContainer implements EasyContainer {
    private final static Logger log = LoggerFactory.getLogger(SpringContainer.class);

    /** Spring容器上下文信息 */
    private final ApplicationContext springContext;

    /** 场景启动器名 */
    private final String starterName;

    /**
     * 初始化
     *
     * @param context     Spring容器上下文信息
     * @param starterName 场景启动器名
     */
    public SpringContainer(ApplicationContext context, String starterName) {
        this.springContext = Ensure.notNull(context);
        this.starterName = Ensure.notBlank(starterName);
    }

    public String getName() {
        return this.springContext.getId();
    }

    @SuppressWarnings("unchecked")
    public <E> E getBean(Class<E> cls, Object... args) {
        if (args.length == 0) {
            return this.springContext.getBean(cls);
        }

        if (args[0] instanceof String) {
            BeanArgument argument = new BeanArgument(args);
            E bean = null;

            // 组件名和匹配类查询
            try {
                bean = this.springContext.getBean(argument.getName(), cls);
            } catch (BeansException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.getLocalizedMessage(), e);
                }
            }

            if (bean != null) {
                return bean;
            }

            // args 作为构造方法的参数
            try {
                bean = (E) this.springContext.getBean(argument.getName(), argument.getArgs());
            } catch (BeansException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.getLocalizedMessage(), e);
                }
            }

            if (bean != null) {
                return bean;
            }

            // 按组件名查询
            try {
                bean = (E) this.springContext.getBean(argument.getName());
            } catch (BeansException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.getLocalizedMessage(), e);
                }
            }

            if (bean != null) {
                return bean;
            }
        }

        // args 作为构造方法的参数
        return this.springContext.getBean(cls, args);
    }

    public <E> E getBeanQuietly(Class<E> type, Object... args) {
        try {
            return this.getBean(type, args);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public <E> E getBeanQuietly(Class<E> type, String name, String defaultName) {
        E bean;
        if (StringUtils.isBlank(name)) {
            bean = this.getBeanQuietly(type);
        } else {
            bean = this.getBeanQuietly(type, name);
        }

        // 尝试使用默认的名字
        if (bean == null && StringUtils.isNotBlank(defaultName)) {
            bean = this.getBeanQuietly(type, defaultName);
        }
        return bean;
    }

    /**
     * 在Spring容器中查找可用的线程池
     *
     * @return 线程池工厂
     */
    public ExecutorServiceFactory getExecutorFactory() {
        // 优先使用Springboot默认线程池
        try {
            ThreadPoolTaskExecutor pool = (ThreadPoolTaskExecutor) this.springContext.getBean("taskExecutor");
            if (pool != null) {
                log.info("{} use Springboot taskExecutor {}", this.starterName, pool);
                return new ScriptServiceFactory(pool);
            }
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getLocalizedMessage(), e);
            }
        }

        // 使用Springboot自定义线程池
        try {
            ThreadPoolTaskExecutor service = this.springContext.getBean(ThreadPoolTaskExecutor.class);
            if (service != null) {
                log.info("{} use Spring ThreadPoolTaskExecutor {}", this.starterName, service);
                return new ScriptServiceFactory(service);
            }
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getLocalizedMessage(), e);
            }
        }

        // 使用JDK自定义线程池
        try {
            ExecutorService service = this.springContext.getBean(ExecutorService.class);
            if (service != null) {
                log.info("{} use Spring ExecutorService {}", this.starterName, service);
                return new ScriptServiceFactory(service);
            }
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getLocalizedMessage(), e);
            }
        }

        // 没有发现可用的线程池
        log.info("{} did not find any available thread pools in SpringContext!", this.starterName);
        return null;
    }

    /**
     * 在Spring上级容器中查找bean
     *
     * @return 容器
     */
    public EasyContext getParent() {
        ApplicationContext parent = this.springContext;
        while ((parent = parent.getParent()) != null) {
            String[] names = parent.getBeanNamesForType(EasyContext.class);
            if (names == null || names.length == 0) {
                continue;
            }

            String[] array = this.sort(names);
            String contextName = array[0]; // 默认只取第一个容器名

            log.info("{} discover {}[id={}] already contains {} ..", this.starterName, parent.getClass().getName(), parent.getId(), contextName);
            try {
                EasyContext context = (EasyContext) parent.getBean(contextName);
                if (context != null) {
                    return context;
                }
            } catch (Throwable e) {
                if (log.isWarnEnabled()) {
                    log.warn(e.getLocalizedMessage(), e);
                }
            }
        }

        return null;
    }

    private String[] sort(String[] names) {
        String[] array = ArrayUtils.copyOf(names, names.length); // 复制
        Comparator<String> comparator = new Comparator<String>() {
            public int compare(String o1, String o2) {
                String[] a1 = o1.split("\\-");
                String[] a2 = o2.split("\\-");

                boolean b1 = a1.length == 2 && StringUtils.isNumber(a1[1]);
                boolean b2 = a2.length == 2 && StringUtils.isNumber(a2[1]);

                if (b1 && b2) {
                    return Integer.parseInt(a1[1]) - Integer.parseInt(a2[1]);
                } else if (b1) {
                    return 0;
                } else {
                    return 1;
                }
            }
        };

        Arrays.sort(array, comparator); // 排序
        return array;
    }
}
