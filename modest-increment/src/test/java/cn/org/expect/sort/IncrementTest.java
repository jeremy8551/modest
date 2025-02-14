package cn.org.expect.sort;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import cn.org.expect.concurrent.EasyThreadSource;
import cn.org.expect.increment.IncrementContext;
import cn.org.expect.increment.IncrementJob;
import cn.org.expect.increment.IncrementListenerImpl;
import cn.org.expect.increment.IncrementPositionImpl;
import cn.org.expect.io.BufferedWriter;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableFileWriter;
import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.printer.StandardFilePrinter;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.Dates;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.Numbers;
import cn.org.expect.util.Settings;
import org.junit.Assert;
import org.junit.Test;

/**
 * 增量剥离测试：第一个与最后一个字段作为联合唯一索引，进行排序
 */
public class IncrementTest {

    public synchronized File[] getTempFiles(int rows) throws IOException {
        File tempDir = FileUtils.getTempDir("test", IncrementTest.class.getSimpleName());

        File newfile = FileUtils.allocate(tempDir, "NEWFILE.txt");
        FileUtils.createFile(newfile);

        File oldfile = FileUtils.allocate(tempDir, "OLDFILE.txt");
        FileUtils.createFile(oldfile);

        File resultfile = FileUtils.allocate(tempDir, "RESULT.txt");
        FileUtils.createFile(resultfile);

        Date start = Dates.parse("1960-01-01");
        Date end = new Date();
        String coldel = ",";

        // 写入旧文件
        BufferedWriter oldOut = new BufferedWriter(oldfile, CharsetUtils.get());
        try {
            StringBuilder buf = new StringBuilder(500);
            for (int i = 1; i <= rows; i++) {
                buf.setLength(0);
                buf.append("Line").append(i).append(coldel);
                buf.append("姓名").append(coldel);
                buf.append("身份证号").append(coldel);
                buf.append("手机号").append(coldel);
                buf.append(Dates.format19(start)).append(coldel);
                buf.append(Dates.format19(end)).append(coldel);
                for (int j = 1; j <= 20; j++) {
                    buf.append("Column").append(j).append(coldel);
                }
                buf.append("INDEX").append(i);
                oldOut.writeLine(buf.toString());
            }
        } finally {
            oldOut.close();
        }

        // 生成新文件
        BufferedWriter result = new BufferedWriter(resultfile, CharsetUtils.get());
        BufferedWriter newOut = new BufferedWriter(newfile, CharsetUtils.get());
        try {
            StringBuilder buf = new StringBuilder(500);

            // 写入不变数据与变化数据，已删除数据
            for (int i = 1; i <= rows; i++) {
                boolean m = false;

                buf.setLength(0);
                buf.append("Line").append(i).append(coldel);
                buf.append("姓名").append(coldel);
                buf.append("身份证号").append(coldel);
                buf.append("手机号").append(coldel);

                if (i == 1000 || i == 2002 || i == 4003) {
                    buf.append(coldel);
                    m = true;
                } else {
                    buf.append(Dates.format19(start)).append(coldel);
                }
                buf.append(Dates.format19(end)).append(coldel);

                for (int j = 1; j <= 20; j++) {
                    buf.append("Column").append(j).append(coldel);
                }

                buf.append("INDEX").append(i);

                // 删除 2001 行与 4000 行内容
                if (i == 2001 || i == 4000) {
                    result.writeLine(buf.toString());
                    continue;
                }

                // 写入变化记录
                if (m) {
                    result.writeLine(buf.toString());
                }

                newOut.writeLine(buf.toString());
            }

            // 写入一行新增数据
            buf.setLength(0);
            buf.append("M1").append(coldel);
            buf.append("姓名").append(coldel);
            buf.append("身份证号").append(coldel);
            buf.append("手机号").append(coldel);
            buf.append(Dates.format19(new Date())).append(coldel);
            buf.append(Dates.format19(new Date())).append(coldel);
            for (int j = 1; j <= 20; j++) {
                buf.append("Column").append(j).append(coldel);
            }
            buf.append("M1");
            newOut.writeLine(buf.toString());
            result.writeLine(buf.toString());
        } finally {
            newOut.close();
            result.close();
        }

        return new File[]{oldfile, newfile, resultfile};
    }

