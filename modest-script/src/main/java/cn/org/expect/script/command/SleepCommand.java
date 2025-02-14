package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import cn.org.expect.expression.MillisExpression;
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
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.util.Dates;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 休眠
 */
public class SleepCommand extends AbstractTraceCommand implements UniversalScriptInputStream, NohupCommandSupported {
    private final static Log log = LogFactory.getLog(SleepCommand.class);

    /** 休眠时间，格式: 10min */
    private String time;

    public SleepCommand(UniversalCommandCompiler compiler, String command, String time) {
        super(compiler, command);
        this.time = time;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.time)) {
            this.time = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "sleep", this.time);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        long compileMillis = session.getCompiler().getCompileMillis();
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String time = analysis.replaceShellVariable(session, context, this.time, true, true);
        long millis = new MillisExpression(time).value();

        if (session.isEchoEnable() || forceStdout) {
            stdout.println("sleep " + millis + " millisecond");
        }

        // 计算休眠时间
        long sleep = millis - (System.currentTimeMillis() - compileMillis);
        if (sleep <= 0) {
            return 0;
        }

        try {
            Thread.sleep(sleep);
            return 0;
        } catch (Throwable e) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message037", this.command, sleep), e);

            // 终止命令
            if (super.isTerminate()) {
                return UniversalScriptCommand.TERMINATE;
            }

            // 休眠
            sleep = millis - (System.currentTimeMillis() - compileMillis);
            if (sleep > 0) {
                Dates.sleep(sleep);
            }
            return 0;
        }
    }

    public void terminate() throws Exception {
        super.terminate();
        try {
            Thread.interrupted();
        } catch (Throwable e) {
            if (log.isWarnEnabled()) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    public boolean enableNohup() {
        return true;
    }
}
