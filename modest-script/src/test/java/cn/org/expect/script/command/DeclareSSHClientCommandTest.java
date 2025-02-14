package cn.org.expect.script.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class DeclareSSHClientCommandTest {

    @Test
    public void test() {
        String str = "declare name SSH client for connect to name@host:port?password=str";
        Pattern compile = Pattern.compile(DeclareSSHClientCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(2, matcher.groupCount());
        Assert.assertEquals("name", matcher.group(1));
        Assert.assertEquals("name@host:port?password=str", matcher.group(2));
    }

    @Test
    public void test1() {
        String str = "declare name SSH client for connect to name@host:port?password=str;";
        Pattern compile = Pattern.compile(DeclareSSHClientCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(2, matcher.groupCount());
        Assert.assertEquals("name", matcher.group(1));
        Assert.assertEquals("name@host:port?password=str", matcher.group(2));
    }

    @Test
    public void test2() {
        String str = "declare name SSH client for connect to name@host:port?password=str ; ";
        Pattern compile = Pattern.compile(DeclareSSHClientCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(2, matcher.groupCount());
        Assert.assertEquals("name", matcher.group(1));
        Assert.assertEquals("name@host:port?password=str", matcher.group(2));
    }
}
