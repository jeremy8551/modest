package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.session.ScriptProcess;
import cn.org.expect.script.session.ScriptProcessEnvironment;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.TimeWatch;

/**
 * 后台运行脚本命令 <br>
 * {@literal nohup command &}
 */
public class NohupCommand extends AbstractCommand {

    /** 后台运行的命令 */
    private final UniversalScriptCommand subcommand;

    /** 后台线程的运行环境 */
    private ScriptProcessEnvironment environment;

    public NohupCommand(UniversalCommandCompiler compiler, String command, UniversalScriptCommand subcommand) {
        super(compiler, command);
        this.subcommand = subcommand;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        File logfile = new File(session.getDirectory(), "nohup.out");
        if (!logfile.canWrite()) {
            logfile = new File(Settings.getUserHome(), logfile.getName());
        }

        // 创建子线程
        ScriptProcessEnvironment environment = new ScriptProcessEnvironment(session, context, stdout, stderr, forceStdout, this.subcommand, logfile);
        ScriptProcess process = session.getSubProcess().create(environment);
        process.start();
        this.environment = environment;
        boolean print = session.isEchoEnable() || forceStdout;

        // 等待后台线程启动，防止启动超时
        int timeout = 2 * 60 * 1000; // 超时时间：2分钟
        TimeWatch watch = new TimeWatch();
        boolean hasPrint = false;
        while (!this.terminate && process.waitFor()) { // 等待线程启动运行后退出
            environment.getWaitRun().sleep(timeout);
            if (this.terminate) {
                break;
            }

            if (!hasPrint) {
                if (print) {
                    stdout.println(ResourcesUtils.getMessage("script.stdout.message028", this.subcommand));
                }
                hasPrint = true;
            }

            // 判断是否大于超时时间
            if (watch.useMillis() > timeout) {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message022", this.command, this.subcommand.getScript()));
                return UniversalScriptCommand.COMMAND_ERROR;
            }
        }

        if (this.terminate) {
            return UniversalScriptCommand.TERMINATE;
        } else {
            if (print) {
                stdout.print("appending output to " + logfile.getAbsolutePath() + Settings.getLineSeparator() + process.getPid());
            }

            session.setValue(process.getPid());
            return 0;
        }
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.environment != null) {
            this.environment.getWaitRun().wakeup();
        }
    }
}
