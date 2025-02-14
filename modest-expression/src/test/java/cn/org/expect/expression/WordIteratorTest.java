package cn.org.expect.expression;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class WordIteratorTest {

    @Test
    public void test() throws IOException {
        WordIterator in = new WordIterator(new BaseAnalysis(), "this string conent is 'test is good' ");
        if (in.isNext("this")) {
            in.assertNext("this");
        }

        Assert.assertTrue(in.hasNext());
        Assert.assertEquals("string", in.next());
        in.mark();

        Assert.assertTrue(in.hasNext());
        Assert.assertEquals("conent", in.next());

        Assert.assertTrue(in.hasNext());
        in.assertNext(new String[]{"is", ""});

        Assert.assertTrue(in.hasNext());
        Assert.assertEquals("'test is good'", in.next());

        in.reset();

        Assert.assertTrue(in.hasNext());
        Assert.assertEquals("conent", in.next());

        Assert.assertTrue(in.hasNext());
        Assert.assertEquals("is", in.next());

        Assert.assertTrue(in.hasNext());
        Assert.assertEquals("'test is good'", in.next());
    }

    @Test
    public void test2() throws IOException {
        WordIterator in = new WordIterator(new BaseAnalysis(), "this string conent is 'test is good' ");
        if (in.isNext("this")) {
            in.assertNext("this");
        }
        Assert.assertTrue(in.hasNext());
        in.assertNext("string");
        Assert.assertEquals(" conent is 'test is good'", in.previewOther());
        Assert.assertEquals("conent", in.previewNext());
        in.assertLast("'test is good'");
        Assert.assertTrue(in.hasNext());
        Assert.assertEquals("conent is", in.readOther());
        in.assertOver();
    }

    @Test
    public void test1() throws IOException {
        WordIterator in = new WordIterator(new BaseAnalysis(), "this string conent is 'test is good' ");

        Assert.assertTrue(in.hasNext());
        Assert.assertEquals(in.next(), "this");

        Assert.assertEquals(in.previewOther(), " string conent is 'test is good'");
        Assert.assertEquals(in.last(), "'test is good'");

        Assert.assertEquals(in.previewOther(), " string conent is");
        Assert.assertEquals(in.last(), "is");
        Assert.assertEquals(in.last(), "conent");
        Assert.assertEquals(in.previewOther(), " string");
    }
}
