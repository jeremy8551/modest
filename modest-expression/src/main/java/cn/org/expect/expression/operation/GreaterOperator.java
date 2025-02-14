package cn.org.expect.expression.operation;

import cn.org.expect.expression.parameter.ExpressionParameter;
import cn.org.expect.expression.parameter.Parameter;
import cn.org.expect.util.Numbers;
import cn.org.expect.util.ResourcesUtils;

/**
 * 大于运算
 *
 * @author jeremy8551@gmail.com
 * @createtime 2014-05-19 23:00:24
 */
public class GreaterOperator implements Operator {

    public Parameter execute(Parameter d1, Parameter d2) {
        ExpressionParameter data = new ExpressionParameter();
        if (d1.getType() == Parameter.STRING && d2.getType() == Parameter.STRING) {
            data.setType(Parameter.BOOLEAN);
            data.setValue(d1.stringValue().compareTo(d2.stringValue()) > 0 ? true : false);
            return data;
        }
        if (d1.getType() == Parameter.DOUBLE && d2.getType() == Parameter.DOUBLE) {
            data.setType(Parameter.BOOLEAN);
            data.setValue(Numbers.greater(d1.doubleValue(), d2.doubleValue()));
            return data;
        }
        if (d1.getType() == Parameter.DOUBLE && d2.getType() == Parameter.LONG) {
            data.setType(Parameter.BOOLEAN);
            data.setValue(Numbers.greater(d1.doubleValue(), Double.valueOf(d2.longValue().doubleValue())));
            return data;
        }
        if (d1.getType() == Parameter.LONG && d2.getType() == Parameter.DOUBLE) {
            data.setType(Parameter.BOOLEAN);
            data.setValue(Numbers.greater(Double.valueOf(d1.longValue().doubleValue()), d2.doubleValue()));
            return data;
        }
        if (d1.getType() == Parameter.LONG && d2.getType() == Parameter.LONG) {
            data.setType(Parameter.BOOLEAN);
            data.setValue(Numbers.greater(d1.longValue(), d2.longValue()));
            return data;
        }
        throw new UnsupportedOperationException(d1 + " > " + d2);
    }

    public int getPriority() {
        return 6;
    }

    public String toString() {
        return ResourcesUtils.getMessage("expression.stdout.message022");
    }
}
