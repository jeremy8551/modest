package cn.org.expect.script.method.inernal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.script.annotation.EasyVariableExtension;
import cn.org.expect.script.annotation.EasyVariableMethod;
import cn.org.expect.script.method.VariableMethodEntry;
import cn.org.expect.script.method.VariableMethodRepository;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

/**
 * 扫描并加载脚本引擎变量方法
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-02-09
 */
public class MethodLoader {
    private final static Log log = LogFactory.getLog(MethodLoader.class);

    /** 容器 */
    private final EasyContext context;

    /** 变量方法仓库 */
    private final VariableMethodRepository repository;

    /**
     * 初始化
     *
     * @param context    脚本引擎上下文信息
     * @param repository 变量方法仓库
     */
    public MethodLoader(EasyContext context, VariableMethodRepository repository) {
        this.context = Ensure.notNull(context);
        this.repository = Ensure.notNull(repository);

        // 加载静态方法定义的变量方法
        for (EasyBeanEntry entry : context.getBeanEntryCollection(EasyVariableExtension.class).values()) {
            Class<Object> type = entry.getType();
            try {
                this.loadStaticMethod(type);
            } catch (Throwable e) {
                if (log.isWarnEnabled()) {
                    log.warn(e.getLocalizedMessage(), e);
                }
            }
        }

        // 加载接口定义的变量方法
        for (EasyBeanEntry entry : context.getBeanEntryCollection(UniversalScriptVariableMethod.class).values()) {
            Class<Object> type = entry.getType();
            if (!Modifier.isAbstract(type.getModifiers())) {
                try {
                    this.loadClassMethod(type);
                } catch (Throwable e) {
                    if (log.isWarnEnabled()) {
                        log.warn(e.getLocalizedMessage(), e);
                    }
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("\n\n\n{}", this.repository.toStandardString());
        }
    }

    /**
     * 加载类中定义的静态方法
     *
     * @param type 类信息
     */
    public void loadStaticMethod(Class<Object> type) {
        if (!UniversalScriptVariableMethod.class.isAssignableFrom(type)) {
            Method[] methods = type.getDeclaredMethods();
            for (Method method : methods) {
                if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers()) && !Modifier.isAbstract(method.getModifiers()) && method.getParameterCount() >= 1) {
                    VariableMethodEntry methodEntry = new VariableMethodEntry(method, type);
                    if (this.repository.contains(methodEntry)) {
                        if (log.isWarnEnabled()) {
                            log.warn("script.stdout.message048", methodEntry.toStandardString(), methodEntry.getMethodInfo());
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("script.stdout.message046", methodEntry.toStandardString(), methodEntry.getMethodInfo());
                        }
                        this.repository.add(methodEntry);
                    }
                }
            }
        }
    }

    /**
     * 加载 {@linkplain UniversalScriptVariableMethod} 接口的实现类
     *
     * @param type 变量方法的类信息
     */
    public void loadClassMethod(Class<?> type) {
        EasyVariableMethod annotation = type.getAnnotation(EasyVariableMethod.class);
        if (StringUtils.isBlank(annotation.name())) {
            log.warn("script.stdout.message047", type.getName(), "name is Blank!");
            return;
        }

        if (annotation.variable() == null) {
            log.warn("script.stdout.message047", type.getName(), "variable Class is null!");
            return;
        }

        // 注册方法
        UniversalScriptVariableMethod method = this.context.newInstance(type);
        VariableMethodEntry entry = new VariableMethodEntry(method);
        if (this.repository.contains(entry)) {
            if (log.isWarnEnabled()) {
                log.warn("script.stdout.message048", entry.toStandardString(), entry.getMethodInfo());
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("script.stdout.message046", entry.toStandardString(), entry.getMethodInfo());
            }

            this.repository.add(entry);
        }
    }
}
