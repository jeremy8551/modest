package cn.org.expect.expression.operation;

import cn.org.expect.expression.parameter.ExpressionParameter;
import cn.org.expect.expression.parameter.Parameter;
import cn.org.expect.util.ResourcesUtils;

/**
 * like
 *
 * @author jeremy8551@gmail.com
 * @createtime 2025-10-07
 */
public class LikeOperator implements Operator {

    public Parameter execute(Parameter d1, Parameter d2) {
        ExpressionParameter data = new ExpressionParameter();

        if (d1.getType() == Parameter.STRING && d2.getType() == Parameter.STRING) {
            data.setType(Parameter.BOOLEAN);
            data.setValue(new Boolean(d1.stringValue().matches(d2.stringValue())));
            return data;
        }

        throw new UnsupportedOperationException(d1 + " like '" + d2 + "'");
    }

    public int getPriority() {
        return 11;
    }

    public String toString() {
        return ResourcesUtils.getMessage("expression.stdout.message008");
    }
}
