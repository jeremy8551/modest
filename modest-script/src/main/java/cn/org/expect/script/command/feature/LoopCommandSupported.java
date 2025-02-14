package cn.org.expect.script.command.feature;

/**
 * 实现本接口的命令可以在循环体语句中使用
 *
 * @author jeremy8551@gmail.com
 */
public interface LoopCommandSupported {

    /**
     * 判断是否可以在循环体语句中使用
     *
     * @return 返回 true 表示不可以在循环体语句中使用
     */
    boolean enableLoop();
}
