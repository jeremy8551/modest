package cn.org.expect.script.command;

import cn.org.expect.printer.Printer;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.TimeWatch;

public class JavaCommandTest extends AbstractJavaCommand {

    public JavaCommandTest() {
        super();
    }

    public void echoUsage(Printer out) {
    }

    @Override
    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, String[] args) throws Exception {
        int timeout = StringUtils.parseInt(args[0], 120);

        context.addGlobalVariable("JavaCommandTest", "JavaCommandTest110");
        TimeWatch watch = new TimeWatch();

        System.out.println(StringUtils.toString(args));
        System.out.println("等待 " + timeout + " 秒!");
        while (!this.terminate) {
            if (this.terminate || watch.useSeconds() >= timeout) {
                break;
            }

        }

        this.terminate = false;
        return 0;
    }

    @Override
    public boolean enableJump() {
        return false;
    }

    @Override
    public boolean enableNohup() {
        return true;
    }

    @Override
    public boolean enablePipe() {
        return false;
    }
}