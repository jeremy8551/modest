package cn.org.expect.script.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class DeclareProgressCommandTest {

    @Test
    public void test() {
        String str = "declare name progress use step print ' this is text!!  ' total 999 times;";
        Pattern compile = Pattern.compile(DeclareProgressCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());

        Assert.assertEquals(4, matcher.groupCount());
        Assert.assertEquals("name", matcher.group(1).trim());
        Assert.assertEquals("step", matcher.group(2));
        Assert.assertEquals("' this is text!!  '", matcher.group(3));
        Assert.assertEquals("999", matcher.group(4));
    }

    @Test
    public void test1() {
        String str = "declare name progress use step print ' this is text!!  ' total 999 times ";
        Pattern compile = Pattern.compile(DeclareProgressCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());

        Assert.assertEquals(4, matcher.groupCount());
        Assert.assertEquals("name", matcher.group(1).trim());
        Assert.assertEquals("step", matcher.group(2));
        Assert.assertEquals("' this is text!!  '", matcher.group(3));
        Assert.assertEquals("999", matcher.group(4));
    }

    @Test
    public void test11() {
        String str = "declare name progress use step print ' this is text!!  ' total 999 times  ; ";
        Pattern compile = Pattern.compile(DeclareProgressCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());

        Assert.assertEquals(4, matcher.groupCount());
        Assert.assertEquals("name", matcher.group(1).trim());
        Assert.assertEquals("step", matcher.group(2));
        Assert.assertEquals("' this is text!!  '", matcher.group(3));
        Assert.assertEquals("999", matcher.group(4));
    }

    @Test
    public void test111() {
        String str = " declare name progress use step print ' this is text!!  ' total 999 times  ; ";
        Pattern compile = Pattern.compile(DeclareProgressCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());

        Assert.assertEquals(4, matcher.groupCount());
        Assert.assertEquals("name", matcher.group(1).trim());
        Assert.assertEquals("step", matcher.group(2));
        Assert.assertEquals("' this is text!!  '", matcher.group(3));
        Assert.assertEquals("999", matcher.group(4));
    }

    @Test
    public void test1112() {
        String str = "declare progress use out print \"测试进度输出已执行 ${process}%, 总共${totalRecord}个记录${leftTime}\" total 100000 times";
        Pattern compile = Pattern.compile(DeclareProgressCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());

        Assert.assertEquals(4, matcher.groupCount());
        Assert.assertEquals("", matcher.group(1));
        Assert.assertEquals("out", matcher.group(2));
        Assert.assertEquals("\"测试进度输出已执行 ${process}%, 总共${totalRecord}个记录${leftTime}\"", matcher.group(3));
        Assert.assertEquals("100000", matcher.group(4));
    }

    @Test
    public void test11121() {
        String str = "declare global progress use out print \"测试进度输出已执行 ${process}%, 总共${totalRecord}个记录${leftTime}\" total 100000 times";
        Pattern compile = Pattern.compile(DeclareProgressCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());

        Assert.assertEquals(4, matcher.groupCount());
        Assert.assertEquals("global ", matcher.group(1));
        Assert.assertEquals("out", matcher.group(2));
        Assert.assertEquals("\"测试进度输出已执行 ${process}%, 总共${totalRecord}个记录${leftTime}\"", matcher.group(3));
        Assert.assertEquals("100000", matcher.group(4));
    }

    @Test
    public void test111213() {
        String str = "declare global test progress use out print \"测试进度输出已执行 ${process}%, 总共${totalRecord}个记录${leftTime}\" total 100000 times";
        Pattern compile = Pattern.compile(DeclareProgressCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());

        Assert.assertEquals(4, matcher.groupCount());
        Assert.assertEquals("global test ", matcher.group(1));
        Assert.assertEquals("out", matcher.group(2));
        Assert.assertEquals("\"测试进度输出已执行 ${process}%, 总共${totalRecord}个记录${leftTime}\"", matcher.group(3));
        Assert.assertEquals("100000", matcher.group(4));
    }
}
