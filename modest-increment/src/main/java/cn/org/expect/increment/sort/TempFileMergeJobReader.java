package cn.org.expect.increment.sort;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.concurrent.EasyJobReader;
import cn.org.expect.io.BufferedSyncWriter;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.Terminator;

/**
 * 合并文件任务信息输入类
 */
public class TempFileMergeJobReader extends Terminator implements EasyJobReader {

    /** 排序上下文信息 */
    private final TableFileSortContext context;

    /** 合并任务 */
    private TempFileMergeJob job;

    /** 清单文件的输入流 */
    private BufferedReader in;

    /** 新清单文件的IO流 */
    private final BufferedSyncWriter listFileWriter;

    /** 记录排序规则 */
    private final TempFileRecordComparator comparator;

    /** 清单文件 */
    private final File listFile;

    /**
     * 初始化
     *
     * @param context    排序上下文信息
     * @param listFile   清单文件
     * @param out        清单文件输出流（用于存储合并后产生的临时文件绝对路径）
     * @param comparator 记录排序规则
     * @throws IOException 访问文件错误
     */
    public TempFileMergeJobReader(TableFileSortContext context, File listFile, BufferedSyncWriter out, TempFileRecordComparator comparator) throws IOException {
        this.context = Ensure.notNull(context);
        this.listFile = FileUtils.assertExists(listFile);
        this.in = IO.getBufferedReader(listFile, this.context.getFile().getCharsetName());
        this.listFileWriter = Ensure.notNull(out);
        this.comparator = Ensure.notNull(comparator);
    }

    public synchronized boolean hasNext() throws IOException {
        TextTableFile template = this.context.getFile();
        List<TextTableFile> list = new ArrayList<TextTableFile>();

        // 合并一定数量的文件
        String filepath;
        for (int i = 1; i <= this.context.getFileCount() && (filepath = this.in.readLine()) != null; i++) {
            if (StringUtils.isNotBlank(filepath)) {
                TextTableFile file = template.clone();
                file.setAbsolutePath(StringUtils.trimBlank(filepath));
                list.add(file);
            }
        }

        if (list.isEmpty()) {
            return false;
        } else {
            this.job = new TempFileMergeJob(this.context, this, this.listFileWriter, this.comparator, list);
            return true;
        }
    }

    public synchronized TempFileMergeJob next() {
        return this.job;
    }

    public synchronized void close() throws IOException {
        IO.close(this.in);
        this.in = null;
        this.job = null;
    }

    /**
     * 添加合并行数
     *
     * @param lines 任务合并的行数
     */
    public void setLineNumbers(long lines) {
        this.context.setAttribute(TableFileSortContext.FILE_LINE_NUMBER, lines);
    }
}
