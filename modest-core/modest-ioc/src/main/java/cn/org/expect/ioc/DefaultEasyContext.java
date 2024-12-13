package cn.org.expect.ioc;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.ProjectPom;
import cn.org.expect.ioc.impl.EasyBeanDefineImpl;
import cn.org.expect.ioc.impl.EasyBeanFactoryImpl;
import cn.org.expect.ioc.scan.BeanClassScanner;
import cn.org.expect.ioc.scan.EasyScanPatternList;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.LogSettings;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourceMessageBundle;
import cn.org.expect.util.ResourceMessageBundleMap;
import cn.org.expect.util.StrAsIntComparator;
import cn.org.expect.util.StrAsNumberComparator;
import cn.org.expect.util.StringComparator;
import cn.org.expect.util.UniqueSequenceGenerator;

/**
 * 容器上下文信息
 *
 * @author jeremy8551@qq.com
 */
public class DefaultEasyContext implements EasyContext {

    /** 序号生成器 */
    protected final static UniqueSequenceGenerator UNIQUE = new UniqueSequenceGenerator(ProjectPom.getArtifactID() + "-{}", 1);

    /** 容器的单例模式，默认使用第一个创建的容器作为单例 */
    private static volatile EasyContext INSTANCE;

    /**
     * 返回实例对象
     *
     * @return 容器上下文信息
     */
    public static EasyContext getInstance() {
        return INSTANCE;
    }

