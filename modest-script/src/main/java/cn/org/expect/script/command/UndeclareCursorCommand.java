package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.CursorMap;
import cn.org.expect.util.ResourcesUtils;

/**
 * undeclare name cursor;
 *
 * @author jeremy8551@gmail.com
 */
public class UndeclareCursorCommand extends AbstractTraceCommand {

    /** 游标名 */
    private final String name;

    public UndeclareCursorCommand(UniversalCommandCompiler compiler, String command, String name) {
        super(compiler, command);
        this.name = name;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            stdout.println("undeclare " + this.name + " cursor");
        }

        CursorMap map = CursorMap.get(context);
        if (map.remove(this.name) == null) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message002", this.command, this.name));
            return UniversalScriptCommand.COMMAND_ERROR;
        } else {
            return 0;
        }
    }
}
