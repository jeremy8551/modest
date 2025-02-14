package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.io.ScriptFileExpression;

/**
 * 合并执行一个脚本文件, 脚本文件执行完毕后会同步子脚本文件的局部变量，全局变量
 */
public class DaemonCommand extends ExecuteFileCommand {

    public DaemonCommand(UniversalCommandCompiler compiler, String command, ScriptFileExpression file) {
        super(compiler, command, file);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext parent, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        return super.execute(session, parent, stdout, stderr, forceStdout, outfile, errfile);
    }

    public int execute(UniversalScriptSession session, UniversalScriptEngine engine, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, ScriptFileExpression file, String charsetName) throws Exception {
        try {
            return super.execute(session, engine, context, stdout, stderr, forceStdout, file, charsetName);
        } finally {
            // 将脚本文件产生的变量复制到其父脚本引擎中
            UniversalScriptContext parent = context.getParent();
            if (parent != null) {
                parent.getLocalVariable().putAll(engine.getContext().getLocalVariable());
                parent.getGlobalVariable().putAll(engine.getContext().getGlobalVariable());
            }
        }
    }

    public boolean enableNohup() {
        return super.enableNohup();
    }
}
