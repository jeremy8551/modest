package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import cn.org.expect.concurrent.EasyJob;
import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.increment.IncrementContext;
import cn.org.expect.increment.IncrementJob;
import cn.org.expect.increment.IncrementListenerImpl;
import cn.org.expect.increment.IncrementPosition;
import cn.org.expect.increment.IncrementPositionImpl;
import cn.org.expect.increment.IncrementReplace;
import cn.org.expect.increment.IncrementReplaceListener;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableFileWriter;
import cn.org.expect.printer.StandardFilePrinter;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptJob;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.internal.ProgressMap;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Numbers;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 剥离增量数据命令 <br>
 * <br>
 * <p>
 * extract increment compare
 * newfilepath of del [modified by sort= index= compare= catalog= table= fresh= update=false delete=] <br>
 * and <br>
 * oldfilepath of del [modified by sort= index= compare= catalog= table= fresh= update=false delete=] <br>
 * write into <br>
 * {@literal filepath [ of del modified by newchg=filedName->value,2->value,3->$3 updchg= delchg= ] } <br>
 * write new into <br>
 * {@literal incfilepath [ of del modified by newchg=filedName->value,2->value,3->$3 ] } <br>
 * write upd into <br>
 * {@literal incfilepath [ of del modified by updchg=filedName->value,2->value,3->$3 ] }<br>
 * write del into <br>
 * {@literal incfilepath [ of del modified by delchg=filedName->value,2->value,3->$3 ] } <br>
 * write log into <br>
 * filepath [ of del modified by ] <br>
 * <br>
 * <br>
 * 触发器表达式规则: 字段名:$字段名 字段位置:$字段名 字段名:$字段位置 <br>
 * 其中 date-yyyyMMdd 表示当前日期或时间 <br>
 * 其中 uuid 表示唯一标志字符串 <br>
 * <br>
 * <br>
 *
 * @author jeremy8551@qq.com
 * @createtime 2021-05-14
 */
public class IncrementCommand extends AbstractTraceCommand implements UniversalScriptJob, NohupCommandSupported {

    /** 新文件输出流表达式 */
    private IncrementExpression newfileExpr;

    /** 旧文件输出流表达式 */
    private IncrementExpression oldfileExpr;

    /** write 输出流表达式 */
    private IncrementExpression[] writeExpr;

    /** 任务信息 */
    private IncrementJob job;

