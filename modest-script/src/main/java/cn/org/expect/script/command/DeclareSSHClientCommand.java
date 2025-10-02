package cn.org.expect.script.command;

import java.util.Date;

import cn.org.expect.crypto.DESEncrypt;
import cn.org.expect.os.OSSecureShellCommand;
import cn.org.expect.printer.OutputStreamPrinter;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.SSHClientMap;
import cn.org.expect.util.Dates;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 建立 SSH 协议的客户端 <br>
 * declare name SSH client for connect to name@host:port?password=str <br>
 */
public class DeclareSSHClientCommand extends AbstractCommand {

    /** 客户端名 */
    private String name;

    private String host;

    private String port;

    private String username;

    private String password;

    public DeclareSSHClientCommand(UniversalCommandCompiler compiler, String command, String name, String host, String port, String username, String password) {
        super(compiler, command);
        this.name = name;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        OSSecureShellCommand client = context.getContainer().getBean(OSSecureShellCommand.class);
        if (client.connect(this.host, Integer.parseInt(this.port), this.username, this.password)) {
            if (session.isEchoEnable() || forceStdout) {
                stdout.println(ResourcesUtils.getMessage("script.stdout.message039", this.name, this.username + "@" + this.host + ":" + this.port + "?password=" + DESEncrypt.encrypt(this.password, context.getCharsetName(), StringUtils.toBytes(Dates.format08(new Date()), context.getCharsetName()))));
            }

            client.setStdout(new OutputStreamPrinter(stdout, client.getCharsetName()));
            client.setStderr(new OutputStreamPrinter(stderr, client.getCharsetName()));

            String name = analysis.replaceShellVariable(session, context, this.name, true, true);
            SSHClientMap.get(context).add(name, client);
            return 0;
        } else {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message034", this.command, "ssh2"));
            return UniversalScriptCommand.COMMAND_ERROR;
        }
    }
}
