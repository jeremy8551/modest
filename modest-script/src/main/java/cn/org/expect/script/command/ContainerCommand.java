package cn.org.expect.script.command;

import java.util.List;
import java.util.Map;

import cn.org.expect.concurrent.EasyJobService;
import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.WithBodyCommandSupported;
import cn.org.expect.script.internal.ContainerCommandReader;
import cn.org.expect.util.StringUtils;

/**
 * container to execute tasks in parallel using thread=2 dropIndex buildIndex freq=day batch=10000 rollback begin ... end
 *
 * @author jeremy8551@gmail.com
 */
public class ContainerCommand extends AbstractCommand implements WithBodyCommandSupported {

    /** 参数集合 */
    private Map<String, String> attributes;

    /** 代码块 */
    private List<UniversalScriptCommand> cmdlist;

    /** 运行容器 */
    private EasyJobService service;

    public ContainerCommand(UniversalCommandCompiler compiler, String command, Map<String, String> attributes, List<UniversalScriptCommand> cmdlist) {
        super(compiler, command);
        this.attributes = attributes;
        this.cmdlist = cmdlist;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String thread = analysis.unQuotation(analysis.replaceShellVariable(session, context, this.attributes.get("thread"), true, true));
        int number = StringUtils.parseInt(thread, 2);

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(analysis.replaceShellVariable(session, context, this.command, false, true));
        }

        ContainerCommandReader in = new ContainerCommandReader(session, context, stdout, stderr, this.cmdlist);
        try {
            this.service = context.getContainer().getBean(ThreadSource.class).getJobService(number);
            this.service.execute(in);
            return 0;
        } finally {
            in.close();
        }
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.service != null) {
            this.service.terminate();
        }
    }
}
