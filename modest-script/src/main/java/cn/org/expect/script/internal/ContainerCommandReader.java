package cn.org.expect.script.internal;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.concurrent.EasyJob;
import cn.org.expect.concurrent.EasyJobReader;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptJob;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Terminator;

/**
 * 容器任务的输入流
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-02-20
 */
public class ContainerCommandReader extends Terminator implements EasyJobReader {

    private final UniversalScriptContext context;
    private final UniversalScriptSession session;
    private final UniversalScriptStdout stdout;
    private final UniversalScriptStderr stderr;
    private EasyJob job;
    private final List<UniversalScriptCommand> list;

    /**
     * 初始化
     *
     * @param session  用户会话信息
     * @param context  脚本引擎上下文信息
     * @param stdout   标准输出流
     * @param stderr   错误输出流
     * @param commands 容器中的命令集合
     */
    public ContainerCommandReader(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, List<UniversalScriptCommand> commands) {
        this.terminate = false;
        this.context = context;
        this.session = session;
        this.stdout = stdout;
        this.stderr = stderr;
        this.list = new ArrayList<UniversalScriptCommand>(commands);
    }

    public synchronized boolean hasNext() throws Exception {
        if (this.job != null) {
            return true;
        }

        while (true) {
            if (this.terminate) {
                return false;
            }

            UniversalScriptAnalysis analysis = this.session.getAnalysis();
            for (int i = 0; !this.terminate && i < this.list.size(); i++) {
                UniversalScriptCommand command = this.list.remove(i);
                UniversalScriptJob job = (UniversalScriptJob) command;

                // 判断命令是否已准备好执行
                if (job.isPrepared(this.session, this.context, this.stdout, this.stderr)) {
                    this.job = job.getJob();
                    this.stdout.println(analysis.unQuotation(analysis.replaceShellVariable(this.session, this.context, command.getScript(), false, true)));
                    return true;
                }
            }

            if (this.terminate || this.list.isEmpty()) {
                return false; // 已全部执行完毕
            } else {
                Dates.sleep(2000); // 还有未准备就绪的任务, 等待2秒后再查询是否有准备就绪任务
            }
        }
    }

    public synchronized EasyJob next() throws Exception {
        EasyJob value = this.job;
        if (value == null) {
            if (this.hasNext()) {
                value = this.job;
            }
        }
        this.job = null;
        return value;
    }

    public void close() {
        this.list.clear();
        this.terminate = false;
    }
}
