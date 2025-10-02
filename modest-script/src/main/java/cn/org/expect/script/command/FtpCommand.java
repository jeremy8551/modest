package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import cn.org.expect.expression.LoginExpression;
import cn.org.expect.os.OSFtpCommand;
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
import cn.org.expect.script.internal.FtpList;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 建立 ftp 客户端 <br>
 * <br>
 * ftp username@serverHost:port?password=password; <br>
 * supported ftp command: <br>
 * exists filepath <br>
 * isfile filepath <br>
 * isDir filepath <br>
 * mkdir filepath <br>
 * cd filepath <br>
 * rm filepath <br>
 * pwd <br>
 * ls <br>
 * PUT file remotedir <br>
 * get remotefile localdir <br>
 */
public class FtpCommand extends AbstractFileCommand implements UniversalScriptInputStream {

    protected String host;

    protected String port;

    protected String username;

    protected String password;

    public FtpCommand(UniversalCommandCompiler compiler, String command, String host, String port, String username, String password) {
        super(compiler, command);
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (!analysis.isBlank(this.host) || !analysis.isBlank(this.port) || !analysis.isBlank(this.username) || !analysis.isBlank(this.password)) {
            throw new UniversalScriptException("script.stderr.message012", this.command, "ftp", "ftp " + username + "@" + host + ":" + port + "?password=" + password);
        }

        String expression = StringUtils.trimBlank(IO.read(in, new StringBuilder("ftp ")));
        LoginExpression expr = new LoginExpression(analysis, expression);
        this.host = expr.getLoginHost();
        this.port = expr.getLoginPort();
        this.username = expr.getLoginUsername();
        this.password = expr.getLoginPassword();
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            stdout.println("ftp " + this.username + "@" + this.host + ":" + this.port + "?password=" + this.password);
        }

        OSFtpCommand ftp = context.getContainer().getBean(OSFtpCommand.class, "ftp");
        if (!ftp.connect(this.host, Integer.parseInt(this.port), this.username, this.password)) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message034", this.command, "ftp"));
            return UniversalScriptCommand.COMMAND_ERROR;
        } else {
            FtpList.get(context).add(ftp);
            return 0;
        }
    }
}
