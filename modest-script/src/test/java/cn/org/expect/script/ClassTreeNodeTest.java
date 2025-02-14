package cn.org.expect.script;

import cn.org.expect.log.LogFactory;
import cn.org.expect.script.method.inernal.ClassTreeNode;
import cn.org.expect.util.MessySequence;
import org.junit.Test;

public class ClassTreeNodeTest {

    @Test
    public void test() {
        LogFactory.load(UniversalScriptContext.class.getPackage().getName() + ":debug");
        ClassTreeNode root = new ClassTreeNode(Object.class);
        root.add(Test33Sequence.class);
        root.add(String.class);
        root.add(Integer.class);
        root.add(TestSequence.class);
        root.add(Test2Sequence.class);
        root.add(CharSequence.class);
        root.add(Number.class);
        root.add(TestChildSequence.class);
        root.add(Object[].class);
        root.add(StringBuilder.class);
        root.add(Integer[].class);
        root.add(MessySequence.class);
        root.add(double[].class);
    }

    private static class TestSequence extends MessySequence {

        public TestSequence(String charsetName) {
            super(charsetName);
        }
    }

    private static class Test2Sequence extends MessySequence {

        public Test2Sequence(String charsetName) {
            super(charsetName);
        }
    }

    private static class Test33Sequence extends MessySequence {

        public Test33Sequence(String charsetName) {
            super(charsetName);
        }
    }

    private static class TestChildSequence extends Test2Sequence {

        public TestChildSequence(String charsetName) {
            super(charsetName);
        }
    }
}
