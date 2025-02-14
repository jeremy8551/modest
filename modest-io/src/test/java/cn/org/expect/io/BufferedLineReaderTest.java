package cn.org.expect.io;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

public class BufferedLineReaderTest {

    @Test
    public void test0() throws IOException {
        BufferedLineReader in = new BufferedLineReader(new StringReader("line1\rline2\r\nline3\nline4\rline5 "), 2, 10);
        String line;
        Assert.assertNotNull((line = in.readLine()));
        Assert.assertEquals(line, "line1");
        Assert.assertEquals(in.getLineSeparator(), "\r");

        Assert.assertNotNull((line = in.readLine()));
        Assert.assertEquals(line, "line2");
        Assert.assertEquals(in.getLineSeparator(), "\r\n");

        Assert.assertNotNull((line = in.readLine()));
        Assert.assertEquals(line, "line3");
        Assert.assertEquals(in.getLineSeparator(), "\n");

        Assert.assertNotNull((line = in.readLine()));
        Assert.assertEquals(line, "line4");
        Assert.assertEquals(in.getLineSeparator(), "\r");

        Assert.assertNotNull((line = in.readLine()));
        Assert.assertEquals(line, "line5 ");
        Assert.assertEquals(in.getLineSeparator(), "");
        in.close();
    }

    @Test
    public void test1() throws IOException {
        String str = "1\r2\n3\r\n4\n\n";
        BufferedLineReader in = new BufferedLineReader(str, 2, 0);

        String s = in.readLine();
        Assert.assertEquals(1, in.getLineNumber());
        Assert.assertEquals(s, "1");
        Assert.assertEquals(in.getLineSeparator(), "\r");

        s = in.readLine();
        Assert.assertEquals(2, in.getLineNumber());
        Assert.assertEquals(s, "2");
        Assert.assertEquals(in.getLineSeparator(), "\n");

        s = in.readLine();
        Assert.assertEquals(3, in.getLineNumber());
        Assert.assertEquals(s, "3");
        Assert.assertEquals(in.getLineSeparator(), "\r\n");

        s = in.readLine();
        Assert.assertEquals(4, in.getLineNumber());
        Assert.assertEquals(s, "4");
        Assert.assertEquals(in.getLineSeparator(), "\n");

        s = in.readLine();
        Assert.assertEquals(5, in.getLineNumber());
        Assert.assertEquals(s, "");
        Assert.assertEquals(in.getLineSeparator(), "\n");

        s = in.readLine();
        Assert.assertEquals(5, in.getLineNumber());
        Assert.assertNull(s);
        Assert.assertEquals(in.getLineSeparator(), "");
        in.close();
    }

    @Test
    public void test2() throws IOException {
        String str = "1\r2\n3\r\n4\n\n";
        BufferedLineReader in = new BufferedLineReader(str, 1, 0);

        String s = in.readLine();
        Assert.assertEquals(1, in.getLineNumber());
        Assert.assertEquals(s, "1");
        Assert.assertEquals(in.getLineSeparator(), "\r");

        s = in.readLine();
        Assert.assertEquals(2, in.getLineNumber());
        Assert.assertEquals(s, "2");
        Assert.assertEquals(in.getLineSeparator(), "\n");

        s = in.readLine();
        Assert.assertEquals(3, in.getLineNumber());
        Assert.assertEquals(s, "3");
        Assert.assertEquals(in.getLineSeparator(), "\r\n");

        s = in.readLine();
        Assert.assertEquals(4, in.getLineNumber());
        Assert.assertEquals(s, "4");
        Assert.assertEquals(in.getLineSeparator(), "\n");

        s = in.readLine();
        Assert.assertEquals(5, in.getLineNumber());
        Assert.assertEquals(s, "");
        Assert.assertEquals(in.getLineSeparator(), "\n");

        s = in.readLine();
        Assert.assertEquals(5, in.getLineNumber());
        Assert.assertNull(s);
        Assert.assertEquals(in.getLineSeparator(), "");
        in.close();
    }

