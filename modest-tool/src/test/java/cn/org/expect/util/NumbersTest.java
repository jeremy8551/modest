package cn.org.expect.util;

import org.junit.Assert;
import org.junit.Test;

public class NumbersTest {

    @Test
    public void test1() {
        Assert.assertTrue(Numbers.plus(Double.valueOf("13767.61"), Double.valueOf("603.04")) == 14370.65);
        Assert.assertTrue(Numbers.subtract(Double.valueOf("13767.61"), Double.valueOf("603.04")) == 13164.57);
        Assert.assertTrue(Numbers.multiply(Double.valueOf("13767.61"), Double.valueOf("34.5")) == 474982.545);
        Assert.assertTrue(Numbers.divide(Double.valueOf("13767.61"), Double.valueOf("2")) == 6883.805);
        Assert.assertTrue(Numbers.mod(Double.valueOf("13.0"), Double.valueOf("2")) == 1);
    }
}
