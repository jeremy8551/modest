package cn.org.expect.expression.operation;

import cn.org.expect.expression.parameter.ArrayParameter;
import cn.org.expect.expression.parameter.ExpressionParameter;
import cn.org.expect.expression.parameter.Parameter;
import cn.org.expect.util.Numbers;
import cn.org.expect.util.ResourcesUtils;

public class InOperator implements Operator {

    public Parameter execute(Parameter d1, Parameter d2) {
        if (Numbers.inArray(d1.getType(), Parameter.STRING, Parameter.DATE, Parameter.DOUBLE, Parameter.LONG) && d2.getType() == Parameter.ARRAY) {
            ExpressionParameter data = new ExpressionParameter();
            data.setType(Parameter.BOOLEAN);
            data.setValue(((ArrayParameter) d2).exists(d1));
            return data;
        } else {
            throw new UnsupportedOperationException(d1 + " in " + d2);
        }
    }

    public int getPriority() {
        return 7;
    }

    public String toString() {
        return ResourcesUtils.getMessage("expression.stdout.message009");
    }
}
