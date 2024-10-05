package cn.org.expect.springboot.starter;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;

/**
 * Springboot程序启动参数
 *
 * @author jeremy8551@qq.com
 * @createtime 2024/2/7 15:24
 */
public class SpringApplicationArgument {

    /** 当前SpringBoot应用 */
    private SpringApplication application;

    /** SpringBoot应用的启动参数 */
    private String[] args;

    public SpringApplicationArgument(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }

    public SpringApplication getApplication() {
        return application;
    }

    public String[] getArgs() {
        return args;
    }

    public String toString() {
        return "SpringApplicationArgument{" + "application=" + application + ", args=" + Arrays.toString(args) + '}';
    }
}
