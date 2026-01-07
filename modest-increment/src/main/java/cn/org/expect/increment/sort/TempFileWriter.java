package cn.org.expect.increment.sort;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import cn.org.expect.io.BufferedSyncWriter;
import cn.org.expect.io.BufferedWriter;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableLine;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.IO;

/**
 * 临时文件输出流：将一个大文件切分成若干小文件
 */
public class TempFileWriter {

    /** 排序上下文信息 */
    private final TableFileSortContext context;

    /** 缓冲区 */
    private TextTableLine[] buffer;

    /** 缓冲区实际容量 */
    private int size;

    /** 缓冲区数组的容量 */
    private final int capacity;

    /** 清单文件输出流 */
    private final BufferedWriter listFileWriter;

    /** 表格型记录排序规则 */
    private final TempFileRecordComparator comparator;

    /** 表格型文件中的字段分隔符 */
    private final String delimiter;

    /** 写文件时的缓存行数 */
    private final int writeBuffer;

    /** 临时文件工厂 */
    private final TempFileFactory factory;

    /** 换行符 */
    private final String lineSeparator;

    public TempFileWriter(TableFileSortContext context, TextTableFile file, TempFileFactory factory, File listFile, String lineSeparator, TempFileRecordComparator comparator) throws IOException {
        this.size = 0;
        this.context = Ensure.notNull(context);
        this.factory = Ensure.notNull(factory);
        this.listFileWriter = new BufferedSyncWriter(listFile, file.getCharsetName(), 1);
        this.capacity = context.getMaxRows();
        this.buffer = new TextTableLine[this.capacity];
        this.delimiter = file.getDelimiter();
        this.writeBuffer = context.getWriterBuffer();
        this.comparator = comparator;
        this.lineSeparator = lineSeparator;
    }

    /**
     * 检查记录中是否有索引重复的数据
     *
     * @throws SortTableFileException 文件访问错误
     */
    protected void checkRepeat() throws SortTableFileException {
        TextTableLine last = this.buffer[0]; // 上一行的位置
        for (int i = 1; i < this.size; i++) {
            TextTableLine line = this.buffer[i];
            int value = this.comparator.compare(last, line);
            if (value == 0) {
                throw new SortTableFileException("increment.stdout.message039", this.context.getName(), last.getLineNumber(), line.getLineNumber());
            } else {
                last = line;
            }
        }
    }

    /**
     * 将记录写入到临时文件中
     *
     * @param record 数据文件
     * @throws IOException            写入文件发生错误
     * @throws SortTableFileException 文件中存在重复数据
     */
    public void write(TextTableLine record) throws IOException, SortTableFileException {
        this.buffer[this.size++] = record;
        if (this.size == this.capacity) {
            this.flush();
        }
    }

    public void flush() throws SortTableFileException, IOException {
        if (this.size > 0) {
            Arrays.sort(this.buffer, 0, this.size, this.comparator); // 排序
            if (this.context.isDuplicate()) {
                this.checkRepeat(); // 检查记录中是否有索引重复的数据
            }

            File file = this.factory.createTempFile(); // 临时文件
            try {
                this.flush(file); // 写入临时文件
            } finally {
                if (file.exists()) {
                    this.listFileWriter.writeLine(file.getAbsolutePath()); // 将临时文件绝对路径写入到清单文件
                    this.listFileWriter.flush();
                }
            }
            this.size = 0;
        }
    }

    /**
     * 把缓存记录写入到临时文件中
     *
     * @param file 临时文件
     * @throws IOException IO错误
     */
    protected void flush(File file) throws IOException {
        OutputStreamWriter out = IO.getFileWriter(file, this.listFileWriter.getCharsetName(), false);
        try {
            for (int i = 0; i < this.size; i++) {
                TextTableLine line = this.buffer[i];
                out.write(line.getContent());
                out.write(this.delimiter);
                out.write(String.valueOf(line.getLineNumber())); // 因为在临时文件的每行右侧，增加了一列用来记录所属的行号
                out.write(this.lineSeparator);
                if (i % this.writeBuffer == 0) {
                    out.flush();
                }
            }
            out.flush();
        } finally {
            out.close();
        }
    }

    public void close() throws IOException, SortTableFileException {
        try {
            this.flush();
        } finally {
            this.listFileWriter.close();
            this.buffer = null;
            this.size = 0;
        }
    }
}
