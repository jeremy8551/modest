package cn.org.expect.script.io;

import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;

/**
 * 提供脚本错误输出的空实现
 */
public class ScriptNullStderr extends ScriptNullStdout implements UniversalScriptStderr {

    /**
     * 初始化 ScriptNullStderr
     */
    public ScriptNullStderr(UniversalScriptStdout proxy) {
        super(proxy);
    }
}
