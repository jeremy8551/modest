package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.sort.OrderByExpression;
import cn.org.expect.sort.TableFileSortContext;
import cn.org.expect.sort.TableFileSorter;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

/**
 * 对表格型文件进行排序 <br>
 * <p>
 * sort table file ~/file/name.txt of del [modified by filecount=2 keeptmp readbuf=8192] order by int(1) desc,2,3 {asc | desc}
 *
 * @author jeremy8551@qq.com
 */
public class SortTableFileCommand extends AbstractTraceCommand {

    private TableFileSorter tfs;
    private String filepath;
    private String filetype;
    private OrderByExpression[] orders;
    private CommandAttribute map;

    public SortTableFileCommand(UniversalCommandCompiler compiler, String script, String filepath, String filetype, OrderByExpression[] orders, CommandAttribute map) {
        super(compiler, script);
        this.filepath = filepath;
        this.filetype = filetype;
        this.orders = orders;
        this.map = map;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            stdout.println(session.getAnalysis().replaceShellVariable(session, context, this.command, true, true, true, false));
        }

        TableFileSortContext cxt = new TableFileSortContext();
        cxt.setThreadSource(context.getContainer().getBean(ThreadSource.class));
        cxt.setDeleteFile(!this.map.contains("keeptemp"));
        cxt.setKeepSource(!this.map.contains("covsrc"));

        if (this.map.contains("maxfile")) {
            cxt.setFileCount(this.map.getIntAttribute("maxfile"));
        }

        if (this.map.contains("maxrow")) {
            cxt.setMaxRows(this.map.getIntAttribute("maxrow"));
        }

        if (this.map.contains("readbuf")) {
            cxt.setReaderBuffer(this.map.getIntAttribute("readbuf"));
        } else {
            cxt.setReaderBuffer(IO.FILE_BYTES_BUFFER_SIZE);
        }

        if (this.map.contains("thread")) {
            cxt.setThreadNumber(this.map.getIntAttribute("thread"));
        }

        if (this.map.contains("writebuf")) {
            cxt.setWriterBuffer(this.map.getIntAttribute("writebuf"));
        }

        String tempFilepath = this.map.getAttribute("temp");
        if (StringUtils.isNotBlank(tempFilepath)) {
            String filepath = session.getAnalysis().replaceShellVariable(session, context, tempFilepath, true, true, true, false);
            cxt.setTempDir(new File(filepath));
        }

        TextTableFile file = context.getContainer().getBean(TextTableFile.class, this.filetype, this.map);
        Ensure.notNull(file);
        file.setAbsolutePath(this.filepath);

        this.tfs = new TableFileSorter(cxt);
        this.tfs.sort(file, this.orders);
        return 0;
    }

    public void terminate() throws Exception {
        if (this.tfs != null) {
            this.tfs.terminate();
        }
    }

}
