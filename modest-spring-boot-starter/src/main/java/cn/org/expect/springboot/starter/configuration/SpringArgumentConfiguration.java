package cn.org.expect.springboot.starter.configuration;

import cn.org.expect.springboot.starter.listener.ScriptListener;
import cn.org.expect.springboot.starter.script.SpringArgument;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 * SpringBoot 应用程序启动参数
 *
 * @author jeremy8551@gmail.com
 * @createtime 2024/8/3
 */
@Configuration
public class SpringArgumentConfiguration {

    @Bean
    @Lazy
    @Scope("singleton")
    public SpringArgument getSpringApplicationArgument() {
        return ScriptListener.getArgument();
    }
}
