package cn.org.expect.script.command;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.TimeWatch;

public class JavaCommandTest extends AbstractJavaCommand {
    private final static Log log = LogFactory.getLog(JavaCommandTest.class);

    public JavaCommandTest() {
        super();
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, String[] args) throws Exception {
        log.info(JavaCommandTest.class.getName() + ", args: " + StringUtils.toString(args));
        int timeout = StringUtils.parseInt(args[0], 3600);
        log.info(JavaCommandTest.class.getName() + " wait " + timeout + " second!");

        context.addGlobalVariable("JavaCommandTest", "JavaCommandTest110");
        TimeWatch watch = new TimeWatch();

        while (!this.terminate) {
            if (this.terminate || watch.useSeconds() >= timeout) {
                break;
            }
        }

        this.terminate = false;
        return 0;
    }

    public boolean enableJump() {
        return false;
    }

    public boolean enableNohup() {
        return true;
    }

    public boolean enablePipe() {
        return false;
    }
}