    public IncrementCommand(UniversalCommandCompiler compiler, String command, IncrementExpression newfileExpr, IncrementExpression oldfileExpr, IncrementExpression[] writeExpr) {
        super(compiler, command);
        this.newfileExpr = newfileExpr;
        this.oldfileExpr = oldfileExpr;
        this.writeExpr = writeExpr;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (!this.isPrepared(session, context, stdout, stderr)) {
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        if (session.isEchoEnable() || forceStdout) {
            UniversalScriptAnalysis analysis = session.getAnalysis();
            stdout.println(analysis.replaceShellVariable(session, context, this.command, true, false, true, false));
        }

        int value = this.job.execute();
        return this.job.isTerminate() ? UniversalScriptCommand.TERMINATE : (value == 0 ? 0 : UniversalScriptCommand.COMMAND_ERROR);
    }

    public void terminate() throws Exception {
        if (this.job != null) {
            this.job.terminate();
        }
    }

    public boolean isPrepared(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr) throws Exception {
        if (this.job == null) {
            TextTableFile newfile = this.newfileExpr.createTableFile();
            TextTableFile oldfile = this.oldfileExpr.createTableFile();

            int[] newCompars = this.newfileExpr.getComparePosition();
            int[] oldCompars = this.oldfileExpr.getComparePosition();
            if (oldCompars.length == 0 && newCompars.length == 0) {
                int newColumn = newfile.countColumn();
                int oldColumn = oldfile.countColumn();
                int column = Math.min(newColumn, oldColumn);

                // 设置新文件默认比较字段位置信息
                int index = 0;
                int[] newCompare = new int[column - this.newfileExpr.getIndexPosition().length];
                for (int i = 0; i < column; i++) {
                    int p = i + 1;
                    if (!Numbers.inArray(p, this.newfileExpr.getIndexPosition())) {
                        newCompare[index++] = p;
                    }
                }
                Ensure.isTrue(index == newCompare.length, index, newCompare.length, column);
                newCompars = newCompare;

                // 设置旧文件默认比较字段位置信息
                index = 0;
                int[] oldCompare = new int[column - this.oldfileExpr.getIndexPosition().length];
                for (int i = 0; i < column; i++) {
                    int p = i + 1;
                    if (!Numbers.inArray(p, this.oldfileExpr.getIndexPosition())) {
                        oldCompare[index++] = p;
                    }
                }
                Ensure.isTrue(index == oldCompare.length, index, oldCompare.length, column);
                oldCompars = oldCompare;
            }

            IncrementPosition position = new IncrementPositionImpl(this.newfileExpr.getIndexPosition(), this.oldfileExpr.getIndexPosition(), newCompars, oldCompars);
            if (ArrayUtils.isEmpty(this.newfileExpr.getIndexPosition()) || ArrayUtils.isEmpty(this.oldfileExpr.getIndexPosition())) {
                throw new IOException(ResourcesUtils.getMessage("script.message.stderr137", "index"));
            }

            IncrementReplaceListener replaceList = new IncrementReplaceListener();
            List<IncrementReplace> newReplace = replaceList.getNewChgs();
            List<IncrementReplace> updReplace = replaceList.getUpdChgs();
            List<IncrementReplace> delReplace = replaceList.getDelChgs();

            // 日志文件路径
            String logfilepath = null;
            TextTableFileWriter newout = null, updout = null, delout = null;
            for (IncrementExpression expr : this.writeExpr) {
                if (expr.isLogWriter()) { // 日志文件输出流
                    if (logfilepath == null) {
                        logfilepath = expr.getFilepath();
                    } else {
                        throw new IOException(ResourcesUtils.getMessage("script.message.stderr132", this.command));
                    }
                } else {
                    String filetype = expr.getFiletype();
                    TextTableFile table = expr.createTableFile(StringUtils.defaultString(filetype, this.newfileExpr.getFiletype()));
                    TextTableFileWriter out = table.getWriter(expr.contains("append"), expr.contains("outbuf") ? expr.getIntAttribute("outbuf") : 100);
                    Set<String> kinds = expr.getKinds(); // new upd del
                    if (kinds.size() == 0) { // write into 语句
                        if (newout == null && updout == null && delout == null) {
                            newout = out;
                            updout = out;
                            delout = out;
                            newReplace.addAll(expr.getNewChg());
                            updReplace.addAll(expr.getUpdChg());
                            delReplace.addAll(expr.getDelChg());
                        } else {
                            throw new IOException(ResourcesUtils.getMessage("script.message.stderr133", this.command));
                        }
                    } else {
                        for (String kind : kinds) {
                            if (kind.equalsIgnoreCase("new")) { // 新增数据
                                if (newout == null) {
                                    newout = out;
                                    newReplace.addAll(expr.getNewChg());
                                } else {
                                    throw new IOException(ResourcesUtils.getMessage("script.message.stderr134", this.command));
                                }
                            } else if (kind.equalsIgnoreCase("upd")) { // 修改数据
                                if (updout == null) {
                                    updout = out;
                                    updReplace.addAll(expr.getUpdChg());
                                } else {
                                    throw new IOException(ResourcesUtils.getMessage("script.message.stderr135", this.command));
                                }
                            } else if (kind.equalsIgnoreCase("del")) { // 删除数据
                                if (delout == null) {
                                    delout = out;
                                    delReplace.addAll(expr.getDelChg());
                                } else {
                                    throw new IOException(ResourcesUtils.getMessage("script.message.stderr136", this.command));
                                }
                            }
                        }
                    }
                }
            }

            // 剥离增量上下文信息
            IncrementContext inccxt = new IncrementContext();
            inccxt.setName(ResourcesUtils.getMessage("increment.standard.output.msg006", newfile.getAbsolutePath()));
            inccxt.setNewFile(newfile);
            inccxt.setOldFile(oldfile);
            inccxt.setPosition(position);
            inccxt.setNewWriter(newout);
            inccxt.setUpdWriter(updout);
            inccxt.setDelWriter(delout);
            inccxt.setSortNewfile(!this.newfileExpr.contains("nosort"));
            inccxt.setSortOldfile(!this.oldfileExpr.contains("nosort"));
            inccxt.setSortNewContext(this.newfileExpr.createSortContext());
            inccxt.setSortOldContext(this.oldfileExpr.createSortContext());
            inccxt.setReplaceList(replaceList);
            inccxt.setThreadSource(context.getContainer().getBean(ThreadSource.class));
            inccxt.setListeners(null);

            // 设置新文件的读取进度
            if (this.newfileExpr.contains("progress")) {
                String progress = this.newfileExpr.getAttribute("progress");
                inccxt.setNewfileProgress(ProgressMap.getProgress(context, progress));
            }

            // 设置旧文件的读取进度
            if (this.oldfileExpr.contains("progress")) {
                String progress = this.oldfileExpr.getAttribute("progress");
                inccxt.setOldfileProgress(ProgressMap.getProgress(context, progress));
            }

            // 设置日志输出接口
            if (StringUtils.isNotBlank(logfilepath)) {
                if ("stdout".equalsIgnoreCase(logfilepath)) {
                    inccxt.setLogger(new IncrementListenerImpl(stdout, position, newfile, oldfile));
                } else if ("stderr".equalsIgnoreCase(logfilepath)) {
                    inccxt.setLogger(new IncrementListenerImpl(stderr, position, newfile, oldfile));
                } else {
                    String charsetName = StringUtils.defaultString(newfile.getCharsetName(), context.getCharsetName()); // 新文件的字符集编码
                    StandardFilePrinter out = new StandardFilePrinter(new File(logfilepath), charsetName, false);
                    inccxt.setLogger(new IncrementListenerImpl(out, position, newfile, oldfile));
                }
            }

            this.job = new IncrementJob(inccxt);
        }

        IncrementContext cxt = this.job.getContext();
        return FileUtils.isFile(cxt.getNewFile().getFile()) && FileUtils.isFile(cxt.getOldFile().getFile());
    }

    public EasyJob getJob() {
        IncrementJob job = this.job;
        this.job = null;
        return job;
    }

    public boolean enableNohup() {
        return true;
    }

}
