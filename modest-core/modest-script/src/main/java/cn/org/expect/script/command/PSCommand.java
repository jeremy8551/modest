package cn.org.expect.script.command;

import java.io.File;
import java.util.Iterator;

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
import cn.org.expect.script.session.ScriptSubProcess;
import cn.org.expect.util.CharTable;
import cn.org.expect.util.Dates;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 打印后台命令
 *
 * @author jeremy8551@qq.com
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
        String[] titles = StringUtils.split(ResourcesUtils.getMessage("script.message.stdout048"), ',');
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
        for (Iterator<UniversalScriptCommand> it = mainProcess.iterator(); it.hasNext(); ) {
            UniversalScriptCommand obj = it.next();
            table.addCell("0");
            table.addCell(session.getCompiler().getLineNumber());
            table.addCell(session.isAlive());
            table.addCell(session.isTerminate());
            table.addCell(Dates.format19(session.getCreateTime()));
            table.addCell("");
            table.addCell(mainProcess.getExitcode());
            table.addCell(obj.getScript());
        }

        // 打印子线程
        ScriptSubProcess subProcess = session.getSubProcess();
        for (Iterator<ScriptProcess> it = subProcess.iterator(); it.hasNext(); ) {
            ScriptProcess obj = it.next();
            table.addCell(obj.getPid());
            table.addCell(obj.getLineNumber());
            table.addCell(obj.isAlive());
            table.addCell(obj.isTerminate());
            table.addCell(Dates.format19(obj.getStartTime()));
            table.addCell(Dates.format19(obj.getEndTime()));
            table.addCell(obj.getExitcode());
            table.addCell(obj.getCommand().getScript());
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
        String[] titles = StringUtils.split(ResourcesUtils.getMessage("script.message.stdout049"), ',');
        CharTable table = new CharTable();
        table.addTitle(titles[0]);
        table.addTitle(titles[1]);
        table.addTitle(titles[2]);
        table.addTitle(titles[3]);
        table.addTitle(titles[4]);
        table.addTitle(titles[5]);
        table.addTitle("");

        UniversalScriptSessionFactory sessionFactory = session.getSessionFactory();
        for (Iterator<String> it = sessionFactory.getSessionIDs().iterator(); it.hasNext(); ) {
            String id = it.next();
            UniversalScriptSession obj = sessionFactory.get(id);
            boolean self = id.equals(session.getId());
            table.addCell(obj.getId());
            table.addCell(obj.getParentID());
            table.addCell(obj.isAlive());
            table.addCell(obj.isTerminate());
            table.addCell(Dates.format19(obj.getCreateTime()));
            table.addCell(Dates.format19(obj.getEndTime()));
            table.addCell(self ? "*" : "");
        }

        return table;
    }

    public void terminate() throws Exception {
    }

    public boolean enableNohup() {
        return true;
    }

}
