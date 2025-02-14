package cn.org.expect.script.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class DeclareCatalogCommandTest {

    @Test
    public void test() {
        String str = "declare name catalog configuration use driver com.ibm.db2.jcc.DB2Driver url jdbc:db2://130.1.16.26:50000/username username db2inst1 password db2inst1;";
        Pattern compile = Pattern.compile(DeclareCatalogCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(4, matcher.groupCount());
        Assert.assertEquals("declare", matcher.group(1));
        Assert.assertEquals("", matcher.group(2));
        Assert.assertEquals("name", matcher.group(3));
        Assert.assertEquals("driver com.ibm.db2.jcc.DB2Driver url jdbc:db2://130.1.16.26:50000/username username db2inst1 password db2inst1;", matcher.group(4));
    }

    @Test
    public void test1() {
        String str = "declare name catalog configuration use /home/user/config.properties ; ";
        Pattern compile = Pattern.compile(DeclareCatalogCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(4, matcher.groupCount());
        Assert.assertEquals("declare", matcher.group(1));
        Assert.assertEquals("", matcher.group(2));
        Assert.assertEquals("name", matcher.group(3));
        Assert.assertEquals("/home/user/config.properties ; ", matcher.group(4));
    }

    @Test
    public void test12() {
        String str = "declare global name catalog configuration use /home/user/config.properties ; ";
        Pattern compile = Pattern.compile(DeclareCatalogCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(4, matcher.groupCount());
        Assert.assertEquals("declare", matcher.group(1));
        Assert.assertEquals("global ", matcher.group(2));
        Assert.assertEquals("name", matcher.group(3));
        Assert.assertEquals("/home/user/config.properties ; ", matcher.group(4));
    }

    @Test
    public void test121() {
        String str = "declare global uddb11 catalog configuration use driver ${databaseDriverName} url '${databaseUrl}' username ${username} password ${password}";
        Pattern compile = Pattern.compile(DeclareCatalogCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(4, matcher.groupCount());
        Assert.assertEquals("declare", matcher.group(1));
        Assert.assertEquals("global ", matcher.group(2));
        Assert.assertEquals("uddb11", matcher.group(3));
        Assert.assertEquals("driver ${databaseDriverName} url '${databaseUrl}' username ${username} password ${password}", matcher.group(4));
    }

    @Test
    public void test1212() {
        String str = "declare global uddb11 catalog configuration use driver com.ibm.db2.jcc.DB2Driver url 'jdbc:db2://130.1.16.26:50000/bhcdb' username db2inst1 password db2inst1";
        Pattern compile = Pattern.compile(DeclareCatalogCommandCompiler.REGEX);
        Matcher matcher = compile.matcher(str);
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(4, matcher.groupCount());
        Assert.assertEquals("declare", matcher.group(1));
        Assert.assertEquals("global ", matcher.group(2));
        Assert.assertEquals("uddb11", matcher.group(3));
        Assert.assertEquals("driver com.ibm.db2.jcc.DB2Driver url 'jdbc:db2://130.1.16.26:50000/bhcdb' username db2inst1 password db2inst1", matcher.group(4));
    }
}
