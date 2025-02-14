package cn.org.expect.printer;

import java.io.CharArrayWriter;
import java.io.IOException;

import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.Settings;
import org.junit.Assert;
import org.junit.Test;

public class OutputStreamPrinterTest {

    @Test
    public void test() throws IOException {
        CharArrayWriter writer = new CharArrayWriter();
        StandardPrinter printer = new StandardPrinter(writer);
        OutputStreamPrinter out = new OutputStreamPrinter(printer, null);

        out.setCharsetName(CharsetUtils.get());
        Assert.assertEquals(CharsetUtils.get(), out.getCharsetName());

        out.write("123\r\n4567\r89\n0测试".getBytes(CharsetUtils.get()));
        out.flush();

        Assert.assertEquals("123" + Settings.LINE_SEPARATOR + "4567" + Settings.LINE_SEPARATOR + "89" + Settings.LINE_SEPARATOR + "0测试" + Settings.LINE_SEPARATOR, writer.toString());

        writer.reset();
        out.write((byte) '\n');
        out.close();
    }

    @Test
    public void test1() {
        try {
            new OutputStreamPrinter(null, null);
            Assert.fail();
        } catch (Exception e) {
        }
    }
}
