package cn.org.expect.springboot.starter.configuration;

import cn.org.expect.day.NationalHoliday;
import cn.org.expect.io.Codepage;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.springboot.starter.SpringApplicationArgument;
import cn.org.expect.springboot.starter.ioc.EasyContextFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 * 脚本引擎容器的Spring配置类 <br>
 * <br>
 * 因为要防止同时引入多个模块的类，会引起启动混乱，所以要将脚本引擎容器与脚本引擎工厂分开 <br>
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/3
 */
@Configuration
public class EasyContextConfiguration {

    @Lazy
    @Bean
    @Scope("singleton")
    public synchronized EasyContext getEasyContext(ApplicationContext springContext, SpringApplicationArgument argument) {
        return EasyContextFactory.create(argument, springContext);
    }

    @Lazy
    @Bean
    public NationalHoliday getNationalHoliday(EasyContext context) {
        return context.getBean(NationalHoliday.class);
    }

    @Lazy
    @Bean
    public Codepage getCodepage(EasyContext context) {
        return context.getBean(Codepage.class);
    }
}
