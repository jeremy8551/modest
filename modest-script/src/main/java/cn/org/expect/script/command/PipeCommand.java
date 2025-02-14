package cn.org.expect.script.command;

import java.util.List;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandResultSet;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptInputStream;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.io.ScriptStdbuf;
import cn.org.expect.script.session.ScriptMainProcess;

public class PipeCommand extends AbstractCommand implements NohupCommandSupported {

    private final List<UniversalScriptCommand> list;

    private UniversalScriptCommand run;

    public PipeCommand(UniversalCommandCompiler compiler, String command, List<UniversalScriptCommand> commands) {
        super(compiler, command);
        this.list = commands;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        int size = this.list.size();
        int last = size - 1;
        UniversalScriptParser parser = session.getCompiler().getParser();

        ScriptMainProcess process = session.getMainProcess();
        ScriptStdbuf cache = new ScriptStdbuf(stdout);
        for (int i = 0; !this.terminate && i < size; i++) {
            UniversalScriptCommand command = this.list.get(i);
            this.run = command;

            if (i > 0 && command instanceof UniversalScriptInputStream) {
                if (log.isDebugEnabled()) {
                    log.debug("script.stdout.message027", command.getClass().getName(), cache.toString());
                }

                UniversalScriptInputStream pipe = (UniversalScriptInputStream) command;
                pipe.read(session, context, parser, session.getAnalysis(), cache.toReader());
            }

            cache.clear();
            UniversalCommandResultSet result = process.execute(session, context, (i == last ? stdout : cache), stderr, true, command);
            int value = result.getExitcode();
            if (value != 0) {
                return UniversalScriptCommand.COMMAND_ERROR;
            }
        }

        return this.terminate ? UniversalScriptCommand.TERMINATE : 0;
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.run != null) {
            this.run.terminate();
        }
    }

    public boolean enableNohup() {
        return true;
    }
}
