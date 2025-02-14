package cn.org.expect.script.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class DeclareSSHForwardCommandTest {

    @Test
    public void test() {
        String str = "declare name ssh tunnel use proxy name@proxyHost:proxySSHPort?password=proxyPassword connect to remoteHost:remoteSSHPort";
        Pattern compile = Pattern.compile(DeclareSSHTunnelCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(3, matcher.groupCount());
        Assert.assertEquals("name", matcher.group(1));
        Assert.assertEquals("name@proxyHost:proxySSHPort?password=proxyPassword", matcher.group(2));
        Assert.assertEquals("remoteHost:remoteSSHPort", matcher.group(3));
    }

    @Test
    public void test1() {
        String str = "declare name ssh tunnel use proxy name@proxyHost:proxySSHPort?password=proxyPassword connect to remoteHost:remoteSSHPort;";
        Pattern compile = Pattern.compile(DeclareSSHTunnelCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());

        Assert.assertEquals(3, matcher.groupCount());
        Assert.assertEquals("name", matcher.group(1));
        Assert.assertEquals("name@proxyHost:proxySSHPort?password=proxyPassword", matcher.group(2));
        Assert.assertEquals("remoteHost:remoteSSHPort", matcher.group(3));
    }

    @Test
    public void test11() {
        String str = "declare name ssh tunnel use proxy name@proxyHost:proxySSHPort?password=proxyPassword connect to remoteHost:remoteSSHPort ; ";
        Pattern compile = Pattern.compile(DeclareSSHTunnelCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(3, matcher.groupCount());
        Assert.assertEquals("name", matcher.group(1));
        Assert.assertEquals("name@proxyHost:proxySSHPort?password=proxyPassword", matcher.group(2));
        Assert.assertEquals("remoteHost:remoteSSHPort", matcher.group(3));
    }
}
