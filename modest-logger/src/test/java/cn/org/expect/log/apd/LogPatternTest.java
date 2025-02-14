package cn.org.expect.log.apd;

import java.util.List;

import cn.org.expect.log.LogField;
import cn.org.expect.log.field.AbstractField;
import cn.org.expect.log.field.CategoryField;
import cn.org.expect.log.field.ClassNameField;
import cn.org.expect.log.field.ConstantField;
import cn.org.expect.log.field.DateField;
import cn.org.expect.log.field.FileField;
import cn.org.expect.log.field.LineField;
import cn.org.expect.log.field.LinenoField;
import cn.org.expect.log.field.MDCField;
import cn.org.expect.log.field.MessageField;
import cn.org.expect.log.field.MethodField;
import cn.org.expect.log.field.NewlineField;
import cn.org.expect.log.field.PriorityField;
import cn.org.expect.log.field.ProcessidField;
import cn.org.expect.log.field.RelativeField;
import cn.org.expect.log.field.ThreadName;
import cn.org.expect.log.field.ThrowableField;
import cn.org.expect.log.internal.LogFieldAlign;
import cn.org.expect.log.internal.LogPattern;
import org.junit.Assert;
import org.junit.Test;

public class LogPatternTest {

    @Test
    public void test1() {
        LogPattern pattern = new LogPattern("");
        List<LogField> list = pattern.getFields();
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void test2() {
        LogPattern pattern = new LogPattern(" ");
        List<LogField> list = pattern.getFields();
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void test3() {
        LogPattern pattern = new LogPattern("%d %m %n");
        List<LogField> list = pattern.getFields();
        Assert.assertEquals(DateField.class, list.get(0).getClass());
        Assert.assertEquals(MessageField.class, list.get(2).getClass());
        Assert.assertEquals(NewlineField.class, list.get(4).getClass());
    }

    @Test
    public void test4() {
        LogPattern pattern = new LogPattern("------- %% %d %p %processId %l %level %100.200ex %10.10c{10} %-1.10F %.100L %10r %-3.10C %.40t %-10.100X{test} %.22M %100m %n");
        List<LogField> list = pattern.getFields();
        Assert.assertEquals(32, list.size());

        int i = 0;
        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());
        Assert.assertEquals(DateField.class, list.get(i++).getClass());
        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());
        Assert.assertEquals(PriorityField.class, list.get(i++).getClass());
        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());
        Assert.assertEquals(ProcessidField.class, list.get(i++).getClass());
        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());
        Assert.assertEquals(LineField.class, list.get(i++).getClass());
        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());
        Assert.assertEquals(PriorityField.class, list.get(i++).getClass());
        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());

        checkField(list.get(i++), ThrowableField.class, 100, 200, true);

        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());

        checkField(list.get(i++), CategoryField.class, 10, 10, true);

        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());

        checkField(list.get(i++), FileField.class, 1, 10, false);

        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());

        checkField(list.get(i++), LinenoField.class, -1, 100, true);

        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());

        checkField(list.get(i++), RelativeField.class, 10, -1, true);

        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());

        checkField(list.get(i++), ClassNameField.class, 3, 10, false);

        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());

        checkField(list.get(i++), ThreadName.class, -1, 40, true);

        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());

        checkField(list.get(i++), MDCField.class, 10, 100, false);

        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());

        checkField(list.get(i++), MethodField.class, -1, 22, true);

        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());

        checkField(list.get(i++), MessageField.class, 100, -1, true);

        Assert.assertEquals(ConstantField.class, list.get(i++).getClass());
        Assert.assertEquals(NewlineField.class, list.get(i).getClass());
    }

    public static void checkField(LogField field, Class<?> type, int min, int max, boolean right) {
        AbstractField f = (AbstractField) field;
        Assert.assertEquals(type, field.getClass());
        LogFieldAlign align = f.getAlign();
        Assert.assertEquals(min, align.getMin());
        Assert.assertEquals(max, align.getMax());
        Assert.assertEquals(right, align.isRight());
    }
}
