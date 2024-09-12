package cn.org.expect.expression.operation;

import cn.org.expect.expression.parameter.ExpressionParameter;
import cn.org.expect.expression.parameter.Parameter;
import cn.org.expect.util.ResourcesUtils;

/**
 * 或运算
 *
 * @author jeremy8551@qq.com
 * @createtime 2014-05-19 23:00:16
 */
public class OrOper implements Operator {

    public Parameter execute(Parameter d1, Parameter d2) {
        if (d1.getType() != Parameter.BOOLEAN || d2.getType() != Parameter.BOOLEAN) {
            throw new UnsupportedOperationException(d1 + " || " + d2);
        }

        ExpressionParameter data = new ExpressionParameter();
        data.setType(Parameter.BOOLEAN);
        data.setValue(new Boolean(d1.booleanValue() || d2.booleanValue()));
        return data;
    }

    public int getPriority() {
        return 12;
    }

    public String toString() {
        return ResourcesUtils.getMessage("expression.standard.output.msg018");
    }
}
