package cn.org.expect.springboot.starter.configuration;

import cn.org.expect.cn.NationalHoliday;
import cn.org.expect.io.Codepage;
import cn.org.expect.ioc.EasyetlContext;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptEngineFactory;
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
 * @author jeremy8551@qq.com
 * @createtime 2023/10/3
 */
@Configuration
public class UniversalScriptEngineConfiguration {

    @Lazy
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UniversalScriptEngine getScriptEngine(EasyetlContext context, UniversalScriptEngineFactory factory) {
        UniversalScriptEngine engine = factory.getScriptEngine();
        ApplicationContext springContext = context.getBean(ApplicationContext.class);
        SpringEnvironment bindings = new SpringEnvironment(springContext);
        engine.setBindings(bindings, UniversalScriptContext.ENVIRONMENT_SCOPE);
        return engine;
    }

    @Lazy
    @Bean
    public NationalHoliday getNationalHoliday(EasyetlContext context) {
        return context.getBean(NationalHoliday.class);
    }

    @Lazy
    @Bean
    public Codepage getCodepage(EasyetlContext context) {
        return context.getBean(Codepage.class);
    }

}

