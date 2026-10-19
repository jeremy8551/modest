package cn.org.expect.springboot.starter.configuration;

import cn.org.expect.springboot.starter.listener.ScriptListener;
import cn.org.expect.springboot.starter.script.SpringArgument;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * SpringBoot 应用程序启动参数
 *
 * @author jeremy8551@gmail.com
 * @createtime 2024/8/3
 */
@AutoConfiguration
@EnableConfigurationProperties(ModestProperties.class)
public class SpringArgumentConfiguration {

    @Bean
    @ConditionalOnMissingBean(SpringArgument.class)
    public SpringArgument getSpringApplicationArgument() {
        return ScriptListener.getArgument();
    }
}
