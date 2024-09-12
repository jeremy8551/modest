package cn.org.expect.jdk;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class ReflectTest {

    @Test
    public void test0() {
        System.out.println("choose " + JavaDialectFactory.get().getClass().getName());
    }

    /**
     * 判断属性是否被 static 修饰
     */
    @Test
    public void test1() throws NoSuchFieldException {
        Field[] fields = ReflectTestObject.class.getDeclaredFields();
        for (Field field : fields) {
            if (StringUtils.inArrayIgnoreCase(field.getName(), "n", "n1", "n3")) {
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                Assert.assertTrue(isStatic);
            } else {
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                Assert.assertFalse(isStatic);
            }
        }
    }

    /**
     * 判断属性是否被 final 修饰
     */
    @Test
    public void test2() {
        Field[] fields = ReflectTestObject.class.getDeclaredFields();
        for (Field field : fields) {
            if (StringUtils.inArrayIgnoreCase(field.getName(), "n", "n3")) {
                boolean isFinal = Modifier.isFinal(field.getModifiers());
                Assert.assertTrue(isFinal);
            } else {
                boolean isFinal = Modifier.isFinal(field.getModifiers());
                Assert.assertFalse(isFinal);
            }
        }
    }

    @Test
    public void test3() throws NoSuchFieldException {
        ReflectTestObject lc = new ReflectTestObject();
        System.out.println(JavaDialectFactory.get().getClass().getName());
        Assert.assertEquals("n2", JavaDialectFactory.get().getField(lc, ReflectTestObject.class.getDeclaredField("N2")));
        Assert.assertEquals("n1", JavaDialectFactory.get().getField(lc, ReflectTestObject.class.getDeclaredField("N1")));
        Assert.assertEquals("n3", JavaDialectFactory.get().getField(lc, ReflectTestObject.class.getDeclaredField("N3")));
        Assert.assertEquals("n", JavaDialectFactory.get().getField(lc, ReflectTestObject.class.getDeclaredField("N")));
    }

    @Test
    public void test4() throws NoSuchFieldException {
        ReflectTestObject lc = new ReflectTestObject();
        JavaDialectFactory.get().setField(lc, ReflectTestObject.class.getDeclaredField("N1"), "n11");
        JavaDialectFactory.get().setField(lc, ReflectTestObject.class.getDeclaredField("N2"), "n22");
        Assert.assertEquals("n11", lc.getN1());
        Assert.assertEquals("n22", lc.getN2());
    }

    // 测试修改常量值，JDK8之前可以使用，21之后不行
    @Test
    public void test5() {
        int version = Settings.getJDKVersion();
        if (version <= 8) {
            System.out.println(JavaDialectFactory.get().getClass().getName());
            ReflectTestObject lc = new ReflectTestObject();
            JavaDialectFactory.get().setField(lc, "N.*", String.class, "new");
        }
    }

}
