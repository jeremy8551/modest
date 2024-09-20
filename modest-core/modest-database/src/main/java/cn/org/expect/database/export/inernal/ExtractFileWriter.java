package cn.org.expect.database.export.inernal;

import java.io.File;
import java.io.IOException;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.database.SQL;
import cn.org.expect.database.export.ExtractMessage;
import cn.org.expect.database.export.ExtractWriter;
import cn.org.expect.database.export.ExtracterContext;
import cn.org.expect.io.TableLine;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableFileWriter;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.EasyContextAware;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

@EasyBean(name = "local", description = "卸载数据到本地文件")
public class ExtractFileWriter implements ExtractWriter, EasyContextAware {

    /** 文件路径 */
    private String filepath;

    /** 文件名的序号 */
    private int fileNumber;

    /** 最大记录数 */
    private long maximum;

    /** 计数器 */
    private long count;

    /** true 表示重新写入 */
    private boolean rewrite;

    /** 文件输出流 */
    private TextTableFileWriter writer;

    /** 上下文信息 */
    private ExtracterContext context;

    /** 写入行数 */
    protected long lineNumber;

    /** 写入总字节数 */
    private long bytes;

    /** 标题输入流 */
    private TableTitle title;

    /** 消息信息 */
    private ExtractMessage message;

    /** 容器上下文信息 */
    protected EasyContext ioc;

    public void setContext(EasyContext context) {
        this.ioc = context;
    }

    /**
     * 初始化
     *
     * @param context 卸数引擎上下文信息
     * @param message 消息信息
     * @throws Exception 卸载数据发生错误
     */
    public ExtractFileWriter(ExtracterContext context, ExtractMessage message) throws Exception {
        this.context = Ensure.notNull(context);
        this.message = Ensure.notNull(message);
        this.filepath = context.getTarget();
        this.maximum = context.getMaximum();
        this.rewrite = this.maximum > 0;
        this.fileNumber = -1;
        this.lineNumber = 0;
        this.count = 0;
        this.open();
    }

    /**
     * 打开文件输出流
     *
     * @throws Exception 卸载数据发生错误
     */
    protected void open() throws Exception {
        this.closeWriter();
        File file = this.createNewfile();

        // 保存文件路径
        String target = this.message.getTarget();
        if (StringUtils.isNotBlank(target)) {
            target += ", ";
        }
        target += file.getAbsolutePath();
        this.message.setTarget(target);

        // 打开文件输出流
        TextTableFile template = (TextTableFile) this.context.getFormat();
        TextTableFile table = template.clone();
        table.setAbsolutePath(file.getAbsolutePath());
        this.writer = table.getWriter(this.context.isAppend(), this.context.getCacheLines());

        // 写入列标题信息
        if (this.context.isTitle()) {
            if (this.title == null) {
                this.title = new TableTitle(this.context, this.ioc);
            }
            this.write(this.title);
        }
    }

    /**
     * 创建文件
     *
     * @return 文件
     */
    protected File createNewfile() {
        this.fileNumber++;
        int begin = this.filepath.indexOf('{');
        if (begin != -1) {
            int end = SQL.indexOfBrace(this.filepath, begin);
            if (end == -1) {
                throw new IllegalArgumentException(this.filepath);
            }

            int size = end - begin + 1;
            String filepath = StringUtils.replace(this.filepath, begin, size, (this.fileNumber == 0 ? "" : String.valueOf(this.fileNumber)));
            return new File(filepath);
        } else {
            File file = new File(this.filepath);
            String suffix = FileUtils.getFilenameSuffix(file.getName());
            String filename = FileUtils.getFilenameNoSuffix(file.getName());
            String filepath = filename + (this.fileNumber == 0 ? "" : "-" + this.fileNumber) + "." + suffix;
            return new File(file.getParentFile(), filepath);
        }
    }

    public void write(TableLine line) throws Exception {
        this.count++;
        this.lineNumber++;
        this.writer.addLine(line);
    }

    public boolean rewrite() throws Exception {
        if (this.rewrite && this.count >= this.maximum) { // 已达最大记录
            this.count = 0;
            this.open();
            return true;
        } else {
            return false;
        }
    }

    public void flush() throws IOException {
        this.writer.flush();
    }

    public void close() throws IOException {
        this.closeWriter();
        this.message.setRows(this.lineNumber);
        this.message.setBytes(this.bytes);
    }

    /**
     * 关闭文件输出流
     *
     * @throws IOException 卸载数据发生错误
     */
    protected void closeWriter() throws IOException {
        if (this.writer != null) {
            this.writer.flush();
            this.bytes += this.writer.getTable().getFile().length();
            this.writer.close();
            this.writer = null;
        }
    }

}
