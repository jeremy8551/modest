package cn.org.expect.springboot.starter.configuration;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.springboot.starter.script.SpringBindings;
import cn.org.expect.springboot.starter.script.SpringEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * 脚本引擎的Spring配置类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/3
 */
@Configuration
public class ScriptEngineConfiguration {

    @Lazy
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UniversalScriptEngine getUniversalScriptEngine(EasyContext context, UniversalScriptEngineFactory factory) {
        UniversalScriptEngine engine = factory.getScriptEngine();
        ApplicationContext springContext = context.getBean(ApplicationContext.class);
        engine.getContext().addVariable(new SpringEnvironment(springContext), UniversalScriptContext.ENVIRONMENT_SCOPE);
        return engine;
    }

    @Lazy
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public ScriptEngine getScriptEngine(EasyContext context, ScriptEngineFactory factory) {
        ScriptEngine engine = factory.getScriptEngine();
        ApplicationContext springContext = context.getBean(ApplicationContext.class);
        engine.setBindings(new SpringBindings(springContext), UniversalScriptContext.ENVIRONMENT_SCOPE);
        return engine;
    }
}
