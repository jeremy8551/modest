package cn.org.expect.springboot.starter.configuration;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import cn.org.expect.concurrent.ExecutorServiceFactory;
import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.internal.ScanPatternList;
import cn.org.expect.springboot.starter.ProjectPom;
import cn.org.expect.springboot.starter.script.SpringArgument;
import cn.org.expect.springboot.starter.script.SpringContainer;
import cn.org.expect.springboot.starter.script.SpringContainerDefine;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * 脚本引擎容器的工厂类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2024/2/8 09:30
 */
public class EasyContextFactory {
    private final static Logger log = LoggerFactory.getLogger(EasyContextFactory.class);

    /**
     * 创建一个脚本引擎容器实例
     *
     * @param argument      SpringBoot应用的启动参数
     * @param springContext Spring容器上下文信息
     * @return 脚本引擎容器实例
     */
    public static EasyContext create(SpringArgument argument, ApplicationContext springContext) {
        long start = System.currentTimeMillis();
        String starterName = ProjectPom.getArtifactID(); // 场景启动器名
        log.info("{} starting ..", starterName);
        log.info("{} slf4j Logger is {}", starterName, log.getClass().getName()); // 打印日志接口的实现类

        // 打印类加载器
        ClassLoader classLoader = getClassLoader(springContext);
        log.info("{} classLoader is {}", starterName, classLoader.getClass().getName());

        // 打印启动参数
        log.info("{} args is {}", starterName, Arrays.toString(argument.getArgs()));

        // 打印类扫描规则
        String[] array = getScanRule(starterName, classLoader, argument);
        log.info("{} class scan pattern is {}", starterName, StringUtils.trim(Arrays.toString(array), '[', ']'));

        // 将Spring容器封装
        SpringContainer spring = new SpringContainer(springContext, starterName);
        EasyContext parent = spring.getParent();
        ExecutorServiceFactory pool = spring.getExecutorFactory(); // 在Spring容器中查找可用的线程池工厂

        // 初始化容器
        EasyContext context = new DefaultEasyContext(classLoader, array);
        context.setParent(parent);
        context.getBean(ThreadSource.class).setExecutorsFactory(pool); // 添加 Spring 容器中的线程池
        context.addContainer(spring); // 添加 Spring 容器
        context.addBean(new SpringContainerDefine(springContext)); // 将 Spring 容器上下文信息作为单例注册到容器中
        context.refresh();

        // 打印启动成功标志
        log.info("{} initialization in {} ms ..", starterName, (System.currentTimeMillis() - start));
        return context;
    }

    /**
     * 返回待扫描类包的通配符数组
     *
     * @param starterName 启动器名
     * @param classLoader 类加载器
     * @return 包名通配符数组
     */
    private static String[] getScanRule(String starterName, ClassLoader classLoader, SpringArgument argument) {
        SpringApplication application = argument.getApplication();
        String[] args = argument.getArgs();

        // 类扫描配置信息
        ScanPatternList list = new ScanPatternList();
        list.addProperty();
        list.addArgument(args);

        // 读取 SpringBoot 启动类上配置的类扫描规则
        if (application != null) {
            Class<?> mainApplicationClass = application.getMainApplicationClass();
            log.info("{} SpringBoot Application is {}", starterName, mainApplicationClass.getName());
            Annotation[] annotations = mainApplicationClass.getAnnotations();
            for (Object obj : annotations) {
                if (obj instanceof SpringBootApplication) {
                    SpringBootApplication anno = (SpringBootApplication) obj;
                    ArrayList<String> list1 = ArrayUtils.asList(anno.scanBasePackages());
                    List<String> list2 = ClassUtils.asNameList(anno.scanBasePackageClasses());
                    if (list1.isEmpty() && list2.isEmpty()) {
                        list1.add(application.getClass().getPackage().getName());
                    } else {
                        list.addAll(list1);
                        list.addAll(list2);
                    }
                    list.exclude(ArrayUtils.asList(anno.excludeName()));
                    list.exclude(ClassUtils.asNameList(anno.exclude()));
                } else if (obj instanceof ComponentScan) {
                    ComponentScan anno = (ComponentScan) obj;
                    ArrayList<String> list1 = ArrayUtils.asList(anno.value());
                    ArrayList<String> list2 = ArrayUtils.asList(anno.basePackages());
                    if (list1.isEmpty() && list2.isEmpty()) {
                        list1.add(application.getClass().getPackage().getName());
                    } else {
                        list.addAll(list1);
                        list.addAll(list2);
                    }
                    list.addAll(ClassUtils.asNameList(anno.basePackageClasses()));
                }
            }
        }

        // 如果没有配置扫描的包名，则自动扫描classpath下的所有类
        if (list.getScanPattern().isEmpty()) {
            String[] array = ClassUtils.getClassPath();
            for (String classpath : array) {
                if (FileUtils.isDirectory(classpath)) {
                    Set<String> pkgs = ClassUtils.findShortPackage(classLoader, classpath);
                    for (String name : pkgs) {
                        if (!name.startsWith("java.") && !name.startsWith("javax.")) {
                            list.add(name);
                        }
                    }
                }
            }
        }

        // 添加默认组件
        list.addGroupID();
        return list.toArray();
    }

    private static ClassLoader getClassLoader(ApplicationContext springContext) {
        ClassLoader cl = springContext.getClassLoader(); // 类加载器
        if (cl == null) {
            cl = ClassUtils.getClassLoader();
        }
        return cl;
    }
}
