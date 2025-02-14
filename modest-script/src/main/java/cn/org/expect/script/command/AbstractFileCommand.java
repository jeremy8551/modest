package cn.org.expect.script.command;

import cn.org.expect.script.UniversalCommandCompiler;

/**
 * 文件操作类命令模版
 *
 * @author jeremy8551@gmail.com
 */
public abstract class AbstractFileCommand extends AbstractTraceCommand {

    public AbstractFileCommand(UniversalCommandCompiler compiler, String str) {
        super(compiler, str);
    }
}
