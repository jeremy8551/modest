package cn.org.expect.springboot.starter.configuration;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.script.UniversalScriptEngineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * 脚本引擎工厂的Spring配置类
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/3
 */
@Configuration
public class UniversalScriptEngineFactoryConfiguration {

    @Lazy
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UniversalScriptEngineFactory getScriptEngineFactory(EasyContext context) {
        return new UniversalScriptEngineFactory(context);
    }

}

