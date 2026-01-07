package cn.org.expect.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.printer.Progress;
import cn.org.expect.printer.StandardPrinter;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithLogSettings;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.Dates;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import cn.org.expect.zh.ChineseRandom;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@RunWithLogSettings("sout+:debug")
public class TextTableFileCounterTest {
    private final static Log log = LogFactory.getLog(TextTableFileCounterTest.class);

    @EasyBean
    private EasyContext context;

    /**
     * 使用指定用户名创建一个文件
     */
    public File createfile() throws IOException {
        File dir = FileUtils.getTempDir(this.getClass().getSimpleName());
        File file = new File(dir, TextTableFileCounter.class.getSimpleName() + StringUtils.toRandomUUID() + ".csv");
        FileUtils.createFile(file);
        return file;
    }

    @Test
    public void testCalcTextFileLinesFile() throws Exception {
        ThreadSource threadSource = this.context.getBean(ThreadSource.class);
        File file = this.createfile();

        FileUtils.write(file, CharsetUtils.get(), false, (CharSequence) null);
        Assert.assertEquals(0, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, "");
        Assert.assertEquals(0, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, " ");
        Assert.assertEquals(1, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, "1\n");
        Assert.assertEquals(1, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, "1\n1");
        Assert.assertEquals(2, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, "1\n1\n");
        Assert.assertEquals(2, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, "1\r\n1\r\n");
        Assert.assertEquals(2, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, "1\n1\r\n");
        Assert.assertEquals(2, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, "1\n2\r\n3\r");
        Assert.assertEquals(new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()), 3);
    }

    @Test
    public void testCountTextFileLinesFile() throws Exception {
        ThreadSource threadSource = this.context.getBean(ThreadSource.class);
        File file = createfile();

        FileUtils.write(file, CharsetUtils.get(), false, (CharSequence) null);
        Assert.assertEquals(0, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, "");
        Assert.assertEquals(0, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, " ");
        Assert.assertEquals(1, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, "1\n");
        Assert.assertEquals(1, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, "1\n1");
        Assert.assertEquals(2, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, "1\n1\n");
        Assert.assertEquals(2, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, "1\r\n2\r\n");
        Assert.assertEquals(2, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, "1\n2\r\n");
        Assert.assertEquals(2, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, "1\n2\r\n3\r");
        Assert.assertEquals(3, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));

        FileUtils.write(file, CharsetUtils.get(), false, "1\n2\r\n3\r4");
        Assert.assertEquals(4, new TextTableFileCounter(threadSource, 2).execute(file, CharsetUtils.get()));
    }

    @Test
    public void test() throws Exception {
        LogFactory.load("debug:sout+", EasyBeanEntry.class.getPackage().getName() + ":info");
        ThreadSource threadSource = this.context.getBean(ThreadSource.class);
        File file = FileUtils.createTempFile("TEST_FILE_BIG.txt");
        String charsetName = CharsetUtils.get();
        long rows = this.createBigFile(file, charsetName);
        Assert.assertEquals(rows, new TextTableFileCounter(threadSource, 2).execute(file, charsetName));
        log.info("Parallel statistics completed, total {} rows!", rows);
    }

    /**
     * 生成一个2G表格文件 : <br>
     * 3445201 行数据, 共计 2147720745 字节, 文件 2.00 GB <br>
     * 文本最后一行没有换行符
     *
     * @param file        文件
     * @param charsetName 字符集
     * @return 返回行数
     * @throws IOException 生成文件发生错误
     */
    public long createBigFile(File file, String charsetName) throws IOException {
        int cache = 400; // 缓冲行数
        String str = "2G";
        long maxLength = 2L * 1024 * 1024 * 1024; // 文件最大值 2G

        // 已存在
        if (file.exists() && file.isFile() && file.length() >= maxLength) {
            if (new Date(file.lastModified()).compareTo(Dates.parse(Dates.format10(new Date()))) < 0) {
                file.delete(); // 如果文件不是当前生成的，则删除重建
            } else {
                long rows = this.countTextFileLines(file, charsetName);
                log.info("serial statistics completed, total: " + rows + " row!");
                return rows;
            }
        }
        log.info("maximum value: " + str + ", maxLength: " + maxLength);

        // 配置进度输出接口
        StandardPrinter printer = new StandardPrinter();
        Progress process = new Progress(printer, "preparing " + str + " text file ${process}%, ${leftTime}", maxLength);

        Date start = Dates.parse("2000-01-01");
        Date end = new Date();

        // 生成随机信息
        boolean lb = false;
        boolean over = false;
        ChineseRandom random = new ChineseRandom();
        Random r = random.get();
        BufferedWriter out = new BufferedWriter(file, charsetName, cache);
        try {
            long rows = 0;
            StringBuilder buf = new StringBuilder();
            while (true) { // 执行循环体
                if (lb) {
                    long currentLength = file.length();
                    process.print(currentLength, true);
                    if (currentLength >= maxLength) {
                        // 文件大小够了后需要中断
                        over = true;
                    }
                }

                buf.setLength(0);
                buf.append(++rows).append(','); // 行号
                buf.append(StringUtils.toRandomUUID()).append(','); // 唯一编号
                buf.append(random.nextName()).append(','); // 姓名
                buf.append(random.nextIdCard()).append(','); // 身份证号
                buf.append(random.nextMobile()).append(','); // 手机号
                buf.append("999").append(',');

                // 添加状态信息
                int value = r.nextInt(4);
                switch (value) {
                    case 0:
                        buf.append("\"normal\"").append(',');
                        break;
                    case 1:
                        buf.append("\"overdue\"").append(',');
                        break;
                    case 2:
                        buf.append("\"finish\"").append(',');
                        break;
                    case 3:
                        buf.append("\"turnoff\"").append(',');
                        break;
                    default:
                        throw new IllegalArgumentException(String.valueOf(value));
                }

                // 添加随机账号
                buf.append("\"P");
                buf.append(Dates.format08(Dates.random(start, end)));
                buf.append("27922650                       \",");

                buf.append(Dates.format08(Dates.random(start, end))).append(',');
                buf.append(Dates.format08(Dates.random(start, end))).append(',');
                buf.append(Dates.format08(Dates.random(start, end))).append(',');

                buf.append('+').append(r.nextDouble() * 100000).append(',');
                buf.append('+').append(r.nextDouble() * 100000).append(',');
                buf.append('+').append(r.nextDouble() * 100000).append(',');

                buf.append(Dates.format08(Dates.random(start, end))).append(",,");
                buf.append("+000000000000000000.00,+000000000000000000.00,");
                buf.append('+').append(r.nextDouble() * 100000).append(',');
                buf.append("\"3\",20210413,,,,,\"04501               \",\"GD10000765215                           \",\"BA2019053000000195                      \",\"37010006                      \",\"鏉庡崥                                                        \",20210413,\"13070050            \",,,,+000000000000250000.00,+000000000000000099.68,+000000000000000000.00,");

                String lineSeparator;
                switch (r.nextInt(3)) {
                    case 0:
                        lineSeparator = "\n";
                        break;
                    case 1:
                        lineSeparator = "\r\n";
                        break;
                    case 2:
                        lineSeparator = "\r";
                        break;

                    default:
                        throw new IllegalArgumentException();
                }

                if (over) {
                    out.write(buf.toString());
                    break;
                } else {
                    // 将缓存写入文件
                    lb = out.writeLine(buf.toString(), lineSeparator);
                }
            }
            out.flush();

            log.info(file.getAbsolutePath() + " total write " + rows + " rows data, total " + file.length() + " bytes!");
            Assert.assertEquals(rows, this.countTextFileLines(file, charsetName));
            log.info("serial statistics completed ..");
            return rows;
        } finally {
            out.close();
        }
    }

    /**
     * 统计文本文件行数 <br>
     * 使用逐行读取的方式计算文本文件行数
     *
     * @param file        文件
     * @param charsetName 文件字符集, 为空时取操作系统默认值
     * @return 文本行数
     * @throws IOException 读取文件发生错误
     */
    public long countTextFileLines(File file, String charsetName) throws IOException {
        BufferedReader in = IO.getBufferedReader(file, charsetName, IO.getCharArrayLength());
        try {
            long rows = 0;
            while (in.readLine() != null) {
                rows++;
            }
            return rows;
        } finally {
            in.close();
        }
    }
}
