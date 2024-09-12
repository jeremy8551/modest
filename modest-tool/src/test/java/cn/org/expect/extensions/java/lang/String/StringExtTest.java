package cn.org.expect.extensions.java.lang.String;

import org.junit.Assert;
import org.junit.Test;

public class StringExtTest {

    @Test
    public void test1() {
        Assert.assertEquals("3", "1,2,3".split(',')[2]);
    }
    
}
