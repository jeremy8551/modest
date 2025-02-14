package cn.org.expect.script.io;

import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;

public class ScriptNullStderr extends ScriptNullStdout implements UniversalScriptStderr {

    public ScriptNullStderr(UniversalScriptStdout proxy) {
        super(proxy);
    }
}