    /**
     * 创建一个实例对象
     *
     * @param classLoader 类加载器
     * @param args        参数数组
     */
    public synchronized static DefaultEasyContext newInstance(ClassLoader classLoader, String... args) {
        DefaultEasyContext context = new DefaultEasyContext(classLoader, args);
        INSTANCE = context;
        return context;
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
    private EasyScanPatternList scanRule;

    /** Ioc容器管理器 */
    private EasyContainerContextManager iocManager;

    /** 组件信息表 */
    private EasyBeanTable table;

    /** 组件工厂管理器 */
    private EasyBeanBuilderManager builders;

    /** 容器工厂 */
    private EasyBeanFactoryImpl factory;

    /** 事件管理器 */
    private EasyBeanEventManager eventManager;

    /**
     * 上下文信息
     *
     * @param args 参数数组，格式如下:
     *             org.test 表示扫描这个包名下的类信息
     *             !org.test 表示扫描包时，排除掉这个包名下的类
     *             sout:debug 表示使用控制台输出debug级别的日志
     */
    public DefaultEasyContext(String... args) {
        this(null, args);
    }

    /**
     * 容器上下文信息
     *
     * @param classLoader 类加载器
     * @param args        参数数组 <br>
     *                    设置日志级别，规则详见: {@linkplain LogSettings#load(LogContext, String...)} <br>
     *                    设置扫描包名，如：org.apache,org.spring
     */
    public DefaultEasyContext(ClassLoader classLoader, String... args) {
        this.setClassLoader(classLoader);
        this.setArgument(args);

        // 加载资源文件
        ResourceMessageBundle resourceBundle = new ResourceMessageBundleMap(classLoader);

        // 加载日志模块参数
        LogContext logContext = LogFactory.getContext();
        logContext.setResourceBundle(resourceBundle);
        String[] params = LogSettings.load(logContext, args); // 解析日志参数，并从参数数组中删除日志相关的参数

        // 扫描并加载组件
        EasyScanPatternList list = new EasyScanPatternList();
        list.addProperty();
        list.addArgument(params);
        list.addGroupID();
        String[] arguments = list.toArray();

        this.init();
        Ensure.isTrue(this.addSingletonBean(this)); // 注册容器上下文信息
        Ensure.isTrue(this.addSingletonBean(resourceBundle)); // 注册国际化资源信息
        this.addBeans(); // 注册其他组件
        this.scanPackages(arguments);
    }

    /**
     * 初始化
     */
    protected void init() {
        this.scanRule = new EasyScanPatternList();
        this.iocManager = new EasyContainerContextManager(this);
        this.factory = new EasyBeanFactoryImpl(this);
        this.table = new EasyBeanTable(this);
        this.eventManager = new EasyBeanEventManager(this);
        this.builders = new EasyBeanBuilderManager(this);
        this.name = UNIQUE.nextString();
    }

    public EasyContext getParent() {
        return parent;
    }

    public void setParent(EasyContext parent) {
        this.parent = parent;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = ClassUtils.getClassLoader(classLoader);
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

    public synchronized EasyContainerContext removeIoc(String name) {
        return this.iocManager.remove(name);
    }

    public synchronized EasyContainerContext addIoc(EasyContainerContext ioc) {
        return this.iocManager.add(ioc);
    }

    public <E> E createBean(Class<?> type, Object... args) {
        return this.factory.createBean(type, args);
    }

    public synchronized void removeBeanInfo() {
        this.table.clear();
        this.builders.clear();
        this.eventManager.clear();
    }

    public String[] getScanRule() {
        return this.scanRule.toArray();
    }

    public synchronized int scanPackages(String... args) {
        this.scanRule.addAll(ArrayUtils.asList(args));
        BeanClassScanner scanner = new BeanClassScanner();
        int load = scanner.load(this, args);
        this.refresh();
        return load;
    }

    public synchronized void refresh() {
        this.table.refresh();
    }

    public EasyBeanDefine getBeanInfo(Class<?> type, String name) {
        Ensure.notNull(type);
        return this.table.get(type).indexOf(name).getBeanInfo();
    }

    public <E> E getBean(Class<E> type, Object... args) {
        return this.iocManager.getBean(type, args);
    }

    public boolean addSingletonBean(Object bean) {
        Ensure.notNull(bean);
        EasyBeanDefineImpl beanInfo = new EasyBeanDefineImpl(bean.getClass());
        beanInfo.setSingleton(true); // 默认单例模式
        beanInfo.setLazy(false); // 编程方式添加单例对象，延迟加载方式设置为false
        beanInfo.setBean(bean);
        return this.addBean(beanInfo);
    }

    public boolean addBean(Class<?> beanClass, String name, boolean singleton, boolean lazy, int priority, String description) {
        EasyBeanDefineImpl beanInfo = new EasyBeanDefineImpl(beanClass);
        beanInfo.setName(name);
        beanInfo.setSingleton(singleton);
        beanInfo.setLazy(lazy);
        beanInfo.setPriority(priority);
        beanInfo.setDescription(description);
        return this.addBean(beanInfo);
    }

    public boolean addBean(Class<?> beanClass) {
        return this.addBean(new EasyBeanDefineImpl(beanClass));
    }

    public synchronized boolean addBean(EasyBeanDefine beanInfo) {
        if (beanInfo == null) {
            return false;
        }

        boolean add = false;
        Class<?> cls = beanInfo.getType();
        if (this.builders.add(cls, this.eventManager)) {
            add = true;
        }

        // 添加类和父类 与实现类的映射关系
        Class<?> supercls = cls;
        while (supercls != null && !supercls.equals(Object.class)) {
            if (this.table.get(supercls).push(beanInfo)) {
                this.eventManager.addBeanEvent(beanInfo);
                add = true;
            }
            supercls = supercls.getSuperclass();
        }

        // 添加接口与实现类的映射关系
        List<Class<?>> interfaces = ClassUtils.getAllInterface(cls, null);
        for (Class<?> type : interfaces) {
            if (this.table.get(type).push(beanInfo)) {
                this.eventManager.addBeanEvent(beanInfo);
                add = true;
            }
        }
        return add;
    }

    public synchronized List<EasyBeanInfo> removeBeanInfoList(Class<?> type) {
        return new ArrayList<EasyBeanInfo>(this.table.remove(type));
    }

    public boolean containsBeanInfo(Class<?> type, Class<?> cls) {
        return this.table.get(type).contains(cls);
    }

    public List<EasyBeanDefine> removeBeanInfo(Class<?> type, Class<?> cls) {
        List<EasyBeanDefine> list = this.table.get(type).remove(cls);
        for (EasyBeanDefine beanInfo : list) {
            this.eventManager.removeBeanEvent(beanInfo);
        }
        return list;
    }

    public List<EasyBeanInfo> getBeanInfoList(Class<?> type) {
        return new ArrayList<EasyBeanInfo>(this.table.get(type));
    }

    public List<EasyBeanInfo> getBeanInfoList(Class<?> type, String name) {
        Ensure.notNull(type);
        return new ArrayList<EasyBeanInfo>(this.table.get(type).indexOf(name));
    }

    public List<Class<?>> getBeanInfoTypes() {
        return new ArrayList<Class<?>>(this.table.keySet());
    }

    public List<Class<?>> getBeanBuilderType() {
        return new ArrayList<Class<?>>(this.builders.keySet());
    }

    public EasyBeanBuilder<?> getBeanBuilder(Class<?> type) {
        return this.builders.get(type);
    }

    public synchronized EasyBeanBuilder<?> removeBeanBuilder(Class<?> type) {
        return this.builders.remove(type);
    }

    public synchronized boolean addBeanBuilder(Class<?> type, EasyBeanBuilder<?> builder) {
        Ensure.notNull(type);
        Ensure.notNull(builder);
        return this.builders.add(type, builder);
    }

    public String getName() {
        return this.name;
    }

    /**
     * 注册组件
     */
    protected void addBeans() {
        Ensure.isTrue(this.addBean(StrAsIntComparator.class, "int", false, true, 0, "字符串作为整数的比较规则"));
        Ensure.isTrue(this.addBean(StrAsNumberComparator.class, "number", false, true, 0, "字符串作为浮点数的比较规则"));
        Ensure.isTrue(this.addBean(StringComparator.class, "string", false, true, 0, "字符串比较规则"));

        Class<Object> type1 = ClassUtils.forName("cn.org.expect.concurrent.ThreadSourceImpl");
        if (type1 != null) {
            Ensure.isTrue(this.addBean(type1, "", true, true, 0, "线程池"));
        }

        Class<Object> type2 = ClassUtils.forName("cn.org.expect.expression.AnalysisImpl");
        if (type2 != null) {
            Ensure.isTrue(this.addBean(type2, "", false, true, 0, "脚本语句分析器"));
        }
    }
}
