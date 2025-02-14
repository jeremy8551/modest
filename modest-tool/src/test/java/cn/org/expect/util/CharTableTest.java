package cn.org.expect.util;

import org.junit.Assert;
import org.junit.Test;

public class CharTableTest {

    private static String getString() {
        String str = "";
        str += "----------------------------------" + Settings.LINE_SEPARATOR;
        str += "列      b                         " + Settings.LINE_SEPARATOR;
        str += "------  --------------------------" + Settings.LINE_SEPARATOR;
        str += "第一列  测试换行列                " + Settings.LINE_SEPARATOR;
        str += "        你好啊俄式一下水电费水电费" + Settings.LINE_SEPARATOR;
        str += "        世界                      " + Settings.LINE_SEPARATOR;
        str += "3       4                         " + Settings.LINE_SEPARATOR;
        str += "----------------------------------";
        return str;
    }

    @Test
    public void test1() {
        CharTable ct = new CharTable();
        ct.addTitle("a");
        ct.addCell("1").addCell("2").addCell("3");
        ct.toString(CharTable.Style.STANDARD);

        String str = "";
        str += Settings.LINE_SEPARATOR;
        str += "-" + Settings.LINE_SEPARATOR;
        str += "a" + Settings.LINE_SEPARATOR;
        str += "-" + Settings.LINE_SEPARATOR;
        str += "1" + Settings.LINE_SEPARATOR;
        str += "2" + Settings.LINE_SEPARATOR;
        str += "3" + Settings.LINE_SEPARATOR;
        str += "-";
        Assert.assertEquals(StringUtils.trimBlank(str), StringUtils.trimBlank(ct.toString()));
    }

    @Test
    public void test2() {
        CharTable ct = new CharTable();
        ct.addTitle("a");
        ct.addTitle("b");
        ct.addCell("1").addCell("2").addCell("3").addCell("4");
        ct.toString(CharTable.Style.STANDARD);

        String r = "";
        r += "----" + Settings.LINE_SEPARATOR;
        r += "a  b" + Settings.LINE_SEPARATOR;
        r += "-  -" + Settings.LINE_SEPARATOR;
        r += "1  2" + Settings.LINE_SEPARATOR;
        r += "3  4" + Settings.LINE_SEPARATOR;
        r += "----";
        Assert.assertEquals(StringUtils.trimBlank(r), StringUtils.trimBlank(ct.toString()));
    }

    @Test
    public void test3() {
        CharTable ct = new CharTable();
        ct.addTitle("列");
        ct.addTitle("b");
        ct.addCell("1").addCell("2").addCell("3").addCell("4");
        ct.toString(CharTable.Style.STANDARD);

        String r = "";
        r += "-----" + Settings.LINE_SEPARATOR;
        r += "列  b" + Settings.LINE_SEPARATOR;
        r += "--  -" + Settings.LINE_SEPARATOR;
        r += "1   2" + Settings.LINE_SEPARATOR;
        r += "3   4" + Settings.LINE_SEPARATOR;
        r += "-----";
        Assert.assertEquals(StringUtils.trimBlank(r), StringUtils.trimBlank(ct.toString()));
    }

    @Test
    public void test4() {
        CharTable ct = new CharTable();
        ct.addTitle("列");
        ct.addTitle("b");
        ct.addCell("1").addCell("2").addCell("3").addCell("4");
        ct.toString(CharTable.Style.STANDARD);

        String r = "";
        r += "-----" + Settings.LINE_SEPARATOR;
        r += "列  b" + Settings.LINE_SEPARATOR;
        r += "--  -" + Settings.LINE_SEPARATOR;
        r += "1   2" + Settings.LINE_SEPARATOR;
        r += "3   4" + Settings.LINE_SEPARATOR;
        r += "-----";
        Assert.assertEquals(StringUtils.trimBlank(r), StringUtils.trimBlank(ct.toString()));
    }

    @Test
    public void test5() {
        CharTable ct = new CharTable();
        ct.addTitle("列");
        ct.addTitle("b");
        ct.addCell("第一列").addCell("测试换行列\n你好啊俄式一下水电费水电费\n世界\n").addCell("3").addCell("4");
        ct.toString(CharTable.Style.STANDARD);

        Assert.assertEquals(StringUtils.trimBlank(getString()), StringUtils.trimBlank(ct.toString()));
    }

