package cn.org.expect.expression.parameter;

import cn.org.expect.expression.ExpressionException;
import cn.org.expect.util.ResourcesUtils;

/**
 * 布尔数值取反
 *
 * @author jeremy8551@gmail.com
 * @createtime 2025-10-09
 */
public class NegationParameter extends ExpressionParameter {

    /** 表达式 */
    protected String expression;

    /** 取反符号在表达式中的位置 */
    protected int index;

    /** 参数 */
    protected Parameter parameter;

    /**
     * 布尔数值取反
     *
     * @param expression 表达式
     * @param index      取反符号在表达式中的位置
     */
    public NegationParameter(String expression, int index) {
        this.type = Parameter.BOOLEAN;
        this.value = null;
        this.expression = expression;
        this.index = index;
    }

    /**
     * 设置参数
     *
     * @param parameter 参数
     */
    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void execute() {
        if (this.parameter == null) {
            throw new ExpressionException("expression.stdout.message062", this.expression, this.index);
        }

        this.parameter.execute();
        this.value = !this.parameter.booleanValue();
    }

    public Parameter copy() {
        NegationParameter copy = new NegationParameter(this.expression, this.index);
        copy.expression = this.expression;
        copy.index = this.index;
        copy.parameter = this.parameter;
        copy.setType(this.type);
        copy.setValue(this.value);
        return copy;
    }

    public boolean equals(Object obj) {
        if (obj instanceof NegationParameter) {
            NegationParameter var = (NegationParameter) obj;
            if (this.type == var.getType()) {
                Parameter parameter = var.parameter;

                boolean v1 = this.parameter == null;
                boolean v2 = parameter == null;
                if (v1 && v2) {
                    return true;
                } else if (v1 || v2) {
                    return false;
                } else {
                    return this.parameter.equals(parameter);
                }
            }
        }
        return false;
    }

    public String toString() {
        return ResourcesUtils.getMessage("expression.stdout.message007", this.parameter);
    }
}
