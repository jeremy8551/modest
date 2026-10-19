package cn.org.expect.springboot.starter.configuration;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.springboot.starter.script.SpringBindings;
import cn.org.expect.springboot.starter.script.SpringEnvironment;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 脚本引擎的Spring配置类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/3
 */
@AutoConfiguration
public class ScriptEngineConfiguration {

    /** springContext变量名 */
    public final static String SPRING_CONTEXT_VARIABLE_NAME = "springContext";

    @Bean
    @ConditionalOnMissingBean(UniversalScriptEngine.class)
    public UniversalScriptEngine getUniversalScriptEngine(EasyContext context, UniversalScriptEngineFactory factory) {
        UniversalScriptEngine engine = factory.getScriptEngine();
        ApplicationContext springContext = context.getBean(ApplicationContext.class);
        engine.getContext().addVariable(SPRING_CONTEXT_VARIABLE_NAME, springContext, UniversalScriptContext.GLOBAL_SCOPE);
        engine.getContext().addVariable(new SpringEnvironment(springContext), UniversalScriptContext.ENVIRONMENT_SCOPE);
        return engine;
    }

    @Bean
    @ConditionalOnMissingBean(ScriptEngine.class)
    public ScriptEngine getScriptEngine(EasyContext context, ScriptEngineFactory factory) {
        ScriptEngine engine = factory.getScriptEngine();
        ApplicationContext springContext = context.getBean(ApplicationContext.class);
        engine.getContext().getBindings(UniversalScriptContext.GLOBAL_SCOPE).put(SPRING_CONTEXT_VARIABLE_NAME, springContext);
        engine.setBindings(new SpringBindings(springContext), UniversalScriptContext.ENVIRONMENT_SCOPE);
        return engine;
    }
}
