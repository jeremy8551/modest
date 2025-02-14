package cn.org.expect.script.command;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.expression.LoginExpression;
import cn.org.expect.os.OSSecureShellCommand;
import cn.org.expect.printer.OutputStreamPrinter;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptChecker;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.SSHTunnelMap;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 建立SSH端口转发协议 <br>
 * <br>
 * declare {name} SSH tunnel use proxy {proxyUsername}@{proxyHost}:{proxySSHPort}?password={proxyPassword} connect to {remoteHost}:{remoteSSHPort}; <br>
 */
public class DeclareSSHTunnelCommand extends AbstractCommand {

    /** 端口转发协议名 */
    private String name;

    /** 代理服务器配置: username@host:port?password=password */
    private String proxy;

    /** 目标服务器配置 remote:port */
    private String remote;

    public DeclareSSHTunnelCommand(UniversalCommandCompiler compiler, String command, String name, String proxy, String remote) {
        super(compiler, command);
        this.name = name;
        this.proxy = proxy;
        this.remote = remote;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();

        // 解析代理服务器配置
        LoginExpression expr = new LoginExpression(analysis, "ssh " + this.proxy);
        String host = expr.getLoginHost();
        int port = Integer.parseInt(expr.getLoginPort());
        String username = expr.getLoginUsername();
        String password = expr.getLoginPassword();
        List<String> portForward = new ArrayList<String>();
        analysis.split(analysis.replaceShellVariable(session, context, this.remote, true, false), portForward, ':');

        // 连接代理服务器
        OSSecureShellCommand client = context.getContainer().getBean(OSSecureShellCommand.class);
        if (!client.connect(host, port, username, password)) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message034", this.command, "ssh2"));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        client.setStdout(new OutputStreamPrinter(stdout, client.getCharsetName()));
        client.setStderr(new OutputStreamPrinter(stderr, client.getCharsetName()));

        // 解析本地端口号
        String localStr = portForward.get(0);
        boolean variableName = false;
        int inputPort;
        UniversalScriptChecker checker = context.getEngine().getChecker();
        if (checker.isVariableName(localStr)) { // 本地端口位置是变量名
            inputPort = 0; // 随机分配
            variableName = true;
        } else if (StringUtils.isNumber(localStr) && //
            ((inputPort = Integer.parseInt(localStr)) == 0 || (inputPort >= 1024 && inputPort <= 65535)) //
        ) { // 端口为0 或 1024-65535
            inputPort = Integer.parseInt(localStr);
        } else {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message060", this.command, localStr));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        // 建立本地端口转发隧道
        int localPort = client.localPortForward(inputPort, portForward.get(1), Integer.parseInt(portForward.get(2)));
        if (variableName) {
            context.addLocalVariable(localStr, localPort);
        }

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(localPort);
        }

        SSHTunnelMap.get(context).add(this.name, client);
        return 0;
    }
}
