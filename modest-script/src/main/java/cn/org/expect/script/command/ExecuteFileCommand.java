package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.io.ScriptFileExpression;

/**
 * 执行脚本文件
 */
public class ExecuteFileCommand extends AbstractTraceCommand implements NohupCommandSupported {

    /** 子脚本的会话信息 */
    private UniversalScriptSession session;

    /** 脚本文件的绝对路径 */
    protected String filepath;

    public ExecuteFileCommand(UniversalCommandCompiler compiler, String command, String filepath) {
        super(compiler, command);
        this.filepath = filepath;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext parent, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        String charsetName = parent.getCharsetName();
        ScriptFileExpression file = new ScriptFileExpression(session, parent, this.filepath);

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(". " + file.getAbsolutePath());
        }

        UniversalScriptEngine engine = new UniversalScriptEngine(parent.getEngine());
        UniversalScriptContext context = engine.getContext();
        context.setWriter(stdout.getWriter());
        context.setErrorWriter(stderr.getWriter());
        context.setStepWriter(parent.getStepWriter());

        try {
            return this.execute(session, engine, context, stdout, stderr, forceStdout, file, charsetName);
        } finally {
            engine.close();
        }
    }

    /**
     * 执行脚本文件对应的脚本引擎
     *
     * @param session     用户会话信息
     * @param engine      脚本文件对应的脚本引擎
     * @param context     脚本文件对应的上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @param file        脚本文件表达式
     * @param charsetName 脚本文件字符集
     * @return 0表示命令执行成功 非0表示命令错误
     * @throws Exception 脚本命令错误
     */
    public int execute(UniversalScriptSession session, UniversalScriptEngine engine, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, ScriptFileExpression file, String charsetName) throws Exception {
        this.session = session.subsession();
        try {
            this.session.setScriptFile(file); // 设置脚本文件
            return engine.eval(this.session, context, stdout, stderr, forceStdout, file.getReader());
        } finally {
            try {
                UniversalScriptContext parent = context.getParent();
                if (parent != null && parent.getParent() == null) { // 父脚本引擎是发起方时，需要保留变量信息
                    parent.getLocalVariable().addAll(engine.getContext().getLocalVariable());
                    parent.getGlobalVariable().addAll(engine.getContext().getGlobalVariable());
                }
            } finally {
                this.session.close();
            }
        }
    }

    public void terminate() throws Exception {
        if (this.session != null) {
            this.session.terminate();
        }
    }

    public boolean enableNohup() {
        return true;
    }

}
