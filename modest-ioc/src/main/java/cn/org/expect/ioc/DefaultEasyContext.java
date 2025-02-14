package cn.org.expect.ioc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import cn.org.expect.ProjectPom;
import cn.org.expect.ioc.impl.DefaultBeanEntry;
import cn.org.expect.ioc.internal.BeanArgument;
import cn.org.expect.ioc.internal.BeanBuilder;
import cn.org.expect.ioc.internal.BeanFactoryRepository;
import cn.org.expect.ioc.internal.BeanRepository;
import cn.org.expect.ioc.internal.BeanSubject;
import cn.org.expect.ioc.internal.ContainerRepository;
import cn.org.expect.ioc.internal.PropertiesRepository;
import cn.org.expect.ioc.internal.ScanPatternList;
import cn.org.expect.ioc.spi.BeanConfigScanner;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.LogSettings;
import cn.org.expect.message.ResourceMessageBundleRepository;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.SPI;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.UniqueSequenceGenerator;

/**
 * 容器上下文信息
 *
 * @author jeremy8551@gmail.com
 */
public class DefaultEasyContext implements EasyContext {

    /** 序号生成器 */
    protected final static UniqueSequenceGenerator UNIQUE = new UniqueSequenceGenerator(ProjectPom.getArtifactID() + "-{}", 1);

    /** 根容器锁 */
    protected final static Object lock = new Object();

    /** 根容器 */
    protected static volatile EasyContext ROOT;

    /**
     * 返回实例对象
     *
     * @return 容器上下文信息
     */
    public static EasyContext getRoot() {
        return ROOT;
    }

    /** 上级容器 */
    private EasyContext parent;

    /** 容器编号（唯一） */
    private String name;

    /** 类加载器 */
    private ClassLoader classLoader;

    /** 启动参数 */
    private String[] args;

    /** 用来记录类扫描规则 */
    private ScanPatternList scanRule;

    /** 第三方容器的集合 */
    private ContainerRepository containerRepository;

    /** 组件信息表 */
    private BeanRepository beanRepository;

    /** 组件工厂管理器 */
    private BeanFactoryRepository beanFactoryRepository;

    /** 容器工厂 */
    private BeanBuilder beanBuilder;

    /** 事件管理器 */
    private BeanSubject beanSubject;

    /** 类扫描器 */
    private EasyClassScanner scanner;

    /** 属性 */
    private PropertiesRepository properties;

    /**
     * 上下文信息
     *
     * @param args 参数数组 <br>
     *             设置日志级别，规则详见: {@linkplain LogSettings#load(String[])} <br>
     *             设置扫描包名，如: <br>
     *             org.test 表示扫描包名中的类 <br>
     *             !org.test 表示排除掉包名中的类 <br>
     */
    public DefaultEasyContext(String... args) {
        this(null, args);
    }

    /**
     * 容器上下文信息
     *
     * @param classLoader 类加载器
     * @param args        参数数组 <br>
     *                    设置日志级别，规则详见: {@linkplain LogSettings#load(String[])} <br>
     *                    设置扫描包名，如: <br>
     *                    org.test 表示扫描包名中的类 <br>
     *                    !org.test 表示排除掉包名中的类 <br>
     */
    public DefaultEasyContext(ClassLoader classLoader, String... args) {
        this.setClassLoader(classLoader);
        this.setArgument(args);

        // 加载资源文件
        ResourceMessageBundleRepository repository = ResourcesUtils.getRepository();
        repository.load(this.getClassLoader());

        // 加载日志模块参数
        String[] params = LogFactory.load(args); // 解析日志参数，并从参数数组中删除日志相关的参数

        // 扫描并加载组件
        ScanPatternList list = new ScanPatternList();
        list.addProperty();
        list.addArgument(params);
        list.addGroupID();
        String[] arguments = list.toArray();

        this.init();
        Ensure.isTrue(this.addBean(this)); // 注册容器上下文信息
        Ensure.isTrue(this.addBean(repository)); // 注册国际化资源信息
        this.scanPackages(arguments);
        this.setParent();
    }

    /**
     * 初始化
     */
    protected void init() {
        this.name = UNIQUE.nextString();
        this.properties = new PropertiesRepository();
        this.scanRule = new ScanPatternList();
        this.containerRepository = new ContainerRepository();
        this.beanFactoryRepository = new BeanFactoryRepository();
        this.scanner = new EasyClassScanner(this.getClassLoader()); // 类扫描器
        this.beanSubject = new BeanSubject(this); // 第一步
        this.beanBuilder = new BeanBuilder(this); // 第二步
        this.beanRepository = new BeanRepository(this); // 第三步
    }

