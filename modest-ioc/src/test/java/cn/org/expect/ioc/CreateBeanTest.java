package cn.org.expect.ioc;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.util.ClassUtils;
import org.junit.Assert;
import org.junit.Test;

public class CreateBeanTest {

    @Test
    public void test1() {
        DefaultEasyContext context = new DefaultEasyContext("sout:info", ClassUtils.getPackageName(CreateBeanTest.class, 3));
        Word instance = context.getBean(Word.class, 10, "testName", (long) 1000, context, 91, new Boolean(true), new int[]{1, 2, 3});
        Assert.assertEquals(10, instance.getNo());
        Assert.assertEquals("testName", instance.getName());
        Assert.assertEquals(Long.valueOf(1000), instance.getTest());
        Assert.assertEquals(context, instance.getContext());
        Assert.assertEquals(new Integer(91), instance.getI1());
        Assert.assertEquals(3, instance.getArray().length);
        Assert.assertTrue(instance.isaBoolean());
    }

    @Test
    public void test2() {
        DefaultEasyContext context = new DefaultEasyContext("sout:info", ClassUtils.getPackageName(CreateBeanTest.class, 3));
        TestContext instance = context.getBean(TestContext.class);
        Assert.assertNotNull(instance);
    }

    @EasyBean
    public static class Word {

        private int no;
        private String name;
        private Long test;
        private EasyContext context;
        private Integer i1;
        private boolean aBoolean;
        private int[] array;

        public Word(int no, String name, Long test, EasyContext context, Integer i1, boolean aBoolean, int[] array) {
            this.no = no;
            this.name = name;
            this.test = test;
            this.context = context;
            this.i1 = i1;
            this.aBoolean = aBoolean;
            this.array = array;
        }

        public int getNo() {
            return no;
        }

        public String getName() {
            return name;
        }

        public Long getTest() {
            return test;
        }

        public EasyContext getContext() {
            return context;
        }

        public Integer getI1() {
            return i1;
        }

        public boolean isaBoolean() {
            return aBoolean;
        }

        public int[] getArray() {
            return array;
        }
    }

    @EasyBean
    public static class TestContext {

        private final EasyContext context;

        public TestContext(EasyContext context) {
            this.context = context;
        }

        public EasyContext getContext() {
            return context;
        }
    }
}
