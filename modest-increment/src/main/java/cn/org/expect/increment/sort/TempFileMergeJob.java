package cn.org.expect.increment.sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.concurrent.AbstractJob;
import cn.org.expect.io.BufferedSyncWriter;
import cn.org.expect.io.BufferedWriter;
import cn.org.expect.io.TableLine;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableFileReader;
import cn.org.expect.io.TextTableLine;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;

/**
 * 合并数据文件
 */
public class TempFileMergeJob extends AbstractJob {

    /** 合并任务容器 */
    private final TempFileMergeJobReader reader;

    /** 待合并数据文件 */
    private final List<TextTableFile> files;

    /** 文件清单的输出流 */
    private final BufferedSyncWriter out;

    /** 排序规则 */
    private final Comparator<TableLine> comparator;

    /** 排序上下文信息 */
    private final TableFileSortContext context;

    /**
     * 初始化
     *
     * @param context          剥离增量任务上下文信息
     * @param in               任务输入流
     * @param out              文件清单的输出流（将产生的临时文件绝对路径写入到输出流中）
     * @param recordComparator 记录排序规则
     * @param list             临时文件集合
     */
    public TempFileMergeJob(TableFileSortContext context, TempFileMergeJobReader in, BufferedSyncWriter out, TempFileRecordComparator recordComparator, List<TextTableFile> list) {
        super();
        this.context = Ensure.notNull(context);

        // 合并文件任务名
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < list.size(); ) {
            TextTableFile file = list.get(i);
            buf.append(FileUtils.getFilename(file.getAbsolutePath()));
            if (++i < list.size()) {
                buf.append(", ");
            }
        }

        this.setName(ResourcesUtils.getMessage("increment.stdout.message002", context.getName(), buf));
        this.reader = in;
        this.files = list;
        this.out = out;
        this.comparator = recordComparator;
    }

    public int execute() throws IOException, SortTableFileException {
        TextTableFile file = this.merge(this.files); // 合并文件
        if (file != null) {
            this.out.writeLine(file.getAbsolutePath()); // 将合并后文件写入清单文件
            this.out.flush();
        }
        return 0;
    }

    /**
     * 合并数据文件
     *
     * @param files 数据文件
     * @return 返回合并后文件, 操作被中断时返回null
     * @throws SortTableFileException 合并文件发生错误
     */
    public TextTableFile merge(List<TextTableFile> files) throws SortTableFileException, IOException {
        if (this.status.isTerminate()) {
            return null;
        }

        if (files.isEmpty()) {
            throw new SortTableFileException("increment.stdout.message038", this.getName());
        }

        // 如果只有一个文件则直接退出
        if (files.size() == 1) {
            return files.get(0);
        }

        // 待合并的文件个数大于1时
        List<TextTableFile> list = new ArrayList<TextTableFile>((files.size() / 2) + 1);
        for (int i = 0; i < files.size(); i++) {
            if (this.status.isTerminate()) {
                return null;
            }

            TextTableFile file0 = files.get(i); // 第一个数据文件
            if (++i >= files.size()) {
                list.add(file0);
                break;
            }

            TextTableFile file1 = files.get(i); // 第二个数据文件
            list.add(this.merge(file0, file1)); // 合并后的文件保存到 list 中

            boolean delete0 = file0.delete();
            boolean delete1 = file1.delete();

            if (!delete0) {
                throw new SortTableFileException("increment.stdout.message036", this.getName(), file0.getAbsolutePath());
            }

            if (!delete1) {
                throw new SortTableFileException("increment.stdout.message036", this.getName(), file1.getAbsolutePath());
            }
        }

        return this.merge(list); // 递归调用，直到合并为1个文件为止
    }

    /**
     * 合并两个文件
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 返回合并后的文件, 被中断时返回 null
     * @throws IOException 合并文件发生错误
     */
    public TextTableFile merge(TextTableFile file1, TextTableFile file2) throws IOException, SortTableFileException {
        FileUtils.assertFile(file1.getFile());
        FileUtils.assertFile(file2.getFile());

        int readerBuffer = this.context.getReaderBuffer();
        TempFileFactory creator = this.context.getAttribute(TableFileSortContext.TEMP_FILE_FACTORY);

        // 合并操作
        TextTableFileReader in1 = null;
        TextTableFileReader in2 = null;
        try {
            in1 = file1.getReader(readerBuffer);
            in2 = file2.getReader(readerBuffer);

            // 合并后的文件
            TextTableFile file = file1.clone();
            file.setAbsolutePath(creator.createMergeFile().getAbsolutePath());
            BufferedWriter out = new BufferedWriter(file.getFile(), file.getCharsetName());
            try {
                this.merge(in1, in2, out);
            } finally {
                out.close();
            }

            // 保存合并记录数
            if (this.reader != null) {
                this.reader.setLineNumbers(in1.getLineNumber() + in2.getLineNumber());
            }
            return file;
        } finally {
            IO.close(in1, in2);
        }
    }

    /**
     * 合并操作
     *
     * @param reader1 文件输入流
     * @param reader2 文件输入流
     * @param out     文件输出流
     * @throws IOException            访问文件发生错误
     * @throws SortTableFileException 合并文件发生错误
     */
    private void merge(TextTableFileReader reader1, TextTableFileReader reader2, BufferedWriter out) throws IOException, SortTableFileException {
        TextTableLine line1 = reader1.readLine();
        TextTableLine line2 = reader2.readLine();

        // 比较文本 没有数据
        if (line1 == null) {
            while (line2 != null) {
                if (this.status.isTerminate()) {
                    return;
                } else {
                    out.writeLine(line2.getContent(), reader2.getLineSeparator());
                    line2 = reader2.readLine();
                }
            }
            return;
        }

        // 被比较文本 没有数据
        if (line2 == null) {
            while (line1 != null) {
                if (this.status.isTerminate()) {
                    return;
                } else {
                    out.writeLine(line1.getContent(), reader1.getLineSeparator());
                    line1 = reader1.readLine();
                }
            }
            return;
        }

        while (line1 != null && line2 != null) {
            if (this.status.isTerminate()) {
                return;
            }

            TableLine record1 = new TempFileRecord(line1);
            TableLine record2 = new TempFileRecord(line2);
            int v = this.comparator.compare(record1, record2);
            if (v == 0) {
                if (this.context.isDuplicate()) {
                    int column1 = line1.getColumn(); // 最右侧字段的位置，代表就在原文件中的行号
                    int column2 = line2.getColumn();
                    throw new SortTableFileException("increment.stdout.message039", this.getName(), line1.getColumn(column1), line2.getColumn(column2));
                } else {
                    out.writeLine(line1.getContent(), reader1.getLineSeparator());
                    out.writeLine(line2.getContent(), reader2.getLineSeparator());

                    line1 = reader1.readLine();
                    line2 = reader2.readLine();
                }
            } else if (v < 0) {
                out.writeLine(line1.getContent(), reader1.getLineSeparator());
                line1 = reader1.readLine();
            } else {
                out.writeLine(line2.getContent(), reader2.getLineSeparator());
                line2 = reader2.readLine();
            }
        }

        while (line2 != null) {
            if (this.status.isTerminate()) {
                return;
            } else {
                out.writeLine(line2.getContent(), reader2.getLineSeparator());
                line2 = reader2.readLine();
            }
        }

        while (line1 != null) {
            if (this.status.isTerminate()) {
                return;
            } else {
                out.writeLine(line1.getContent(), reader1.getLineSeparator());
                line1 = reader1.readLine();
            }
        }

        out.flush();
    }
}
