package cn.org.expect.script.method;

import java.util.List;

import cn.org.expect.annotation.ScriptFunction;
import cn.org.expect.ioc.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 扫描并加载脚本引擎变量方法
 *
 * @author jeremy8551@qq.com
 * @createtime 2021-02-09
 */
public class VariableMethodScanner {
    private final static Log log = LogFactory.getLog(VariableMethodScanner.class);

    /** 脚本引擎变量方法的工厂类 */
    private VariableMethodRepository repository;

    /** 脚本引擎工厂 */
    private UniversalScriptEngineFactory factory;

    private UniversalScriptContext context;

    /**
     * 初始化
     *
     * @param context    脚本引擎上下文信息
     * @param repository 变量方法仓库
     */
    public VariableMethodScanner(UniversalScriptContext context, VariableMethodRepository repository) {
        this.context = context;
        this.factory = context.getFactory();
        this.repository = repository;

        // 显示所有已加载的变量方法
        List<EasyBean> beanInfoList = this.factory.getContext().getBeanInfoList(UniversalScriptVariableMethod.class);
        for (EasyBean beanInfo : beanInfoList) {
            this.loadVariableMethod(beanInfo.getType());
        }

        if (log.isDebugEnabled() && !this.repository.isEmpty()) {
            log.debug(this.repository.toString(null));
        }
    }

    /**
     * 加载变量方法
     *
     * @param cls 变量方法的Class信息
     */
    public void loadVariableMethod(Class<?> cls) {
        if (this.repository.contains(cls)) {
            return;
        }

        if (cls.isAnnotationPresent(ScriptFunction.class)) {
            ScriptFunction anno = cls.getAnnotation(ScriptFunction.class);
            String name = StringUtils.trimBlank(anno.name());
            if (StringUtils.isBlank(name)) {
                return;
            }

            String[] words = StringUtils.trimBlank(anno.keywords());
            UniversalScriptVariableMethod method = this.repository.get(name);
            if (method != null) {
                throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr128", name, method.getClass().getName(), cls.getName()));
            }

            try {
                method = this.context.getContainer().createBean(cls);
            } catch (Throwable e) {
                if (log.isWarnEnabled()) {
                    log.warn(ResourcesUtils.getMessage("script.message.stdout042", cls.getName()), e);
                }
                return;
            }

            // 保存变量方法
            this.repository.add(name, method);

            if (log.isDebugEnabled()) {
                log.debug(ResourcesUtils.getMessage("script.message.stdout051", cls.getName()));
            }

            // 添加关键字
            for (String key : words) {
                this.factory.getKeywords().add(key);
            }
        } else {
            if (log.isDebugEnabled()) { // 只有调试模式才会打印警告
                log.warn(ResourcesUtils.getMessage("script.message.stderr052", cls.getName(), UniversalScriptVariableMethod.class.getName(), ScriptFunction.class.getName()));
            }
        }
    }

}
