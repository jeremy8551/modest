package cn.org.expect.springboot.starter.configuration;

import javax.script.ScriptEngineFactory;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.script.spi.ScriptEngineFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 * 脚本引擎工厂的Spring配置类
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/3
 */
@Configuration
public class ScriptEngineFactoryConfiguration {

    @Lazy
    @Bean
    @Scope("singleton")
    public UniversalScriptEngineFactory getUniversalScriptEngineFactory(EasyContext context) {
        return new UniversalScriptEngineFactory(context);
    }

    @Lazy
    @Bean
    @Scope("singleton")
    public ScriptEngineFactory getScriptEngineFactory(UniversalScriptEngineFactory factory) {
        return new ScriptEngineFactoryImpl(factory);
    }
}
