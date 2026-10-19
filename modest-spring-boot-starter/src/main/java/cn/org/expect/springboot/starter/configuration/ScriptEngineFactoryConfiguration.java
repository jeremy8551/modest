package cn.org.expect.springboot.starter.configuration;

import javax.script.ScriptEngineFactory;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.javax.script.ScriptEngineFactoryImpl;
import cn.org.expect.script.UniversalScriptEngineFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 脚本引擎工厂的Spring配置类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/3
 */
@AutoConfiguration
public class ScriptEngineFactoryConfiguration {

    @Bean
    @ConditionalOnMissingBean(UniversalScriptEngineFactory.class)
    public UniversalScriptEngineFactory getUniversalScriptEngineFactory(EasyContext context) {
        return context.getBean(UniversalScriptEngineFactory.class);
    }

    @Bean
    @ConditionalOnMissingBean(ScriptEngineFactory.class)
    public ScriptEngineFactory getScriptEngineFactory(UniversalScriptEngineFactory factory) {
        return new ScriptEngineFactoryImpl(factory);
    }
}
