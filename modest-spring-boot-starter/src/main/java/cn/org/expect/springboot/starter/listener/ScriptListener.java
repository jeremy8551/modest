package cn.org.expect.springboot.starter.listener;

import cn.org.expect.springboot.starter.script.SpringArgument;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

/**
 * 通知Spring已启动完毕
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/8
 */
public class ScriptListener implements ApplicationListener<ApplicationStartingEvent> {

    private static SpringArgument ARGUMENT;

    public ScriptListener() {
    }

    /**
     * 通知Spring已启动完毕
     *
     * @param event the event to respond to
     */
    public void onApplicationEvent(ApplicationStartingEvent event) {
        ScriptListener.ARGUMENT = new SpringArgument(event.getSpringApplication(), event.getArgs());
    }

    public static SpringArgument getArgument() {
        return ARGUMENT;
    }
}
