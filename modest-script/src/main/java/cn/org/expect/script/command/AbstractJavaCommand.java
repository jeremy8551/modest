package cn.org.expect.script.command;

import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.util.Terminator;

/**
 * Java 命令模版类，详见 {@linkplain JavaCommand} 中注释信息
 */
public abstract class AbstractJavaCommand extends Terminator {

    /**
     * 初始化
     */
    public AbstractJavaCommand() {
    }

    /**
     * 执行用户自定义逻辑
     *
     * @param session 用户会话信息
     * @param context 脚本引擎上下文信息
     * @param stdout  标准信息输出流
     * @param stderr  错误信息输出流
     * @param args    外部输入参数
     * @return 返回0表示正确 返回非0表示错误
     * @throws Exception 执行自定义逻辑发生错误
     */
    public abstract int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, String[] args) throws Exception;

    /**
     * 判断在 jump 命令过程中会不会执行 {@linkplain #execute(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, String[])} 方法
     *
     * @return 返回 true 表示在 jump 命令过程中不会执行 {@linkplain #execute(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, String[])} 方法
     */
    public abstract boolean enableJump();

    /**
     * 判断是否支持在后台运行命令
     *
     * @return 返回 true 表示在支持在后台运行命令
     */
    public abstract boolean enableNohup();

    /**
     * 判断是否支持向管道中输出信息
     *
     * @return 返回 true 表示命令可以向管道中输出信息
     */
    public abstract boolean enablePipe();
}