    @Test
    public void test6() {
        CharTable ct = new CharTable();
        ct.addTitle("列");
        ct.addTitle("b");
        ct.addCell("第一列").addCell("测试换行列\n你好啊俄式一下水电费水电费\n世界\n").addCell("3").addCell("4");
        ct.toString(CharTable.Style.DB2);

        String r = "";
        r += "列      b                         " + Settings.LINE_SEPARATOR;
        r += "------  --------------------------" + Settings.LINE_SEPARATOR;
        r += "第一列  测试换行列                " + Settings.LINE_SEPARATOR;
        r += "        你好啊俄式一下水电费水电费" + Settings.LINE_SEPARATOR;
        r += "        世界                      " + Settings.LINE_SEPARATOR;
        r += "3       4                         ";

        Assert.assertEquals(StringUtils.trimBlank(r), StringUtils.trimBlank(ct.toString()));
    }

    @Test
    public void test7() {
        CharTable ct = new CharTable();
        ct.addTitle("列");
        ct.addTitle("b");
        ct.addCell("第一列").addCell("测试换行列\n你好啊俄式一下水电费水电费\n世界\n").addCell("3").addCell("4");
        ct.toString(CharTable.Style.SHELL);

        String r = "";
        r += "列      b                         " + Settings.LINE_SEPARATOR;
        r += "第一列  测试换行列                " + Settings.LINE_SEPARATOR;
        r += "        你好啊俄式一下水电费水电费" + Settings.LINE_SEPARATOR;
        r += "        世界                      " + Settings.LINE_SEPARATOR;
        r += "3       4                         ";

        Assert.assertEquals(StringUtils.trimBlank(r), StringUtils.trimBlank(ct.toString()));
    }

    @Test
    public void test8() {
        CharTable ct = new CharTable();
        ct.addTitle("列");
        ct.addTitle("b");
        ct.addCell("第一列").addCell("测试换行列\n你好啊俄式一下水电费水电费\n世界\n").addCell("3").addCell("4");
        ct.toString(CharTable.Style.DB2);

        String r = "";
        r += "列      b                         " + Settings.LINE_SEPARATOR;
        r += "------  --------------------------" + Settings.LINE_SEPARATOR;
        r += "第一列  测试换行列                " + Settings.LINE_SEPARATOR;
        r += "        你好啊俄式一下水电费水电费" + Settings.LINE_SEPARATOR;
        r += "        世界                      " + Settings.LINE_SEPARATOR;
        r += "3       4";

        Assert.assertEquals(StringUtils.trimBlank(r), StringUtils.trimBlank(ct.toString()));
    }

    @Test
    public void test9() {
        CharTable ct = new CharTable();
        ct.addTitle("测试1-");
        ct.addTitle("测试2");
        ct.addTitle("测试3");
        ct.addCell("1");
        ct.addCell("测试\nksdf");
        ct.addCell("");
        ct.addCell("s");
        ct.addCell("|");
        ct.addCell("_");
        ct.addCell("----");
        ct.addCell("");
        ct.addCell("");
        ct.toString(CharTable.Style.MARKDOWN);

        String r = "";
        r += "| 测试1- | 测试2        | 测试3 |" + Settings.LINE_SEPARATOR;
        r += "| ------ | ------------ | ----- |" + Settings.LINE_SEPARATOR;
        r += "| 1      | 测试<br>ksdf |       |" + Settings.LINE_SEPARATOR;
        r += "| s      | \\|           | _     |" + Settings.LINE_SEPARATOR;
        r += "| ----   |              |       |";

        Assert.assertEquals(StringUtils.trimBlank(r), StringUtils.trimBlank(ct.toString()));
    }

    @Test
    public void test10() {
        CharTable ct = new CharTable();
        ct.addTitle("测试1-");
        ct.addTitle("测试2");
        ct.addTitle("测试3");
        ct.addCell("1");
        ct.addCell("2\n3\n5");
        ct.addCell("3\n4");
        ct.addCell("s");
        ct.addCell("k");
        ct.addCell("m");
        ct.addCell("o");
        ct.addCell("p\r\nq\ns\ni");
        ct.addCell("p");
        ct.toString(CharTable.Style.SIMPLE);

        String r = "";
        r += "1       2      3    " + Settings.LINE_SEPARATOR;
        r += "        3      4    " + Settings.LINE_SEPARATOR;
        r += "        5           " + Settings.LINE_SEPARATOR;
        r += "s       k      m    " + Settings.LINE_SEPARATOR;
        r += "o       p      p    " + Settings.LINE_SEPARATOR;
        r += "        q           " + Settings.LINE_SEPARATOR;
        r += "        s           " + Settings.LINE_SEPARATOR;
        r += "        i           ";

        Assert.assertEquals(StringUtils.trimBlank(r), StringUtils.trimBlank(ct.toString()));
    }
}
