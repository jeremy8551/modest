package cn.org.expect.expression.operation;

import cn.org.expect.expression.ExpressionException;
import cn.org.expect.expression.parameter.Parameter;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;

/**
 * not in, not like, !=
 *
 * @author jeremy8551@gmail.com
 * @createtime 2014-05-19 23:03:18
 */
public class NotOperator implements Operator {

    /** 操作 */
    protected Operator operator;

    /** 表达式 */
    protected String expression;

    /** 取反符号在表达式中的位置 */
    protected int index;

    public NotOperator(EqualsOperator operator) {
        this.operator = Ensure.notNull(operator);
    }

    public NotOperator(String expression, int index) {
        this.expression = expression;
        this.index = index;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Operator getOperator() {
        return operator;
    }

    public Parameter execute(Parameter d1, Parameter d2) {
        if (this.operator == null) {
            throw new ExpressionException("expression.stdout.message061", this.expression, this.index);
        }

        Parameter result = this.operator.execute(d1, d2);
        result.setValue(!result.booleanValue());
        return result;
    }

    public int getPriority() {
        if (this.operator == null) {
            throw new ExpressionException("expression.stdout.message061", this.expression, this.index);
        }

        return this.operator.getPriority();
    }

    public String toString() {
        return ResourcesUtils.getMessage("expression.stdout.message015", this.operator);
    }
}