    @Test
    public void test() throws Exception {
        DefaultEasyContext context = new DefaultEasyContext("debug:sout+", EasyBeanEntry.class.getPackage().getName() + ":info");
        Log log = LogFactory.getLog(IncrementTest.class);

        File[] files = this.getTempFiles(100000);
        File oldfile = files[0];
        File newfile = files[1];
        File resultfile = files[2];
        File logfile = new File(newfile.getParentFile(), FileUtils.changeFilenameExt(newfile.getName(), "log"));
        File incfile = new File(newfile.getParentFile(), "INC_" + newfile.getName());

        String newFirstLine = FileUtils.readline(newfile, CharsetUtils.get(), 1);
        String oldFirstLine = FileUtils.readline(oldfile, CharsetUtils.get(), 1);

        Assert.assertEquals(newFirstLine, oldFirstLine);
        log.info("");
        log.info(newFirstLine);
        log.info(oldFirstLine);
        log.info("");

        // 当前目录
        log.info("新文件: " + newfile);
        log.info("旧文件: " + oldfile);
        log.info("增量文件: " + incfile.getAbsolutePath());
        log.info("日志文件: " + logfile.getAbsolutePath());
        log.info("正确文件: " + resultfile.getAbsolutePath());
        log.info("");

        // 读取文件类型
        TextTableFile file = context.getBean(TextTableFile.class, "txt");
        TextTableFile newtxtfile = file.clone();
        TextTableFile oldtxtfile = file.clone();
        TextTableFile inctxtfile = file.clone();

        // 设置文件
        newtxtfile.setAbsolutePath(newfile.getAbsolutePath());
        oldtxtfile.setAbsolutePath(oldfile.getAbsolutePath());
        inctxtfile.setAbsolutePath(incfile.getAbsolutePath());

        // 输出流
        TextTableFileWriter writer = inctxtfile.getWriter(false, IO.FILE_BYTES_BUFFER_SIZE);

        // 总列数
        int column = newtxtfile.countColumn();

        int[] newIndexPosition = new int[]{1, column};
        int[] oldIndexPosition = new int[]{1, column};
        int[] newComparePosition = this.toComparePositions(column, newIndexPosition);
        int[] oldComparePosition = this.toComparePositions(column, oldIndexPosition);
        IncrementPositionImpl position = new IncrementPositionImpl(newIndexPosition, oldIndexPosition, newComparePosition, oldComparePosition);

        IncrementContext incrementContext = new IncrementContext();
        incrementContext.setThreadSource(new EasyThreadSource());
        incrementContext.setName(newfile.getAbsolutePath());
        incrementContext.setNewFile(newtxtfile);
        incrementContext.setOldFile(oldtxtfile);
        incrementContext.setPosition(position);
        incrementContext.setNewWriter(writer);
        incrementContext.setUpdWriter(writer);
        incrementContext.setDelWriter(writer);
        incrementContext.setSortNewfile(true);
        incrementContext.setSortOldfile(true);
        incrementContext.setSortNewContext(new TableFileSortContext(context, null));
        incrementContext.setSortOldContext(new TableFileSortContext(context, null));
        incrementContext.setReplaceList(null);

        // 输出剥离增量日志
        StandardFilePrinter out = new StandardFilePrinter(logfile, CharsetUtils.get(), false);
        incrementContext.setLogger(new IncrementListenerImpl(out, position, newtxtfile, oldtxtfile));

        IncrementJob inc = new IncrementJob(incrementContext);
        Assert.assertEquals(0, inc.execute());

        // 判断剥离增量结果文件与正确文件是否相等
        long n = FileUtils.equalsIgnoreLineSeparator(incfile, CharsetUtils.get(), resultfile, CharsetUtils.get(), 0);
        if (n != 0) {
            String msg = "第 " + n + " 行不同!" + Settings.LINE_SEPARATOR;
            msg += FileUtils.readline(incfile, CharsetUtils.get(), n) + Settings.LINE_SEPARATOR; // 读取文件中的指定行内容
            msg += FileUtils.readline(resultfile, CharsetUtils.get(), n); // 读取文件中的指定行内容
            log.error(msg);
            Assert.fail();
        }

        // 输出增量日志
        log.info("");
        log.info("增量日志内容如下: ");
        log.info(FileUtils.readline(logfile, CharsetUtils.get(), 0));
    }

    /**
     * 转为对比位置信息
     *
     * @param column 总列数
     * @param indexs 主键位置数组
     * @return 数组
     */
    private int[] toComparePositions(int column, int[] indexs) {
        int[] array = new int[column - indexs.length]; // 排除主键字段
        for (int i = 0, j = 0; i < column; i++) {
            int position = i + 1;
            if (!Numbers.inArray(position, indexs)) {
                array[j++] = position;
            }
        }
        return array;
    }
}
