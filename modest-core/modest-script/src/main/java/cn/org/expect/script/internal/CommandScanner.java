package cn.org.expect.script.internal;

import java.lang.reflect.Method;
import java.util.List;

import cn.org.expect.annotation.ScriptCommand;
import cn.org.expect.ioc.EasyBeanInfo;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandRepository;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptContextAware;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 脚本引擎命令类扫描器 <br>
 * 用于扫描当前JVM 中所有可用的脚本引擎命令类信息
 *
 * @author jeremy8551@qq.com
 * @createtime 2021-02-09
 */
public class CommandScanner {
    private final static Log log = LogFactory.getLog(CommandScanner.class);

    /** 脚本引擎命令仓库 */
    private UniversalCommandRepository repository;

    /** 脚本引擎工厂 */
    private UniversalScriptEngineFactory factory;

    /** 脚本引擎上下文信息 */
    private UniversalScriptContext context;

    /**
     * 初始化
     *
     * @param context    脚本引擎上下文信息
     * @param repository 命令仓库
     */
    public CommandScanner(UniversalScriptContext context, UniversalCommandRepository repository) {
        this.context = Ensure.notNull(context);
        this.repository = Ensure.notNull(repository);
        this.factory = context.getFactory();

        // 显示所有已加载的脚本引擎命令
        EasyContext cxt = context.getContainer();
        List<EasyBeanInfo> beanList = cxt.getBeanInfoList(UniversalCommandCompiler.class);
        for (EasyBeanInfo beanInfo : beanList) {
            Class<? extends UniversalCommandCompiler> cls = beanInfo.getType();
            try {
                this.loadScriptCommand(cls);
            } catch (Throwable e) {
                if (log.isWarnEnabled()) {
                    log.warn(ResourcesUtils.getMessage("class.standard.output.msg018", cls.getName()), e);
                }
            }
        }
    }

    /**
     * 加载脚本命令
     *
     * @param cls 脚本引擎命令的Class信息
     */
    public void loadScriptCommand(Class<? extends UniversalCommandCompiler> cls) {
        if (this.repository.contains(cls)) {
            return;
        }

        if (cls.isAnnotationPresent(ScriptCommand.class)) {
            ScriptCommand anno = cls.getAnnotation(ScriptCommand.class);
            String[] names = StringUtils.trimBlank(anno.name());
            String[] words = StringUtils.trimBlank(anno.keywords());

            if (StringUtils.isBlank(names)) {
                if (log.isWarnEnabled()) {
                    log.warn(ResourcesUtils.getMessage("script.message.stderr048", cls.getName(), ScriptCommand.class.getName(), "name"));
                }
                return;
            }

            UniversalCommandCompiler compiler;
            try {
                compiler = this.context.getContainer().createBean(cls);
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn(ResourcesUtils.getMessage("class.standard.output.msg012", cls.getName()), e);
                }
                return;
            }

            // 向编译器中注入脚本引擎上下文信息
            if (compiler instanceof UniversalScriptContextAware) {
                ((UniversalScriptContextAware) compiler).setContext(this.context);
            } else {
                this.invoke(compiler, this.context);
            }

            this.repository.add(names, compiler);

            if (log.isDebugEnabled()) {
                log.debug(ResourcesUtils.getMessage("script.message.stdout050", cls.getName()));
            }

            // 关键字
            for (String key : words) {
                this.factory.getKeywords().add(key);
            }
        } else {
            if (log.isDebugEnabled()) { // 只有调试模式才会打印警告
                log.warn(ResourcesUtils.getMessage("script.message.stderr052", cls.getName(), UniversalScriptCommand.class.getName(), ScriptCommand.class.getName()));
            }
        }
    }

    /**
     * 向编译器中注入脚本引擎上下文信息，如果编译器类中存在 void set(UniversalScriptContext) 方法时，自动调用方法并注入上下文信息
     *
     * @param compiler 编译器
     * @param context  上下文信息
     */
    private void invoke(UniversalCommandCompiler compiler, UniversalScriptContext context) {
        Method[] methods = compiler.getClass().getDeclaredMethods();
        for (Method method : methods) {
            Class<?>[] types = method.getParameterTypes();
            if (method.getName().startsWith("set") // 方法名
                    && "void".equalsIgnoreCase(method.getReturnType().getName()) // 无返回值
                    && types != null && types.length == 1 && types[0].equals(UniversalScriptContext.class) // 只有一个参数
            ) {
                try {
                    method.invoke(compiler, new Object[]{context});
                } catch (Throwable e) {
                    throw new UniversalScriptException(compiler.getClass().getName() + "." + StringUtils.toString(method), e);
                }
            }
        }
    }

}