    @Test
    public void test3() throws IOException {
        String str = "1\r2\n3\r\n4\n\n";
        BufferedLineReader in = new BufferedLineReader(str);

        String s = in.readLine();
        Assert.assertEquals(1, in.getLineNumber());
        Assert.assertEquals(s, "1");
        Assert.assertEquals(in.getLineSeparator(), "\r");

        s = in.readLine();
        Assert.assertEquals(2, in.getLineNumber());
        Assert.assertEquals(s, "2");
        Assert.assertEquals(in.getLineSeparator(), "\n");

        s = in.readLine();
        Assert.assertEquals(3, in.getLineNumber());
        Assert.assertEquals(s, "3");
        Assert.assertEquals(in.getLineSeparator(), "\r\n");

        s = in.readLine();
        Assert.assertEquals(4, in.getLineNumber());
        Assert.assertEquals(s, "4");
        Assert.assertEquals(in.getLineSeparator(), "\n");

        s = in.readLine();
        Assert.assertEquals(5, in.getLineNumber());
        Assert.assertEquals(s, "");
        Assert.assertEquals(in.getLineSeparator(), "\n");

        s = in.readLine();
        Assert.assertEquals(5, in.getLineNumber());
        Assert.assertNull(s);
        Assert.assertEquals(in.getLineSeparator(), "");
        in.close();
    }

    @Test
    public void test4() throws IOException {
        String str = "1\r2\n3\r\n4\n\n";
        BufferedLineReader in = new BufferedLineReader(str);

        Assert.assertTrue(in.hasNext());
        String s = in.next();
        Assert.assertEquals(1, in.getLineNumber());
        Assert.assertEquals(s, "1");
        Assert.assertEquals(in.getLineSeparator(), "\r");
        Assert.assertFalse(in.isClosed());

        Assert.assertTrue(in.hasNext());
        s = in.next();
        Assert.assertEquals(2, in.getLineNumber());
        Assert.assertEquals(s, "2");
        Assert.assertEquals(in.getLineSeparator(), "\n");
        Assert.assertFalse(in.isClosed());

        Assert.assertTrue(in.hasNext());
        s = in.next();
        Assert.assertEquals(3, in.getLineNumber());
        Assert.assertEquals(s, "3");
        Assert.assertEquals(in.getLineSeparator(), "\r\n");
        Assert.assertFalse(in.isClosed());

        Assert.assertTrue(in.hasNext());
        s = in.next();
        Assert.assertEquals(4, in.getLineNumber());
        Assert.assertEquals(s, "4");
        Assert.assertEquals(in.getLineSeparator(), "\n");
        Assert.assertFalse(in.isClosed());

        Assert.assertTrue(in.hasNext());
        s = in.next();
        Assert.assertEquals(5, in.getLineNumber());
        Assert.assertEquals(s, "");
        Assert.assertEquals(in.getLineSeparator(), "\n");

        Assert.assertFalse(in.hasNext());
        s = in.next();
        Assert.assertEquals(5, in.getLineNumber());
        Assert.assertNull(s);
        Assert.assertEquals(in.getLineSeparator(), "");

        Assert.assertTrue(in.isClosed());
        in.close();
    }

    @Test
    public void test5() throws IOException {
        StringBuilder str = new StringBuilder("1\r2\n3\r\n4\n\n");
        BufferedLineReader in = new BufferedLineReader(str);

        String s = in.readLine();
        Assert.assertEquals(1, in.getLineNumber());
        Assert.assertEquals(s, "1");
        Assert.assertEquals(in.getLineSeparator(), "\r");

        s = in.readLine();
        Assert.assertEquals(2, in.getLineNumber());
        Assert.assertEquals(s, "2");
        Assert.assertEquals(in.getLineSeparator(), "\n");

        s = in.readLine();
        Assert.assertEquals(3, in.getLineNumber());
        Assert.assertEquals(s, "3");
        Assert.assertEquals(in.getLineSeparator(), "\r\n");

        s = in.readLine();
        Assert.assertEquals(4, in.getLineNumber());
        Assert.assertEquals(s, "4");
        Assert.assertEquals(in.getLineSeparator(), "\n");

        s = in.readLine();
        Assert.assertEquals(5, in.getLineNumber());
        Assert.assertEquals(s, "");
        Assert.assertEquals(in.getLineSeparator(), "\n");

        s = in.readLine();
        Assert.assertEquals(5, in.getLineNumber());
        Assert.assertNull(s);
        Assert.assertEquals(in.getLineSeparator(), "");

        in.close();
    }

