package cn.org.expect.script.command.feature;

import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;

public interface JumpCommandSupported {

    /**
     * 判断命令在 jump 命令过程中不会执行 {@linkplain UniversalScriptCommand#execute(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, boolean)} 方法
     *
     * @return 返回 true 表示命令在 jump 命令过程中不会执行 {@linkplain UniversalScriptCommand#execute(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, boolean)} 方法
     */
    boolean enableJump();
}
