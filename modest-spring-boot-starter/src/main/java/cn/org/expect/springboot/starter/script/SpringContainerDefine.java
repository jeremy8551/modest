package cn.org.expect.springboot.starter.script;

import cn.org.expect.ioc.impl.DefaultBeanEntry;
import org.springframework.context.ApplicationContext;

/**
 * 将 Spring 容器上下文信息转为可识别的组件信息
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/26
 */
public class SpringContainerDefine extends DefaultBeanEntry {

    /**
     * 将 Spring 容器上下文信息转为 EasyBean 中可识别的组件信息
     *
     * @param springContext Spring 容器上下文信息
     */
    public SpringContainerDefine(ApplicationContext springContext) {
        super(springContext.getClass());
        this.singleton = true;
        this.setBean(springContext);
    }
}
