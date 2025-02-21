package cn.org.expect.sort;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import cn.org.expect.concurrent.EasyJobService;
import cn.org.expect.concurrent.internal.DefaultJobReader;
import cn.org.expect.expression.Analysis;
import cn.org.expect.expression.BaseAnalysis;
import cn.org.expect.io.BufferedSyncWriter;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableFileCounter;
import cn.org.expect.io.TextTableFileReader;
import cn.org.expect.io.TextTableFileWriter;
import cn.org.expect.io.TextTableLine;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.Terminates;
import cn.org.expect.util.Terminator;
import cn.org.expect.util.TimeWatch;

/**
 * 表格文件排序 <br>
 * 如果有文件中有重复数据（关键字段重复），就抛出异常
 *
 * @author jeremy8551@gmail.com
 * @createtime 2020-01-26
 */
public class TableFileSorter extends Terminator {
    private final static Log log = LogFactory.getLog(TableFileSorter.class);

    /** 上下文信息 */
    private final TableFileSortContext context;

    /** 观察者, 在终止剥离增量时，通知所有观察者执行终止操作 */
    protected Terminates observers;

    /** 表格型记录排序规则 */
    private TempFileRecordComparator comparator;

    /**
     * 初始化
     *
     * @param context 上下文信息
     */
    public TableFileSorter(TableFileSortContext context) {
        super();
        this.context = Ensure.notNull(context);
        this.observers = new Terminates();
        this.terminate = false;
    }

    /**
     * 返回排序配置信息
     *
     * @return 上下文信息
     */
    public TableFileSortContext getContext() {
        return context;
    }

    public void terminate() throws Exception {
        super.terminate();
        this.observers.terminate();
    }

    /**
     * 判断是否已被中断了
     *
     * @throws SortTableFileException 已被中断
     */
    private void assertNotTerminate() throws SortTableFileException {
        if (this.terminate) { // 已被终止
            throw new SortTableFileException("increment.stdout.message027", this.context.getName());
        }
    }

    /**
     * 对文件中的行按设置字段进行排序
     *
     * @param context 容器上下文信息
     * @param file    数据文件
     * @param orders  排序字段表达式数组
     * @return 排序后的文件
     * @throws Exception 发生错误
     */
    public synchronized File execute(EasyContext context, TextTableFile file, String... orders) throws Exception {
        Analysis analysis = new BaseAnalysis();
        OrderByExpression[] array = new OrderByExpression[orders.length];
        for (int i = 0; i < orders.length; i++) {
            array[i] = new OrderByExpression(context, analysis, orders[i]);
        }
        return this.execute(file, array);
    }

    /**
     * 对文件中的行按设置字段进行排序
     *
     * @param file   数据文件
     * @param orders 排序字段表达式数组
     * @return 排序后的文件
     * @throws Exception 发生错误
     */
    public synchronized File execute(TextTableFile file, OrderByExpression... orders) throws Exception {
        Ensure.notNull(file);
        Ensure.notEmpty(orders);
        FileUtils.assertFile(file.getFile());

        this.context.setName(ResourcesUtils.getMessage("increment.stdout.message001", file.getAbsolutePath()));
        this.context.setFile(file);

        if (log.isDebugEnabled()) {
            log.debug("increment.stdout.message045", this.context.getName(), Arrays.toString(orders));
        }

        TempFileFactory creator = new TempFileFactory(this.context, file.getFile());
        this.context.setAttribute(TableFileSortContext.TEMP_FILE_FACTORY, creator); // 临时文件工厂

        // 设置排序规则
        this.comparator = new TempFileRecordComparator(orders.length);
        for (OrderByExpression expr : orders) {
            this.comparator.add(expr.getPosition(), expr.getComparator(), expr.isAsc());
        }

        try {
            return this.execute(file, creator);
        } finally {
            if (this.context.isDeleteFile()) {
                creator.deleteTempFiles(); // 删除临时文件
            }
        }
    }

