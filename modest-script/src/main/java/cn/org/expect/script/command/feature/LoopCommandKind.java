package cn.org.expect.script.command.feature;

/**
 * 循环体的控制命令
 *
 * @author jeremy8551@gmail.com
 */
public interface LoopCommandKind {

    /** exit 命令 */
    int EXIT_COMMAND = 10;

    /** return 命令 */
    int RETURN_COMMAND = 20;

    /** continue 命令 */
    int CONTINUE_COMMAND = 30;

    /** break 命令 */
    int BREAK_COMMAND = 40;

    /**
     * 返回控制类型
     *
     * @return 控制类型
     */
    int kind();
}