    @Test
    public void test6() throws IOException {
        StringBuilder str = new StringBuilder("1");
        BufferedLineReader in = new BufferedLineReader(str);
        in.skip(str.length());
        Assert.assertEquals(in.getLineNumber(), 1);
        in.close();
    }

    @Test
    public void test7() throws IOException {
        StringBuilder str = new StringBuilder("1\r2\n3\r\n4");
        BufferedLineReader in = new BufferedLineReader(str);
        in.skip(str.length() + 1);
        Assert.assertEquals(in.getLineNumber(), 4);
        in.close();
    }

    @Test
    public void test8() throws IOException {
        StringBuilder str = new StringBuilder("1\r2\n3\r\n4");
        BufferedLineReader in = new BufferedLineReader(str);
        in.skip(str.length());
        Assert.assertEquals(in.getLineNumber(), 4);
        in.close();
    }

    @Test
    public void test9() throws IOException {
        StringBuilder str = new StringBuilder("1\r2\n3\r\n4");
        BufferedLineReader in = new BufferedLineReader(str);
        Assert.assertTrue(in.hasNext());
        Assert.assertEquals(in.next(), "1");
        Assert.assertEquals(in.getLineNumber(), 1);

        Assert.assertTrue(in.hasNext());
        Assert.assertEquals(in.next(), "2");
        Assert.assertEquals(in.getLineNumber(), 2);

        Assert.assertTrue(in.hasNext());
        Assert.assertEquals(in.next(), "3");
        Assert.assertEquals(in.getLineNumber(), 3);

        Assert.assertTrue(in.hasNext());
        Assert.assertEquals(in.next(), "4");
        Assert.assertEquals(in.getLineNumber(), 4);

        Assert.assertFalse(in.hasNext());
        in.close();
    }

    @Test
    public void test10() throws IOException {
        BufferedLineReader in = new BufferedLineReader("1\n2\n3");

        Assert.assertTrue(in.hasNext());
        Assert.assertEquals("1", in.next());
        Assert.assertEquals(1, in.getLineNumber());
        Assert.assertEquals("\n", in.getLineSeparator());

        Assert.assertTrue(in.hasNext());
        Assert.assertEquals("2", in.next());
        Assert.assertEquals(2, in.getLineNumber());
        Assert.assertEquals("\n", in.getLineSeparator());

        Assert.assertTrue(in.hasNext());
        Assert.assertEquals("3", in.next());
        Assert.assertEquals(3, in.getLineNumber());
        Assert.assertEquals(in.getLineSeparator(), "");

        Assert.assertFalse(in.hasNext());
        Assert.assertNull(in.next());
        Assert.assertEquals(3, in.getLineNumber());
        in.close();
    }

    @Test
    public void test11() throws IOException {
        BufferedLineReader in = new BufferedLineReader(" ");
        Assert.assertTrue(in.hasNext());
        Assert.assertEquals(" ", in.next());
        Assert.assertEquals(1, in.getLineNumber());
        Assert.assertEquals(in.getLineSeparator(), "");
        in.close();
    }

    @Test
    public void test12() throws IOException {
        BufferedLineReader in = new BufferedLineReader("");
        Assert.assertFalse(in.hasNext());
        Assert.assertNull(in.next());
        Assert.assertEquals(0, in.getLineNumber());
        Assert.assertEquals(in.getLineSeparator(), "");
        in.close();
    }
}
