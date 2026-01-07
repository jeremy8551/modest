package cn.org.expect.script.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandResultSet;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.LoopCommandKind;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.internal.CommandList;
import cn.org.expect.script.internal.FunctionSet;
import cn.org.expect.script.session.ScriptMainProcess;
import cn.org.expect.util.CollectionUtils;

public class ExecuteFunctionCommand extends AbstractTraceCommand implements NohupCommandSupported {

    /** 自定义方法的输入参数 */
    private String parameters;

    /** 正在运行的脚本命令 */
    protected volatile UniversalScriptCommand command;

    public ExecuteFunctionCommand(UniversalCommandCompiler compiler, String command, String parameters) {
        super(compiler, command);
        this.parameters = parameters;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        List<String> list = new ArrayList<String>();
        analysis.split(this.parameters, list);
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            list.set(i, analysis.replaceShellVariable(session, context, analysis.unQuotation(str), true, !analysis.containsQuotation(str)));
        }
        String[] args = CollectionUtils.toArray(list);

        String name = args[0];
        boolean reverse = name.startsWith("!");
        String functionName = reverse ? name.substring(1) : name;
        CommandList body = FunctionSet.get(context, false).get(functionName); // 优先从局部域中查询自定义方法
        if (body == null) { //
            body = FunctionSet.get(context, true).get(functionName); // 从全局域中查找自定义方法
        }

        int exitcode = this.execute(session, context, stdout, stderr, forceStdout, body, args);
        if (reverse) {
            return exitcode == 0 ? UniversalScriptCommand.COMMAND_ERROR : 0;
        } else {
            return exitcode;
        }
    }

    /**
     * 执行用户自定义方法
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标注信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @param body        自定义方法内容
     * @param args        自定义方法的参数, 第一个值是方法名, 从第二个值开始是方法参数
     * @return 返回0表示方法执行成功
     * @throws Exception 执行自定义方法发生错误
     */
    protected int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, CommandList body, String[] args) throws Exception {
        try {
            if (session.isTerminate()) {
                return UniversalScriptCommand.TERMINATE;
            }

            ScriptMainProcess process = session.getMainProcess();
            session.setFunctionParameter(args);
            for (int i = 0; !session.isTerminate() && i < body.size(); i++) {
                UniversalScriptCommand command = body.get(i);
                this.command = command;
                if (command == null) {
                    continue;
                }

                UniversalCommandResultSet result = process.execute(session, context, stdout, stderr, forceStdout, command);
                int exitcode = result.getExitcode();
                if (exitcode != 0) {
                    return exitcode;
                }

                if (command instanceof LoopCommandKind) {
                    LoopCommandKind cmd = (LoopCommandKind) command;
                    int type = cmd.kind();
                    if (type == LoopCommandKind.EXIT_COMMAND) { // Exit script
                        return exitcode;
                    } else if (type == LoopCommandKind.RETURN_COMMAND) { // Exit method
                        return exitcode;
                    } else if (type == LoopCommandKind.BREAK_COMMAND) { // break
                        throw new UniversalScriptException("script.stderr.message028", this.getScript());
                    } else if (type == LoopCommandKind.CONTINUE_COMMAND) { // continue
                        throw new UniversalScriptException("script.stderr.message029", this.getScript());
                    }
                }
            }

            if (session.isTerminate()) {
                return UniversalScriptCommand.TERMINATE;
            } else {
                return 0;
            }
        } finally {
            this.command = null;
            session.removeFunctionParameter();
        }
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.command != null) {
            this.command.terminate();
        }
    }

    public boolean enableNohup() {
        return true;
    }
}
