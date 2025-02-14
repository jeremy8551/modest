package cn.org.expect.expression;

import cn.org.expect.expression.command.CommandOptionValue;
import org.junit.Assert;
import org.junit.Test;

public class CommandOptionValueTest {

    @Test
    public void test() {
        Assert.assertEquals("--name test", new CommandOptionValue("name", "test", true).toString());
        Assert.assertEquals("--name \"1 2\"", new CommandOptionValue("name", "1 2", true).toString());
    }
}
