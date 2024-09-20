package cn.org.expect.springboot.starter.ioc;

import cn.org.expect.ioc.impl.EasyBeanDefineImpl;
import org.springframework.context.ApplicationContext;

/**
 * 将 Spring 容器上下文信息转为可识别的组件信息
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/26
 */
public class SpringEasyBeanDefine extends EasyBeanDefineImpl {

    /**
     * 将 Spring 容器上下文信息转为 EasyBean 中可识别的组件信息
     *
     * @param springContext Spring 容器上下文信息
     */
    public SpringEasyBeanDefine(ApplicationContext springContext) {
        super(springContext.getClass());
        this.singleton = true;
        this.setBean(springContext);
    }
}