    /**
     * 排序文件
     *
     * @param file    文件
     * @param creator 临时文件工厂
     * @return 排序后文件
     * @throws Exception 发生错误
     */
    protected File execute(TextTableFile file, TempFileFactory creator) throws Exception {
        TimeWatch watch = new TimeWatch();

        // 排序前文件信息
        File oldfile = file.getFile();
        long oldlength = oldfile.length();
        if (oldfile.exists() && oldlength == 0) {
            return oldfile;
        }

        this.assertNotTerminate();
        File listfile = this.divide(file); // 将大文件分成多个小文件，并返回清单文件
        this.assertNotTerminate();

        File mergefile = this.merge(listfile, file.getCharsetName()); // 合并清单文件中记录的临时文件
        this.assertNotTerminate();

        // 是否自动删除右端的行号字段
        if (this.context.isRemoveLastField()) {
            File removefile = this.removeLineNumber(file, mergefile);
            FileUtils.deleteFile(mergefile);
            mergefile = removefile;
        }

        // 判断移动前后文件大小是否有变化
        long mergeFileLength = mergefile.length() - (this.context.isRemoveLastField() ? 0L : (Long) this.context.getAttribute(TableFileSortContext.FILE_SIZE_MORE));
        if (!mergefile.exists() || mergeFileLength != oldlength) {
            throw new SortTableFileException("increment.stdout.message028", this.context.getName(), mergefile.getAbsolutePath(), mergeFileLength, oldfile.getAbsolutePath(), oldlength);
        }

        // 判断合并前与合并后文件记录数是否相等
        if (this.context.getReadLineNumber() != this.context.getMergeLineNumber()) {
            throw new SortTableFileException("increment.stdout.message041", this.context.getName(), this.context.getReadLineNumber(), this.context.getMergeLineNumber());
        }

        // 保留源文件时，直接返回排序后的文件
        if (this.context.keepSource()) {
            File newfile = creator.toSortfile();

            if (log.isDebugEnabled()) {
                log.debug("increment.stdout.message043", this.context.getName(), newfile.getAbsolutePath(), watch.useTime());
            }

            if (mergefile.renameTo(newfile)) {
                return newfile;
            } else {
                throw new SortTableFileException("increment.stdout.message040", this.context.getName(), mergefile.getAbsolutePath(), newfile.getAbsolutePath());
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("increment.stdout.message044", this.context.getName(), mergefile.getAbsolutePath(), oldfile.getAbsolutePath(), watch.useTime());
            }

            // 不保留源文件的处理逻辑:
            // 1.将原文件（oldfile）重命名为一个带 bak 扩展名的文件
            // 2.将排序后文件（mergefile）重命名为原文件名（oldfile）
            // 3.再删除带 bak 扩展名的文件
            File bakfile = creator.toBakfile();
            if (FileUtils.rename(mergefile, oldfile, bakfile) && bakfile.delete()) { // 删除备份文件
                return oldfile;
            } else {
                throw new SortTableFileException("increment.stdout.message029", this.context.getName(), bakfile.getAbsolutePath());
            }
        }
    }

