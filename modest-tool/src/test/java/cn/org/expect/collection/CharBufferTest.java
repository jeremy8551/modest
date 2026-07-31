package cn.org.expect.collection;

import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * {@link CharBuffer} 的单元测试。
 */
public class CharBufferTest {

    @Test
    public void test() {
        CharBuffer cb = new CharBuffer(20, 10);
        cb.append('0');
        cb.append('1');
        cb.append('2');
        cb.append('3');
        cb.append('4');
        cb.append('5');

//		assertTrue(cb.substring(0, 3).equals("0123"));
        Assert.assertTrue(cb.subSequence(0, 3).equals("012"));

        Assert.assertTrue(cb.substring(1, 1).equals(""));
        Assert.assertTrue(cb.substring(1, 2).equals("1"));
        Assert.assertTrue(cb.substring(0, 2).equals("01"));
        Assert.assertTrue(cb.substring(0, 0).equals(""));

        CharBuffer cb1 = new CharBuffer(20, 10).append("a").append(cb);
        Assert.assertTrue(cb1.toString().equals("a012345"));

        Assert.assertTrue(cb.append("012345", 0, 0).toString().equals("012345"));
        Assert.assertTrue(cb.append("012345", 4, 0).toString().equals("012345"));
        Assert.assertTrue(cb.append("012345", 5, 0).toString().equals("012345"));
        Assert.assertTrue(cb.append("012345", 6, 0).toString().equals("012345"));
        Assert.assertTrue(cb.append("012345", 0, 1).toString().equals("0123450"));
        Assert.assertTrue(cb.append("012345", 0, 2).toString().equals("012345001"));
        Assert.assertTrue(cb.append("012345", 0, 6).toString().equals("012345001012345"));
        Assert.assertEquals(cb.append("012345", 0, 7).toString(), "012345001012345012345");
        Assert.assertEquals(cb.append("012345", 0, 10).toString(), "012345001012345012345012345");

        Assert.assertTrue(cb.contains('5'));
        Assert.assertFalse(cb.contains('l'));

//        assertEquals(cb.rtrim('5').toString(), "01234500101234501234501234");
//        assertEquals(cb.rtrim('3', '4').toString(), "012345001012345012345012");
//        assertEquals(cb.rtrim('1').toString(), "012345001012345012345012");

        cb.clear();
//        assertEquals(cb.rtrim('1').toString(), "");
//        assertEquals(cb.rtrim().toString(), "");

        cb.append(' ').append(StringUtils.FULLWIDTH_BLANK);
//        assertEquals(cb.rtrim().toString(), "");

        cb.clear();
        cb.append("ab").append(' ').append(StringUtils.FULLWIDTH_BLANK);
//        assertEquals(cb.rtrim().toString(), "ab");

        cb.clear();
//        assertEquals(cb.append("a 2345   b").trim('a', 'b'), "2345");
    }

    @Test
    public void testConstructorBoundaries() {
        Assert.assertEquals(0, new CharBuffer(0, 1).length());
        assertIllegalArgument(new Runnable() {
            public void run() {
                new CharBuffer(-1, 1);
            }
        });
        assertIllegalArgument(new Runnable() {
            public void run() {
                new CharBuffer(0, 0);
            }
        });
    }

    @Test
    public void testSetLengthBoundariesAndExceptions() {
        final CharBuffer buffer = new CharBuffer(2, 1).append("ab");
        buffer.setLength(2);
        Assert.assertEquals("ab", buffer.toString());
        buffer.setLength(1);
        Assert.assertEquals("a", buffer.toString());
        buffer.setLength(0);
        Assert.assertTrue(buffer.isEmpty());

        assertIllegalArgument(new Runnable() {
            public void run() {
                buffer.setLength(-1);
            }
        });
        assertIllegalArgument(new Runnable() {
            public void run() {
                buffer.setLength(1);
            }
        });
    }

    @Test
    public void testSubstringEmptyAndBounds() {
        final CharBuffer empty = new CharBuffer(0, 1);
        Assert.assertEquals("", empty.substring(0, 0));

        final CharBuffer buffer = new CharBuffer(1, 1).append('x');
        Assert.assertEquals("", buffer.substring(1, 1));
        assertIllegalArgument(new Runnable() {
            public void run() {
                buffer.substring(-1, 0);
            }
        });
        assertIllegalArgument(new Runnable() {
            public void run() {
                buffer.substring(0, 2);
            }
        });
    }

    @Test
    public void testCapacityOverflowIsRejected() {
        final CharBuffer buffer = new CharBuffer(0, 1);
        buffer.count = Integer.MAX_VALUE;
        assertIllegalArgument(new Runnable() {
            public void run() {
                buffer.expandCapacity(1);
            }
        });
    }

    private static void assertIllegalArgument(Runnable runnable) {
        try {
            runnable.run();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }
}
