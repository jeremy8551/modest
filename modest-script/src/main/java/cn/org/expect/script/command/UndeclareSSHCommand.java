package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.SSHClientMap;
import cn.org.expect.util.ResourcesUtils;

/**
 * 关闭 SSH 客户端或端口转发协议 <br>
 * undeclare name ssh [ client | tunnel ];
 */
public class UndeclareSSHCommand extends AbstractTraceCommand {

    /** SSH客户端名 或 端口转发协议名 */
    private String name;

    /** client 或 tunnel */
    private String type;

    public UndeclareSSHCommand(UniversalCommandCompiler compiler, String command, String name, String type) {
        super(compiler, command);
        this.name = name;
        this.type = type;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        boolean print = session.isEchoEnable() || forceStdout;
        if ("client".equalsIgnoreCase(this.type)) {
            if (print) {
                stdout.println(ResourcesUtils.getMessage("script.stdout.message040", this.name));
            }

            SSHClientMap.get(context).close(this.name);
            return 0;
        } else if ("tunnel".equalsIgnoreCase(this.type)) {
            if (print) {
                stdout.println(ResourcesUtils.getMessage("script.stdout.message041", this.name));
            }

            SSHClientMap.get(context).close(this.name);
            return 0;
        } else {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message094", this.command, this.type, "client, tunnel"));
            return UniversalScriptCommand.COMMAND_ERROR;
        }
    }
}
