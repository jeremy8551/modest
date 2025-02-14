package cn.org.expect.expression.operation;

import cn.org.expect.expression.ExpressionException;
import cn.org.expect.expression.parameter.ExpressionParameter;
import cn.org.expect.expression.parameter.Parameter;
import cn.org.expect.expression.parameter.TwoParameter;
import cn.org.expect.util.ResourcesUtils;

public class TreeOperator implements Operator {

    public Parameter execute(Parameter condition, Parameter d2) {
        condition.execute();
        if (condition.getType() != Parameter.BOOLEAN) {
            throw new ExpressionException("expression.stdout.message035", condition.getType());
        }
        TwoParameter run = (TwoParameter) d2;

        ExpressionParameter data = new ExpressionParameter();
        if (condition.booleanValue().booleanValue()) {
            Parameter trueRun = run.getTrueRun();
            trueRun.execute();
            data.setType(trueRun.getType());
            data.setValue(trueRun.value());
        } else {
            Parameter falseRun = run.getFalseRun();
            falseRun.execute();
            data.setType(falseRun.getType());
            data.setValue(falseRun.value());
        }
        return data;
    }

    public int getPriority() {
        return 13;
    }

    public String toString() {
        return ResourcesUtils.getMessage("expression.stdout.message019");
    }
}
