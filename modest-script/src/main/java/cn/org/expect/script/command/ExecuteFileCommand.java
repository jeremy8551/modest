package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.io.PathExpression;

/**
 * 执行脚本文件
 */
public class ExecuteFileCommand extends AbstractTraceCommand implements NohupCommandSupported {

    /** 子脚本的会话信息 */
    private volatile UniversalScriptSession session;

    /** 脚本文件 */
    protected PathExpression file;

    /** 脚本文件的参数 */
    protected String[] parameters;

    public ExecuteFileCommand(UniversalCommandCompiler compiler, String command, PathExpression file, String[] parameters) {
        super(compiler, command);
        this.file = file;
        this.parameters = parameters;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext parent, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        String charsetName = parent.getCharsetName();

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(". " + this.file.getAbsolutePath());
        }

        UniversalScriptEngine engine = parent.getEngine().getFactory().getScriptEngine();
        UniversalScriptContext context = engine.getContext();
        context.setParent(parent);
        engine.setWriter(stdout.getWriter());
        engine.setErrorWriter(stderr.getWriter());
        engine.setStepWriter(parent.getEngine().getStepWriter());

        try {
            return this.execute(session, engine, context, stdout, stderr, forceStdout, this.file, this.parameters, charsetName);
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
     * @param file        脚本文件
     * @param parameters  脚本文件的参数
     * @param charsetName 脚本文件字符集
     * @return 0表示命令执行成功 非0表示发生错误
     * @throws Exception 脚本命令错误
     */
    public int execute(UniversalScriptSession session, UniversalScriptEngine engine, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, PathExpression file, String[] parameters, String charsetName) throws Exception {
        this.session = session.subsession();
        try {
            this.session.setScriptFilepath(file.getAbsolutePath()); // 脚本文件
            this.session.setScriptParameters(parameters); // 脚本参数
            return engine.evaluate(this.session, context, stdout, stderr, forceStdout, file.getReader(context.getCharsetName()));
        } finally {
            try {
                UniversalScriptContext parent = context.getParent();
                if (parent != null && parent.getParent() == null) { // 父脚本引擎是发起方时，需要保留变量信息
                    parent.getLocalVariable().putAll(engine.getContext().getLocalVariable());
                    parent.getGlobalVariable().putAll(engine.getContext().getGlobalVariable());
                }
            } finally {
                session.setValue(this.session.getValue());
                this.session.close();
            }
        }
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.session != null) {
            this.session.terminate();
        }
    }

    public boolean enableNohup() {
        return true;
    }
}
