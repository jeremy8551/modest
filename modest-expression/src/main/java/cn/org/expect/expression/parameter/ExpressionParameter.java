package cn.org.expect.expression.parameter;

import java.util.Date;

import cn.org.expect.util.StringUtils;

/**
 * 表达式数值对象
 *
 * @author jeremy8551@gmail.com
 * @createtime 2014-05-19 15:16:10
 */
public class ExpressionParameter implements Parameter {

    /**
     * 将对象转为表达式参数
     *
     * @param value 对象
     * @return 表达式参数
     */
    public static ExpressionParameter valueOf(Object value) {
        if (value == null) {
            return new ExpressionParameter(Parameter.NULL, null);
        }

        if (value instanceof String) {
            return new ExpressionParameter(Parameter.STRING, value);
        }

        if (value instanceof Number) {
            String str = value.toString();
            if (str.indexOf('.') == -1) {
                return new ExpressionParameter(Parameter.LONG, Long.valueOf(str));
            } else {
                return new ExpressionParameter(Parameter.DOUBLE, new Double(str));
            }
        }

        if (value instanceof Boolean) {
            return new ExpressionParameter(Parameter.BOOLEAN, value);
        }

        if (value instanceof Date) {
            return new ExpressionParameter(Parameter.DATE, value);
        }

        if (value.getClass().isArray()) {
            return new ExpressionParameter(Parameter.ARRAY, value);
        }

        return new ExpressionParameter(Parameter.UNKNOWN, value);
    }

    /** 参数类型 */
    protected int type;

    /** 参数值 */
    protected Object value;

    /**
     * 初始化
     */
    public ExpressionParameter() {
        this.type = UNKNOWN;
    }

    /**
     * 初始化
     *
     * @param type  参数类型
     * @param value 参数值
     */
    public ExpressionParameter(int type, Object value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setValue(Object obj) {
        this.value = obj;
    }

    public void execute() {
    }

    public Double doubleValue() {
        return (Double) this.value;
    }

    public Long longValue() {
        return (Long) this.value;
    }

    public String stringValue() {
        return (String) this.value;
    }

    public Boolean booleanValue() {
        return (Boolean) this.value;
    }

    public Date dateValue() {
        return (Date) this.value;
    }

    public Object value() {
        return this.value;
    }

    public Parameter copy() {
        return new ExpressionParameter(this.type, this.value);
    }

    public boolean equals(Object obj) {
        if (obj instanceof Parameter) {
            Parameter parameter = (Parameter) obj;
            if (this.type == parameter.getType()) {
                Object value = parameter.value();

                boolean v1 = this.value == null;
                boolean v2 = value == null;
                if (v1 && v2) {
                    return true;
                } else if (v1 || v2) {
                    return false;
                } else {
                    return this.value.equals(value);
                }
            }
        }
        return false;
    }

    public String toString() {
        String str = "[";
        str += ExpressionParameter.getTypeName(this.type) + ": ";
        str += StringUtils.toString(this.value);
        str += "]";
        return str;
    }

    /**
     * 把数值类型转换为字符串
     *
     * @param type 数值类型
     * @return 类型名
     */
    public static String getTypeName(int type) {
        switch (type) {
            case BOOLEAN:
                return "Boolean";
            case DOUBLE:
                return "Double";
            case EXPRESS:
                return "Express";
            case LONG:
                return "Long";
            case STRING:
                return "String";
            case DATE:
                return "Date";
            case DATE_UNIT:
                return "DateUnit";
            default:
                return "Unknown" + type;
        }
    }
}
