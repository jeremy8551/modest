package cn.org.expect.script.command;

import java.io.File;
import java.io.Reader;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptInputStream;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.JumpCommandSupported;
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.io.ScriptNullStderr;
import cn.org.expect.script.io.ScriptNullStdout;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

/**
 * 以静默方式执行下一个命令，即使下一个命令执行报错也不会输出任何信息
 */
public class QuietCommand extends AbstractTraceCommand implements UniversalScriptInputStream, LoopCommandSupported, JumpCommandSupported, NohupCommandSupported {
    private final static Log log = LogFactory.getLog(QuietCommand.class);

    /** 子命令 */
    private UniversalScriptCommand subcommand;

    public QuietCommand(UniversalCommandCompiler compiler, String command, UniversalScriptCommand subcommand) {
        super(compiler, command);
        this.subcommand = subcommand;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws Exception {
        if (this.subcommand != null) {
            throw new UniversalScriptException("script.stderr.message012", this.command, "quiet", this.subcommand.getScript());
        }

        String script = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        this.command = "quiet " + script;
        List<UniversalScriptCommand> list = parser.read(script);
        if (list.size() == 1) {
            this.subcommand = list.get(0);
        } else {
            throw new UniversalScriptException("script.stderr.message063", script);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            stdout.println(session.getAnalysis().replaceShellVariable(session, context, this.command, true, false));
        }

        int value;
        try {
            value = this.subcommand.execute(session, context, new ScriptNullStdout(stdout), new ScriptNullStderr(stderr), forceStdout);
        } catch (Throwable e) {
            value = UniversalScriptCommand.COMMAND_ERROR;
            if (log.isDebugEnabled() && this.subcommand != null) {
                log.debug(this.subcommand.getScript(), e);
            }
        }

        session.putValue(value);
        return 0;
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.subcommand != null) {
            this.subcommand.terminate();
        }
    }

    public boolean enableNohup() {
        return this.subcommand == null ? false : (this.subcommand instanceof NohupCommandSupported) && ((NohupCommandSupported) this.subcommand).enableNohup();
    }

    public boolean enableJump() {
        return this.subcommand == null ? false : (this.subcommand instanceof JumpCommandSupported) && ((JumpCommandSupported) this.subcommand).enableJump();
    }

    public boolean enableLoop() {
        return this.subcommand == null ? false : (this.subcommand instanceof LoopCommandSupported) && ((LoopCommandSupported) this.subcommand).enableLoop();
    }
}
