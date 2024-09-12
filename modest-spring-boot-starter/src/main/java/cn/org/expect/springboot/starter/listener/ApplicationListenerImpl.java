package cn.org.expect.springboot.starter.listener;

import cn.org.expect.springboot.starter.SpringApplicationArgument;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

/**
 * 通知Spring已启动完毕
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/11/8
 */
public class ApplicationListenerImpl implements ApplicationListener<ApplicationStartingEvent> {

    public static volatile SpringApplicationArgument ARGUMENT;

    public ApplicationListenerImpl() {
    }

    /**
     * 通知Spring已启动完毕
     *
     * @param event the event to respond to
     */
    public void onApplicationEvent(ApplicationStartingEvent event) {
        ApplicationListenerImpl.ARGUMENT = new SpringApplicationArgument(event.getSpringApplication(), event.getArgs());
    }
}
