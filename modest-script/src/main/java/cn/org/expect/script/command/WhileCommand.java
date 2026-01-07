package cn.org.expect.script.command;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandResultSet;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptExpression;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.LoopCommandKind;
import cn.org.expect.script.command.feature.WithBodyCommandSupported;
import cn.org.expect.script.internal.CommandList;
import cn.org.expect.script.session.ScriptMainProcess;

/**
 * 执行 while 循环 <br>
 * <p>
 * while .. loop ... end loop
 */
public class WhileCommand extends AbstractCommand implements WithBodyCommandSupported, LoopCommandKind {

    /** while 语句中执行代码块 */
    protected CommandList body;

    /** 正在运行的脚本命令 */
    protected volatile UniversalScriptCommand command;

    /** 种类编号 */
    protected int type;

    public WhileCommand(UniversalCommandCompiler compiler, String command, CommandList body) {
        super(compiler, command);
        this.body = body;
        this.body.setOwner(this);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        try {
            ScriptMainProcess process = session.getMainProcess();
            boolean isbreak = false, iscontinue = false;
            while (!session.isTerminate() && new UniversalScriptExpression(session, context, stdout, stderr, this.body.getName()).booleanValue()) {
                iscontinue = false;

                for (int i = 0; !session.isTerminate() && i < this.body.size(); i++) {
                    UniversalScriptCommand command = this.body.get(i);
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
                        this.type = cmd.kind();
                        int type = cmd.kind();
                        if (type == LoopCommandKind.BREAK_COMMAND) { // break
                            isbreak = true;
                            break;
                        } else if (type == LoopCommandKind.CONTINUE_COMMAND) { // continue
                            iscontinue = true;
                            break;
                        } else if (type == LoopCommandKind.EXIT_COMMAND) { // Exit script
                            return exitcode;
                        } else if (type == LoopCommandKind.RETURN_COMMAND) { // Exit the result set loop
                            return exitcode;
                        }
                    }
                }

                if (isbreak) {
                    break;
                }

                if (iscontinue) {
                    continue;
                }
            }

            if (session.isTerminate()) {
                return UniversalScriptCommand.TERMINATE;
            } else {
                return 0;
            }
        } finally {
            this.command = null;
        }
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.command != null) {
            this.command.terminate();
        }
    }

    public int kind() {
        return this.type;
    }
}
