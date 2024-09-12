package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.os.OSSecureShellCommand;
import cn.org.expect.printer.OutputStreamPrinter;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.JumpCommandSupported;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.io.ScriptNullStdout;
import cn.org.expect.util.ResourcesUtils;

/**
 * 建立 SSH 客户端 <br>
 * {@literal ssh user@127.0.0.1:22?password=user && ./test.sh && ./run.sh}
 */
public class SSH2Command extends AbstractTraceCommand implements JumpCommandSupported, NohupCommandSupported {

    /** 登录信息 */
    private String oscommand;
    private String host;
    private String port;
    private String username;
    private String password;
    private OSSecureShellCommand client;

    public SSH2Command(UniversalCommandCompiler compiler, String command, String host, String port, String username, String password, String oscommand) {
        super(compiler, command);
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.oscommand = oscommand;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String host = analysis.replaceShellVariable(session, context, this.host, false, true, true, false);
        String port = analysis.replaceShellVariable(session, context, this.port, false, true, true, false);
        String username = analysis.replaceShellVariable(session, context, this.username, false, true, true, false);
        String password = analysis.replaceShellVariable(session, context, this.password, false, true, true, false);
        String oscommand = analysis.replaceShellVariable(session, context, this.oscommand, true, true, false, true);

        boolean print = session.isEchoEnable() || forceStdout;
        if (print) {
            stdout.println("ssh " + username + "@" + host + ":" + port + "?password=" + password);
        }

        if (analysis.isBlankline(oscommand)) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr098", this.command));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        // 登陆服务器执行命令
        this.client = context.getContainer().getBean(OSSecureShellCommand.class);
        try {
            if (!this.client.connect(host, Integer.parseInt(port), username, password)) {
                stderr.println(ResourcesUtils.getMessage("script.message.stderr038", "ssh2"));
                return UniversalScriptCommand.COMMAND_ERROR;
            }

            this.client.setStdout(new OutputStreamPrinter(print ? stdout : new ScriptNullStdout(stdout), this.client.getCharsetName()));
            this.client.setStderr(new OutputStreamPrinter(stderr, this.client.getCharsetName()));

            if (print) {
                stdout.println(oscommand);
            }

            int exitcode = this.client.execute(oscommand, 0);
            if (exitcode == 0) {
                return 0;
            } else {
                String errout = this.client.getStderr();
                if (!analysis.isBlankline(errout)) {
                    stderr.println(errout);
                }
                stderr.println(ResourcesUtils.getMessage("script.message.stderr020", oscommand, exitcode, host));
                return exitcode;
            }
        } finally {
            this.client.close();
        }
    }

    public void terminate() throws Exception {
        if (this.client != null) {
            this.client.terminate();
        }
    }

    public boolean enableNohup() {
        return true;
    }

    public boolean enableJump() {
        return true;
    }

}
