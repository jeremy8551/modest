package cn.org.expect.printer;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.Settings;
import cn.org.expect.util.TimeWatch;
import org.junit.Assert;
import org.junit.Test;

public class StandardPrinterTest {
    private final static Log log = LogFactory.getLog(StandardPrinterTest.class);

    @Test
    public void test() {
        Writer writer = new Writer() {

            public void write(char[] cbuf, int off, int len) {
                log.info(new String(cbuf, off, len));
            }

            public void flush() {
            }

            public void close() {
            }
        };

        StandardPrinter out = new StandardPrinter();
        out.setWriter(writer);
        out.println("test1");
        out.println("test2");

        TimeWatch watch = new TimeWatch();
        int total = 101525401;
        Progress progress = new Progress("task1", out, "${taskId} has ${totalRecord} records in total, ${process}% loaded, remaining ${leftTime} ..", total);
        for (int i = 1; i <= total; i++) {
            progress.print();
        }

        log.info("{} records, use time: {}", total, watch.useTime());
        Assert.assertEquals(total, progress.getCount().longValue());
    }

    @Test
    public void test0() {
        StandardPrinter printer = new StandardPrinter();
        printer.println("test", new Exception("common exception"));
        printer.println("task1", "task1");
        printer.println("task2", "task2");
        printer.println("task3", "task3");
        printer.println("task4", "task4");
        printer.close();

        Assert.assertNull(printer.getFormatter());
        Assert.assertNotNull(printer.toString());
        Assert.assertNull(printer.getWriter());
    }

    @Test
    public void test1() {
        CharArrayWriter writer = new CharArrayWriter();

        StandardPrinter printer = new StandardPrinter(writer);
        printer.print(true);
        printer.print('1');
        printer.print("234");
        printer.print("567");
        printer.print(890.123);
        printer.print((float) 456.789);
        printer.print(0);
        printer.print((long) 123456);
        printer.print(new StringBuilder().append("7890"));
        printer.flush();
        Assert.assertEquals("true1234567890.123456.78901234567890", writer.toString());

        writer.reset();
        printer.println();
        Assert.assertEquals(Settings.getLineSeparator(), writer.toString());

        writer.reset();
        printer.println(true);
        Assert.assertEquals("true" + Settings.getLineSeparator(), writer.toString());

        writer.reset();
        printer.println('0');
        Assert.assertEquals("0" + Settings.getLineSeparator(), writer.toString());

        writer.reset();
        printer.println("456");
        Assert.assertEquals("456" + Settings.getLineSeparator(), writer.toString());

        writer.reset();
        double d = 789.0123;
        printer.println(d);
        Assert.assertEquals("789.0123" + Settings.getLineSeparator(), writer.toString());

        writer.reset();
        printer.println((float) 456.789);
        Assert.assertEquals("456.789" + Settings.getLineSeparator(), writer.toString());

        writer.reset();
        printer.println(0);
        Assert.assertEquals("0" + Settings.getLineSeparator(), writer.toString());

        writer.reset();
        printer.println((long) 123456);
        Assert.assertEquals("123456" + Settings.getLineSeparator(), writer.toString());

        writer.reset();
        printer.println(new StringBuilder().append("7890"));
        Assert.assertEquals("7890" + Settings.getLineSeparator(), writer.toString());

        writer.reset();
        printer.println("test", new Exception("common exception"));

        writer.reset();
        printer.println("task1", "task1");

        writer.reset();
        printer.println("task2", "task2");

        writer.reset();
        printer.println("task3", "task3");

        writer.reset();
        printer.println("task4", "task4");

        printer.close();
    }

    @Test
    public void test2() {
        CharArrayWriter writer = new CharArrayWriter();

        StandardPrinter printer = new StandardPrinter(writer, null);
        printer.print(0);
        printer.flush();
        Assert.assertEquals("0", writer.toString());

        writer.reset();
        printer.println(1);
        Assert.assertEquals("1" + Settings.getLineSeparator(), writer.toString());

        printer.close();
    }

    @Test
    public void test3() {
        Writer writer = new Writer() {

            public void write(char[] buff, int off, int len) {
                log.info(new String(buff, off, len));
            }

            public void flush() throws IOException {
                throw new IOException("cs");
            }

            public void close() {
            }
        };

        StandardPrinter out = new StandardPrinter(writer);
        out.println("test");
        out.println("test", new IOException());
        out.close();
    }
}
