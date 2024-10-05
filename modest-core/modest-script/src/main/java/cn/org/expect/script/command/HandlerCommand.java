package cn.org.expect.script.command;

import java.io.File;
import java.util.Iterator;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.command.feature.WithBodyCommandSupported;
import cn.org.expect.script.internal.ErrorHandlerMap;
import cn.org.expect.script.internal.ExitHandlerMap;
import cn.org.expect.script.internal.ScriptHandler;
import cn.org.expect.util.ResourcesUtils;

/**
 * 打印所有处理逻辑
 */
public class HandlerCommand extends AbstractTraceCommand implements NohupCommandSupported, WithBodyCommandSupported {

    public HandlerCommand(UniversalCommandCompiler compiler, String command) {
        super(compiler, command);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        boolean print = session.isEchoEnable() || forceStdout;
        if (print) {
            ErrorHandlerMap gehm = ErrorHandlerMap.get(context, true);
            ErrorHandlerMap lehm = ErrorHandlerMap.get(context, false);
            ExitHandlerMap gedm = ExitHandlerMap.get(context, true);
            ExitHandlerMap ledm = ExitHandlerMap.get(context, false);

            int count = 0;

            if (gehm != null) {
                count += gehm.size();
            }

            if (lehm != null) {
                count += lehm.size();
            }

            if (gedm != null) {
                count += gedm.size();
            }

            if (ledm != null) {
                count += ledm.size();
            }

            if (print) {
                stdout.println(ResourcesUtils.getMessage("script.message.stdout015", session.getScriptName(), count));
            }

            // 全局
            for (Iterator<ScriptHandler> it = gehm.values().iterator(); it.hasNext(); ) {
                ScriptHandler obj = it.next();
                if (obj != null && print) {
                    stdout.println(obj.toString(true));
                }
            }

            // 局部
            for (Iterator<ScriptHandler> it = lehm.values().iterator(); it.hasNext(); ) {
                ScriptHandler obj = it.next();
                if (obj != null && print) {
                    stdout.println(obj.toString(false));
                }
            }

            // 全局
            for (Iterator<ScriptHandler> it = gedm.values().iterator(); it.hasNext(); ) {
                ScriptHandler obj = it.next();
                if (obj != null && print) {
                    stdout.println(obj.toString(true));
                }
            }

            // 局部
            for (Iterator<ScriptHandler> it = ledm.values().iterator(); it.hasNext(); ) {
                ScriptHandler obj = it.next();
                if (obj != null && print) {
                    stdout.println(obj.toString(false));
                }
            }
        }

        return 0;
    }

    public void terminate() throws Exception {
    }

    public boolean enableNohup() {
        return true;
    }

}
