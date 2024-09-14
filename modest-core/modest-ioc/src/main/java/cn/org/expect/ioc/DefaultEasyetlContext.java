package cn.org.expect.ioc;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.ioc.impl.EasyetlBeanDefineImpl;
import cn.org.expect.ioc.impl.EasyetlBeanFactoryImpl;
import cn.org.expect.ioc.impl.EasyetlSerialFactory;
import cn.org.expect.ioc.scan.BeanClassScanner;
import cn.org.expect.ioc.scan.EasyScanPatternList;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.cxt.LogConfigAnalysis;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Ensure;

/**
 * 容器上下文信息
 *
 * @author jeremy8551@qq.com
 */
public class DefaultEasyetlContext implements EasyetlContext {

    /** 容器的单例模式，默认使用第一个创建的容器作为单例 */
    private static volatile EasyetlContext INSTANCE;

    /**
     * 返回实例对象
     *
     * @return 容器上下文信息
     */
    public static EasyetlContext getInstance() {
        return INSTANCE;
    }

    /**
     * 创建一个实例对象
     *
     * @param classLoader 类加载器
     * @param args        参数数组
     */
    public synchronized static DefaultEasyetlContext newInstance(ClassLoader classLoader, String... args) {
        DefaultEasyetlContext context = new DefaultEasyetlContext(classLoader, args);
        INSTANCE = context;
        return context;
    }

    /** 上级容器 */
    private EasyetlContext parent;

    /** 容器编号（唯一） */
    private String name;

    /** 类加载器 */
    private ClassLoader classLoader;

    /** 启动参数 */
    private String[] args;

    /** 用来记录类扫描规则 */
    private EasyScanPatternList scanRule;

    /** Ioc容器管理器 */
    private EasyetlContainerContextManager iocManager;

    /** 组件信息表 */
    private EasyetlBeanTable table;

    /** 组件工厂管理器 */
    private EasyetlBeanBuilderManager builders;

    /** 容器工厂 */
    private EasyetlBeanFactoryImpl factory;

    /** 事件管理器 */
    private EasyetlBeanEventManager eventManager;

    /**
     * 上下文信息
     *
     * @param args 参数数组，格式如下:
     *             org.test 表示扫描这个包名下的类信息
     *             !org.test 表示扫描包时，排除掉这个包名下的类
     *             sout:debug 表示使用控制台输出debug级别的日志
     */
    public DefaultEasyetlContext(String... args) {
        this(null, args);
    }

    /**
     * 容器上下文信息
     *
     * @param classLoader 类加载器
     * @param args        参数数组 <br>
     *                    设置日志级别，规则详见: {@linkplain LogConfigAnalysis#parse(LogContext, String...)} <br>
     *                    设置扫描包名，如：org.apache,org.spring
     */
    public DefaultEasyetlContext(ClassLoader classLoader, String... args) {
        this.setClassLoader(classLoader);
        this.setArgument(args);
        String[] configs = LogConfigAnalysis.parse(LogFactory.getContext(), args);

        // 扫描并加载组件
        EasyScanPatternList list = new EasyScanPatternList();
        list.addProperty();
        list.addArgument(configs);
        list.addGroupID();
        String[] arguments = list.toArray();

        this.init();
        this.addSelf();
        this.scanPackages(arguments);
    }

    /**
     * 初始化
     */
    protected void init() {
        this.scanRule = new EasyScanPatternList();
        this.iocManager = new EasyetlContainerContextManager(this);
        this.factory = new EasyetlBeanFactoryImpl(this);
        this.table = new EasyetlBeanTable(this);
        this.eventManager = new EasyetlBeanEventManager(this);
        this.builders = new EasyetlBeanBuilderManager(this);
        this.name = EasyetlSerialFactory.createContextName();
    }

    /**
     * 注册容器上下文信息
     */
    protected void addSelf() {
        EasyetlBeanDefineImpl beanInfo = new EasyetlBeanDefineImpl(DefaultEasyetlContext.class);
        beanInfo.setSingleton(true);
        beanInfo.setLazy(false);
        beanInfo.setBean(this);
        this.addBean(beanInfo);
    }

    public EasyetlContext getParent() {
        return parent;
    }

    public void setParent(EasyetlContext parent) {
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

    public synchronized EasyetlContainerContext removeIoc(String name) {
        return this.iocManager.remove(name);
    }

    public synchronized EasyetlContainerContext addIoc(EasyetlContainerContext ioc) {
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

    public EasyetlBeanDefine getBeanInfo(Class<?> type, String name) {
        Ensure.notNull(type);
        return this.table.get(type).indexOf(name).getBeanInfo();
    }

    public <E> E getBean(Class<E> type, Object... args) {
        return this.iocManager.getBean(type, args);
    }

    public boolean addBean(Class<?> type) {
        return this.addBean(new EasyetlBeanDefineImpl(type));
    }

    public synchronized boolean addBean(EasyetlBeanDefine beanInfo) {
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

    public synchronized List<EasyetlBean> removeBeanInfoList(Class<?> type) {
        return new ArrayList<EasyetlBean>(this.table.remove(type));
    }

    public boolean containsBeanInfo(Class<?> type, Class<?> cls) {
        return this.table.get(type).contains(cls);
    }

    public List<EasyetlBeanDefine> removeBeanInfo(Class<?> type, Class<?> cls) {
        List<EasyetlBeanDefine> list = this.table.get(type).remove(cls);
        for (EasyetlBeanDefine beanInfo : list) {
            this.eventManager.removeBeanEvent(beanInfo);
        }
        return list;
    }

    public List<EasyetlBean> getBeanInfoList(Class<?> type) {
        return new ArrayList<EasyetlBean>(this.table.get(type));
    }

    public List<EasyetlBean> getBeanInfoList(Class<?> type, String name) {
        Ensure.notNull(type);
        return new ArrayList<EasyetlBean>(this.table.get(type).indexOf(name));
    }

    public List<Class<?>> getBeanInfoTypes() {
        return new ArrayList<Class<?>>(this.table.keySet());
    }

    public List<Class<?>> getBeanBuilderType() {
        return new ArrayList<Class<?>>(this.builders.keySet());
    }

    public EasyetlBeanBuilder<?> getBeanBuilder(Class<?> type) {
        return this.builders.get(type);
    }

    public synchronized EasyetlBeanBuilder<?> removeBeanBuilder(Class<?> type) {
        return this.builders.remove(type);
    }

    public synchronized boolean addBeanBuilder(Class<?> type, EasyetlBeanBuilder<?> builder) {
        Ensure.notNull(type);
        Ensure.notNull(builder);
        return this.builders.add(type, builder);
    }

    public String getName() {
        return this.name;
    }
}