    protected void setParent() {
        if (DefaultEasyContext.ROOT == null) {
            synchronized (DefaultEasyContext.lock) {
                if (DefaultEasyContext.ROOT == null) {
                    DefaultEasyContext.ROOT = this;
                } else {
                    this.setParent(DefaultEasyContext.ROOT);
                }
            }
        }
    }

    public EasyContext getParent() {
        return parent;
    }

    public void setParent(EasyContext parent) {
        this.parent = parent;
    }

    public void setClassLoader(ClassLoader classLoader) {
        ClassUtils.setClassLoader(classLoader);
        this.classLoader = ClassUtils.getClassLoader();
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public String[] getArgument() {
        return ArrayUtils.copyOf(this.args, this.args.length);
    }

    public void setArgument(String... args) {
        this.args = ArrayUtils.copyOf(args, args.length);
    }

    public synchronized int scanPackages(String... args) {
        // 扫描 classpath 中的 META-INF/modest/bean.config 文件
        BeanConfigScanner scanner = new BeanConfigScanner();
        List<EasyBeanEntry> beanEntryList = scanner.load(this.getClassLoader());
        for (EasyBeanEntry entry : beanEntryList) {
            this.addBean(entry);
        }

        // 扫描注解
        List<String> list = ArrayUtils.asList(args);
        this.scanRule.addAll(list);
        try {
            return this.scanner.load(this, list); // 执行类扫描
        } finally {
            this.refresh();
        }
    }

    public String[] getScanPackages() {
        return this.scanRule.toArray();
    }

    public synchronized EasyContainer removeContainer(String name) {
        return this.containerRepository.remove(name);
    }

    public synchronized EasyContainer addContainer(EasyContainer ioc) {
        return this.containerRepository.add(ioc);
    }

    public <E> Class<E> forName(String className) {
        return ClassUtils.forName(className, true, this.getClassLoader());
    }

    public <E> E newInstance(Class<?> type, Object... args) {
        return this.beanBuilder.newInstance(type, args);
    }

    public void autowire(Object object) {
        Ensure.notNull(object);
        this.beanBuilder.autoAware(object);
        this.beanBuilder.autowire(object);
    }

    public synchronized void removeBean() {
        this.beanRepository.clear();
        this.beanFactoryRepository.clear();
        this.beanSubject.clear();
    }

    public synchronized void refresh() {
        this.beanRepository.sort();

        // 处理非延迟加载的组件
        List<EasyBeanEntry> list = this.beanRepository.getQuickEntryList();
        for (EasyBeanEntry entry : list) {
            if (entry.getBean() == null) {
                entry.setBean(this.beanBuilder.newInstance(entry.getType()));
            }
        }
    }

    public EasyBeanEntry getBeanEntry(Class<?> type) {
        return this.beanRepository.get(type).head();
    }

    public EasyBeanEntry getBeanEntry(Class<?> type, String name) {
        return this.beanRepository.get(type).get(name).head();
    }

    public <E> E getBean(Class<E> type, Object... args) {
        E bean = this.findOrCreate(type, args);
        if (bean != null) {
            return bean;
        } else {
            return this.containerRepository.getBean(type, args);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> E findOrCreate(Class<E> type, Object... args) {
        // 优先使用接口工厂生成实例对象
        EasyBeanFactory<?> factory = this.beanFactoryRepository.get(type);
        if (factory != null) {
            try {
                return (E) factory.build(this, args);
            } catch (Throwable e) {
                throw new IocException("ioc.stdout.message025", type.getName(), e);
            }
        }

        // 按组件类与组件名查询
        BeanArgument argument = new BeanArgument(args);
        EasyBeanEntry entry = this.beanRepository.get(type).get(argument.getName()).head();

        // 尝试创建类的实例对象
        if (entry == null) {
            try {
                return this.beanBuilder.newInstance(type, args);
            } catch (Throwable e) {
                return null;
            }
        }

        // 防止多线程同时访问同一个组件信息
        entry.lock();
        try {
            // 单例模式
            if (entry.singleton()) {
                if (entry.getBean() == null) {
                    entry.setBean(this.beanBuilder.newInstance(entry.getType(), argument.getArgs()));
                }
                return entry.getBean();
            }

            // 原型模式
            if (entry.getBean() != null) {
                E bean = entry.getBean();
                entry.setBean(null); // 原型模式需要删除存储的实例对象，防止被重复使用
                return bean;
            }
        } finally {
            entry.unlock();
        }

        return this.beanBuilder.newInstance(entry.getType(), argument.getArgs());
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

    public boolean addBean(Object bean) {
        Ensure.notNull(bean);
        DefaultBeanEntry entry = new DefaultBeanEntry(bean.getClass());
        entry.setSingleton(true); // 默认单例模式
        entry.setLazy(false); // 编程方式添加单例对象，延迟加载方式设置为false
        entry.setBean(bean);
        return this.addBean(entry);
    }

    public boolean addBean(Class<?> beanClass, String name, boolean singleton, boolean lazy, int priority, String description) {
        DefaultBeanEntry entry = new DefaultBeanEntry(beanClass);
        entry.setName(name);
        entry.setSingleton(singleton);
        entry.setLazy(lazy);
        entry.setOrder(priority);
        entry.setDescription(description);
        return this.addBean(entry);
    }

    public boolean addBean(Class<?> beanClass) {
        if (this.scanner.load(beanClass)) {
            return true;
        } else {
            return this.addBean(new DefaultBeanEntry(beanClass));
        }
    }

    public synchronized boolean addBean(EasyBeanEntry entry) {
        if (entry == null) {
            return false;
        }

        boolean add = false;
        Class<?> type = entry.getType();

        // 组件工厂接口
        EasyBeanFactory<?> factory = this.beanFactoryRepository.create(type, this);
        if (factory != null) {
            add = true;
        }

        // 组件监听器
        if (EasyBeanListener.class.isAssignableFrom(type)) {
            EasyBeanListener listener = factory != null ? (EasyBeanListener) factory : (EasyBeanListener) this.newInstance(type);
            this.beanSubject.addListener(listener);
        }

        // 添加类和父类 与实现类的映射关系
        Class<?> supercls = type;
        while (supercls != null && !supercls.equals(Object.class)) {
            EasyBeanEntryCollection collection = this.beanRepository.get(supercls);
            if (!collection.contains(entry) && collection.add(entry)) {
                this.beanSubject.notifyAdd(entry);
                add = true;
            }
            supercls = supercls.getSuperclass();
        }

        // 添加接口与实现类的映射关系
        List<Class<?>> interfaces = ClassUtils.getAllInterface(type);
        for (Class<?> interfaceClass : interfaces) {
            EasyBeanEntryCollection collection = this.beanRepository.get(interfaceClass);
            if (!collection.contains(entry) && collection.add(entry)) {
                this.beanSubject.notifyAdd(entry);
                add = true;
            }
        }

        return add;
    }

    public synchronized List<EasyBeanEntry> removeBean(Class<?> type) {
        return new ArrayList<EasyBeanEntry>(this.beanRepository.remove(type).values());
    }

    public boolean containsBean(Class<?> beanClass, Class<?> type) {
        return this.beanRepository.get(beanClass).contains(type);
    }

    public List<EasyBeanEntry> removeBean(Class<?> type, Class<?> cls) {
        List<EasyBeanEntry> list = this.beanRepository.get(type).remove(cls);
        for (EasyBeanEntry entry : list) {
            this.beanSubject.notifyRemove(entry);
        }
        return list;
    }

    public EasyBeanEntryCollection getBeanEntryCollection(Class<?> type) {
        return this.beanRepository.get(type);
    }

    public EasyBeanEntryCollection getBeanEntryCollection(Class<?> type, String name) {
        return this.beanRepository.get(type).get(name);
    }

    public List<Class<?>> getBeanClassList() {
        return new ArrayList<Class<?>>(this.beanRepository.getBeanClass());
    }

    public List<Class<?>> getBeanFactoryClass() {
        return new ArrayList<Class<?>>(this.beanFactoryRepository.getBeanClass());
    }

    public EasyBeanFactory<?> getBeanFactory(Class<?> type) {
        return this.beanFactoryRepository.get(type);
    }

    public synchronized EasyBeanFactory<?> removeBeanFactory(Class<?> type) {
        return this.beanFactoryRepository.remove(type);
    }

    public synchronized boolean addBeanFactory(EasyBeanFactory<?> factory) {
        return this.beanFactoryRepository.add(factory, this);
    }

    public String getName() {
        return this.name;
    }

    public <E> List<E> loadBean(Class<E> service) {
        return new SPI<E>(this.getClassLoader(), service) {

            public List<E> load() {
                List<E> list = super.load();
                for (E e : list) {
                    autowire(e);
                }
                return list;
            }

            protected void process(Throwable e) {
                LogFactory.getLog(EasyContext.class).error(e.getLocalizedMessage(), e);
            }
        }.load();
    }

    public boolean add(Properties properties, Comparator<Properties> comparator) {
        return this.properties.add(properties, comparator);
    }

    public String getProperty(String name) {
        return this.properties.getProperty(name);
    }

    public List<Properties> getProperties() {
        return this.properties.getProperties();
    }
}