    /**
     * 删除表格文件的最右侧的一列
     *
     * @param file     表格文件
     * @param sortfile 排序后文件
     * @return 删除列后的文件
     * @throws IOException 访问文件错误
     */
    private File removeLineNumber(TextTableFile file, File sortfile) throws IOException {
        File mergefile = FileUtils.createNewFile(sortfile.getParentFile(), sortfile.getName());
        TextTableFile clone = file.clone();
        clone.setAbsolutePath(mergefile.getAbsolutePath());

        TextTableFile clonefile = file.clone();
        clonefile.setAbsolutePath(sortfile.getAbsolutePath());
        TextTableFileReader in = clonefile.getReader(this.context.getReaderBuffer());
        try {
            TextTableFileWriter out = clone.getWriter(false, this.context.getWriterBuffer());
            try {
                TextTableLine line;
                while ((line = in.readLine()) != null) {
                    String content = line.getContent();
                    String delimiter = clonefile.getDelimiter();
                    int end = Ensure.fromZero(content.lastIndexOf(delimiter));
                    String str = content.substring(0, end);
                    out.addLine(str); // 截取字符串，删除最后一个字段（行号）
                }
                out.flush();
                return mergefile;
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    /**
     * 将大文件中内容分批写入小文件中，同时对小文件内容进行排序, 将小文件绝对路径写入清单文件
     *
     * @param file 待排序的表格文件
     * @return 临时文件的清单文件(清单文件内容是所有临时文件的绝对路径)
     * @throws IOException 划分文件发生错误
     */
    private File divide(TextTableFile file) throws IOException, SortTableFileException {
        TextTableFileReader in = file.getReader(this.context.getReaderBuffer());
        try {
            TextTableLine line = in.readLine();
            String lineSeparator = Settings.getLineSeparator();
            if (line != null && line.getLineSeparator() != null && line.getLineSeparator().length() > 0) {
                lineSeparator = line.getLineSeparator();
            }

            TempFileFactory factory = this.context.getAttribute(TableFileSortContext.TEMP_FILE_FACTORY);
            File listfile = factory.createListFile(); // 清单文件：记录小文件的绝对路径

            if (log.isDebugEnabled()) {
                log.debug("increment.stdout.message032", this.context.getName(), file.getAbsolutePath(), factory.getParent(), this.context.getMaxRows());
            }

            TempFileWriter out = new TempFileWriter(this.context, file, factory, listfile, lineSeparator, this.comparator);
            try {
                while (line != null) {
                    if (this.terminate) {
                        break;
                    } else {
                        out.write(new TempFileRecord(line, line.getLineNumber()));
                        line = in.readLine();
                    }
                }
                out.flush();

                if (log.isDebugEnabled()) {
                    log.debug("increment.stdout.message033", this.context.getName(), file.getAbsolutePath(), FileUtils.count(listfile, file.getCharsetName()));
                }

                return listfile;
            } finally {
                out.close();
            }
        } finally {
            in.close();
            this.calc(file, in.getLineNumber());
        }
    }

    /**
     * 统计每行右侧增加的新列，所占用的字节数总数
     *
     * @param file       文件
     * @param lineNumber 表格型文件总行数
     * @throws UnsupportedEncodingException 计算总字节数发生错误
     */
    private void calc(TextTableFile file, long lineNumber) throws UnsupportedEncodingException {
        long bytes = 0; // 在每行右侧添加的列内容占用的字节总数
        String charsetName = file.getCharsetName();
        String delimiter = file.getDelimiter();
        StringBuilder buf = new StringBuilder(1000);
        for (long i = 1, cout = 0; i <= lineNumber; i++) {
            buf.append(delimiter).append(i);
            if (++cout >= 100) {
                byte[] array = buf.toString().getBytes(charsetName);
                bytes += array.length;
                buf.setLength(0);
                cout = 0;
            }
        }
        byte[] array = buf.toString().getBytes(charsetName);
        bytes += array.length;

        this.context.setAttribute(TableFileSortContext.FILE_SIZE_MORE, bytes);
        this.context.setReadLineNumber(lineNumber);
        file.setColumn(file.getColumn() + 1); // 因为每行右侧都增加了一个新列：行号，所以需要将列数加一
    }

    /**
     * 合并清单文件中的临时文件
     *
     * @param listfile    清单文件(存储临时文件的绝对路径)
     * @param charsetName 清单文件的字符集
     * @return 合并后的文件
     * @throws IOException 合并临时文件发生错误
     */
    private File merge(File listfile, String charsetName) throws Exception {
        long number = FileUtils.count(listfile, charsetName);
        if (number == 0) {
            throw new SortTableFileException("increment.stdout.message030", this.context.getName(), listfile);
        }

        // 清单文件中只有一个文件路径时
        if (number == 1) {
            String filepath = StringUtils.trimBlank(FileUtils.readline(listfile, charsetName, 0));
            assert filepath != null;
            File newfile = new File(filepath);
            FileUtils.assertFile(newfile);
            this.deleteListFile(listfile);
            Long mergeLines = this.context.getAttribute(TableFileSortContext.FILE_LINE_NUMBER); // 最后一次合并的文件记录数作为最终值
            if (mergeLines == null) {
                this.context.setMergeLineNumber(new TextTableFileCounter(this.context.getThreadSource(), this.context.getThreadNumber()).execute(newfile, charsetName));
            } else {
                this.context.setMergeLineNumber(mergeLines);
            }
            return newfile;
        }

        // 合并清单文件中记录的文件
        else {
            TempFileFactory creator = this.context.getAttribute(TableFileSortContext.TEMP_FILE_FACTORY);
            File newlistfile = creator.createListFile(); // 合并后的清单文件
            BufferedSyncWriter out = new BufferedSyncWriter(newlistfile, charsetName, 1);
            try {
                this.merge(listfile, out);
                out.flush();
            } finally {
                out.close();
                this.deleteListFile(listfile); // 合并完清单文件后，需要删除清单文件
            }

            return this.merge(newlistfile, charsetName); // 递归调用，继续合并新清单文件中的临时文件
        }
    }

    /**
     * 合并清单文件 {@code listfile} 中的临时文件, 并将合并后结果（也就是新产生的临时文件路径）写入到 {@code listfileout} 输出流中
     *
     * @param listfile 清单文件
     * @param out      清单文件输出流
     * @throws IOException 访问文件错误
     */
    protected void merge(File listfile, BufferedSyncWriter out) throws Exception {
        TempFileMergeJobReader in = new TempFileMergeJobReader(this.context, listfile, out, this.comparator);
        try {
            if (this.context.getThreadNumber() <= 1) { // 串行执行
                while (in.hasNext() && !this.terminate) {
                    TempFileMergeJob job = in.next();
                    try {
                        this.observers.add(job);
                        job.execute();
                    } finally {
                        this.observers.remove(job);
                    }
                }
            } else { // 使用线程池并行执行
                EasyJobService service = this.context.getThreadSource().getJobService(this.context.getThreadNumber());
                try {
                    this.observers.add(service);
                    service.execute(new DefaultJobReader(in));
                } finally {
                    this.observers.remove(service);
                }
            }
        } finally {
            in.close();
        }
    }

    /**
     * 删除清单文件
     *
     * @param listfile 清单文件
     */
    private void deleteListFile(File listfile) {
        FileUtils.delete(listfile, 5, 200);
    }
}
