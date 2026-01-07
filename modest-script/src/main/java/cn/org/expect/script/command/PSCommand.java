package cn.org.expect.script.command;

import java.io.File;
import java.util.List;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptSessionFactory;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.session.ScriptMainProcess;
import cn.org.expect.script.session.ScriptProcess;
import cn.org.expect.util.CharTable;
import cn.org.expect.util.Dates;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 打印后台命令
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-06-05
 */
public class PSCommand extends AbstractTraceCommand implements NohupCommandSupported {

    /** 0-表示显示后台进程 1-表示显示用户会话 */
    private final int type;

    public PSCommand(UniversalCommandCompiler compiler, String command, int type) {
        super(compiler, command);
        this.type = type;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        String log;
        if (this.type == 1) {
            log = this.printAllSession(session).toString(CharTable.Style.SHELL);
        } else {
            log = this.printAllProcess(session).toString(CharTable.Style.SHELL);
        }

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(log);
        }
        return 0;
    }

    /**
     * 打印所有后台任务
     *
     * @param session 用户会话
     * @return 字符表格
     */
    public CharTable printAllProcess(UniversalScriptSession session) {
        String[] titles = ResourcesUtils.getMessageArray("script.stdout.message043");
        CharTable table = new CharTable();
        table.addTitle(titles[0]);
        table.addTitle(titles[1], CharTable.ALIGN_RIGHT);
        table.addTitle(titles[2]);
        table.addTitle(titles[3]);
        table.addTitle(titles[4]);
        table.addTitle(titles[5]);
        table.addTitle(titles[6], CharTable.ALIGN_RIGHT);
        table.addTitle(titles[7]);

        // 打印主线程
        ScriptMainProcess mainProcess = session.getMainProcess();
        List<UniversalScriptCommand> commandList = mainProcess.getExecutingCommandList();
        for (int i = 0; i < commandList.size(); i++) {
            UniversalScriptCommand scriptCommand = commandList.get(i);
            if (scriptCommand == null) {
                continue;
            }

            table.addCell("0");
            table.addCell(session.getCompiler().getLineNumber());
            table.addCell(session.isAlive());
            table.addCell(session.isTerminate());
            table.addCell(Dates.format19(session.getCreateTime()));
            table.addCell("");
            table.addCell(mainProcess.getExitcode());
            table.addCell(scriptCommand.getScript());
        }

        // 打印子线程
        List<ScriptProcess> threadList = session.getSubProcess().getThreads();
        for (int i = 0; i < threadList.size(); i++) {
            ScriptProcess process = threadList.get(i);
            if (process == null) {
                continue;
            }

            table.addCell(process.getPid());
            table.addCell(process.getLineNumber());
            table.addCell(process.isAlive());
            table.addCell(process.isTerminate());
            table.addCell(Dates.format19(process.getStartTime()));
            table.addCell(Dates.format19(process.getEndTime()));
            table.addCell(process.getExitcode());
            table.addCell(process.getCommand().getScript());
        }

        return table;
    }

    /**
     * ps -e 打印所有会话信息
     *
     * @param session 会话信息
     * @return 字符表格
     */
    public CharTable printAllSession(UniversalScriptSession session) {
        String[] titles = ResourcesUtils.getMessageArray("script.stdout.message044");
        CharTable table = new CharTable();
        table.addTitle(titles[0]);
        table.addTitle(titles[1]);
        table.addTitle(titles[2]);
        table.addTitle(titles[3]);
        table.addTitle(titles[4]);
        table.addTitle(titles[5]);
        table.addTitle("");

        UniversalScriptSessionFactory factory = session.getSessionFactory();
        List<String> list = factory.getSessionidList();
        for (int i = 0; i < list.size(); i++) {
            String id = list.get(i);
            UniversalScriptSession scriptSession = factory.get(id);
            if (scriptSession == null) {
                continue;
            }

            boolean self = id.equals(session.getId());
            table.addCell(scriptSession.getId());
            table.addCell(scriptSession.getParentID());
            table.addCell(scriptSession.isAlive());
            table.addCell(scriptSession.isTerminate());
            table.addCell(Dates.format19(scriptSession.getCreateTime()));
            table.addCell(Dates.format19(scriptSession.getEndTime()));
            table.addCell(self ? "*" : "");
        }

        return table;
    }

    public boolean enableNohup() {
        return true;
    }
}
