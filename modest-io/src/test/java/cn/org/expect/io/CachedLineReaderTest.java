package cn.org.expect.io;

import java.io.CharArrayReader;
import java.io.IOException;

import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class CachedLineReaderTest {

    @Test
    public void test() throws IOException {
        String str = "1\r2\n3\r\n4\n\n";
        CacheLineReader reader = new CacheLineReader(new CharArrayReader(str.toCharArray()), 1);

        Assert.assertEquals(1, reader.cacheLine(1));
        Assert.assertEquals("", StringUtils.escapeLineSeparator(reader.getLineSeparator()));
        Assert.assertEquals(0, reader.getLineNumber());

        try {
            reader.cacheLine(1);
            Assert.assertEquals("1", reader.readLine());
            Assert.assertEquals(1, reader.getLineNumber());
            Assert.assertEquals("\r", reader.getLineSeparator());

            reader.cacheLine(1);
            Assert.assertEquals(1, reader.getLineNumber());

            Assert.assertEquals("2", reader.readLine());
            Assert.assertEquals(2, reader.getLineNumber());
            Assert.assertEquals("\n", reader.getLineSeparator());

            reader.cacheLine(1);
            Assert.assertEquals("3", reader.readLine());
            Assert.assertEquals(3, reader.getLineNumber());
            Assert.assertEquals("\r\n", reader.getLineSeparator());

            reader.cacheLine(1);
            Assert.assertEquals("4", reader.readLine());
            Assert.assertEquals(4, reader.getLineNumber());
            Assert.assertEquals("\n", reader.getLineSeparator());

            reader.cacheLine(1);
            Assert.assertEquals("", reader.readLine());
            Assert.assertEquals(5, reader.getLineNumber());
            Assert.assertEquals("\n", reader.getLineSeparator());

            Assert.assertEquals(0, reader.cacheLine(1));
            Assert.assertTrue(reader.readLine() == null && "".equals(reader.getLineSeparator()));
        } finally {
            reader.close();
        }
    }

    @Test
    public void test1() throws IOException {
        String str = "\r2\n3\r\n4\n\n";
        CacheLineReader r = new CacheLineReader(new CharArrayReader(str.toCharArray()), 1);
        try {
            Assert.assertEquals(1, r.cacheLine(1));
            Assert.assertEquals(0, r.getLineNumber());

            Assert.assertEquals("", r.readLine());
            Assert.assertEquals(1, r.getLineNumber());
            Assert.assertEquals(1, r.cacheLine(1));
            Assert.assertEquals(StringUtils.escapeLineSeparator("\r"), StringUtils.escapeLineSeparator(r.getLineSeparator()));

            Assert.assertEquals("2", r.readLine());
            Assert.assertEquals(2, r.getLineNumber());
            r.cacheLine(1);
            Assert.assertEquals("\n", r.getLineSeparator());

            Assert.assertEquals("3", r.readLine());
            Assert.assertEquals(3, r.getLineNumber());
            r.cacheLine(1);
            Assert.assertEquals("\r\n", r.getLineSeparator());

            Assert.assertEquals("4", r.readLine());
            Assert.assertEquals(4, r.getLineNumber());
            r.cacheLine(1);
            Assert.assertEquals("\n", r.getLineSeparator());

            Assert.assertEquals("", r.readLine());
            Assert.assertEquals(5, r.getLineNumber());
            Assert.assertEquals("\\n", StringUtils.escapeLineSeparator(r.getLineSeparator()));
            r.cacheLine(1);

            Assert.assertNull(r.readLine());
            Assert.assertEquals("", r.getLineSeparator());
        } finally {
            IO.close(r);
        }
    }

    @Test
    public void test2() throws IOException {
        String str = "";
        CacheLineReader r = new CacheLineReader(new CharArrayReader(str.toCharArray()), 1);
        r.cacheLine(1);
        Assert.assertEquals("", r.getLineSeparator());
        try {
            Assert.assertEquals(0, r.getLineNumber());
            Assert.assertNull(r.readLine());
        } finally {
            r.close();
        }
    }

    @Test
    public void test3() throws IOException {
        String str = "1";
        CacheLineReader r = new CacheLineReader(new CharArrayReader(str.toCharArray()), 1);

        r.cacheLine(1);
        Assert.assertEquals("", r.getLineSeparator());

        try {
            Assert.assertEquals(0, r.getLineNumber());
            Assert.assertEquals("1", r.readLine());
            Assert.assertEquals(1, r.getLineNumber());
            Assert.assertEquals("", r.getLineSeparator());
        } finally {
            r.close();
        }
    }

    @Test
    public void test4() throws IOException {
        String str = "\r\n\r\n";
        CacheLineReader r = new CacheLineReader(new CharArrayReader(str.toCharArray()), 1);
        try {
            r.cacheLine(1);
            Assert.assertEquals(0, r.getLineNumber());

            Assert.assertEquals("", r.readLine());
            Assert.assertEquals(1, r.getLineNumber());
            Assert.assertEquals("\r\n", r.getLineSeparator());

            r.cacheLine(1);
            Assert.assertEquals("", r.readLine());
            Assert.assertEquals(2, r.getLineNumber());
            Assert.assertEquals("\r\n", r.getLineSeparator());
        } finally {
            IO.close(r);
        }
    }

    @Test
    public void test5() throws IOException {
        String str = "1234567890\r2234567890\n3234567890\r\n4";
        CacheLineReader reader = new CacheLineReader(new CharArrayReader(str.toCharArray()), 1);
        try {
            Assert.assertEquals(0, reader.getLineNumber());

            Assert.assertEquals("1234567890", reader.readLine());
            Assert.assertEquals(1, reader.getLineNumber());
            Assert.assertEquals("\r", reader.getLineSeparator());

            Assert.assertEquals("2234567890", reader.readLine());
            Assert.assertEquals(2, reader.getLineNumber());
            Assert.assertEquals("\n", reader.getLineSeparator());

            Assert.assertEquals("3234567890", reader.readLine());
            Assert.assertEquals(3, reader.getLineNumber());
            Assert.assertEquals("\r\n", reader.getLineSeparator());

            Assert.assertEquals("4", reader.readLine());
            Assert.assertEquals(4, reader.getLineNumber());
            Assert.assertEquals("", reader.getLineSeparator());
        } finally {
            IO.close(reader);
        }
    }

    @Test
    public void test6() throws IOException {
        String str = "1\r2\n3\r\n4\n\n";

        CacheLineReader reader = new CacheLineReader(str);
        reader.cacheLine(1);
        Assert.assertTrue(reader.getLineSeparator().equals(""));

        try {
            Assert.assertTrue(reader.getLineNumber() == 0);

            reader.cacheLine(1);
            Assert.assertTrue("1".equals(reader.readLine()));
            Assert.assertTrue(reader.getLineNumber() == 1);
            Assert.assertTrue("\r".equals(reader.getLineSeparator()));

            reader.cacheLine(1);
            Assert.assertTrue("2".equals(reader.readLine()));
            Assert.assertTrue(reader.getLineNumber() == 2);
            Assert.assertTrue("\n".equals(reader.getLineSeparator()));

            reader.cacheLine(1);
            Assert.assertTrue("3".equals(reader.readLine()));
            Assert.assertTrue(reader.getLineNumber() == 3);
            Assert.assertEquals("\r\n", reader.getLineSeparator());

            reader.cacheLine(1);
            Assert.assertTrue("4".equals(reader.readLine()));
            Assert.assertTrue(reader.getLineNumber() == 4);
            Assert.assertTrue("\n".equals(reader.getLineSeparator()));

            reader.cacheLine(1);
            Assert.assertTrue("".equals(reader.readLine()));
            Assert.assertTrue(reader.getLineNumber() == 5);
            Assert.assertTrue("\n".equals(reader.getLineSeparator()));

            reader.cacheLine(1);
            Assert.assertTrue(reader.readLine() == null);
            Assert.assertTrue("".equals(reader.getLineSeparator()));
        } finally {
            reader.close();
        }
    }

    @Test
    public void test9() throws IOException {
        String str = "1\r2\n3\r\n4\n\n";
        CacheLineReader in = new CacheLineReader(new CharArrayReader(str.toCharArray()), 1);
        try {
            Assert.assertEquals("1", in.readLine());
            Assert.assertEquals(1, in.getLineNumber());
            Assert.assertEquals("\r", in.getLineSeparator());

            Assert.assertEquals(1, in.getLineNumber());

            Assert.assertEquals("2", in.readLine());
            Assert.assertEquals(2, in.getLineNumber());
            Assert.assertEquals("\n", in.getLineSeparator());

            Assert.assertEquals("3", in.readLine());
            Assert.assertEquals(3, in.getLineNumber());
            Assert.assertEquals("\r\n", in.getLineSeparator());

            Assert.assertEquals("4", in.readLine());
            Assert.assertEquals(4, in.getLineNumber());
            Assert.assertEquals("\n", in.getLineSeparator());

            Assert.assertEquals("", in.readLine());
            Assert.assertEquals(5, in.getLineNumber());
            Assert.assertEquals("\n", in.getLineSeparator());

            Assert.assertNull(in.readLine());
            Assert.assertEquals(5, in.getLineNumber());
            Assert.assertEquals("", in.getLineSeparator());
        } finally {
            IO.close(in);
        }
    }

    @Test
    public void test8() throws IOException {
        String str = "1\r2\n3\r\n4\n\n";
        CacheLineReader in = new CacheLineReader(new CharArrayReader(str.toCharArray()), 1);
        try {
            Assert.assertEquals("1", in.readLine());
            Assert.assertEquals(1, in.getLineNumber());
            Assert.assertEquals("\r", in.getLineSeparator());

            Assert.assertEquals(1, in.getLineNumber());

            Assert.assertEquals("2", in.readLine());
            Assert.assertEquals(2, in.getLineNumber());
            Assert.assertEquals("\n", in.getLineSeparator());

            Assert.assertEquals("3", in.readLine());
            Assert.assertEquals(3, in.getLineNumber());
            Assert.assertEquals("\r\n", in.getLineSeparator());

            Assert.assertEquals("4", in.readLine());
            Assert.assertEquals(4, in.getLineNumber());
            Assert.assertEquals("\n", in.getLineSeparator());

            Assert.assertEquals("", in.readLine());
            Assert.assertEquals(in.getLineNumber(), 5);
            Assert.assertEquals("\n", in.getLineSeparator());

            Assert.assertNull(in.readLine());
            Assert.assertEquals(in.getLineNumber(), 5);
            Assert.assertEquals("", in.getLineSeparator());
        } finally {
            IO.close(in);
        }
    }
}
