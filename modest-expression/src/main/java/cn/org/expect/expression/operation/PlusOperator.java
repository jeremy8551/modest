package cn.org.expect.expression.operation;

import cn.org.expect.expression.ExpressionException;
import cn.org.expect.expression.parameter.DateUnitParameter;
import cn.org.expect.expression.parameter.ExpressionParameter;
import cn.org.expect.expression.parameter.Parameter;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Numbers;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 加法运算
 *
 * @author jeremy8551@gmail.com
 * @createtime 2014-05-19 22:58:53
 */
public class PlusOperator implements Operator {

    public Parameter execute(Parameter d1, Parameter d2) {
        ExpressionParameter data = new ExpressionParameter();
        if (d1.getType() == Parameter.STRING && d2.getType() == Parameter.STRING) {
            data.setType(Parameter.STRING);
            data.setValue(new StringBuilder().append(d1.stringValue()).append(d2.stringValue()).toString());
            return data;
        }

        if (d1.getType() == Parameter.STRING && d2.getType() == Parameter.DOUBLE) {
            data.setType(Parameter.STRING);
            data.setValue(new StringBuilder().append(d1.stringValue()).append(String.valueOf(d2.doubleValue().doubleValue())).toString());
            return data;
        }

        if (d1.getType() == Parameter.STRING && d2.getType() == Parameter.LONG) {
            data.setType(Parameter.STRING);
            data.setValue(new StringBuilder().append(d1.stringValue()).append(String.valueOf(d2.longValue().longValue())).toString());
            return data;
        }

        if (d1.getType() == Parameter.STRING && d2.getType() == Parameter.DATE) {
            data.setType(Parameter.STRING);
            data.setValue(new StringBuilder().append(d1.stringValue()).append(StringUtils.toString(d2.dateValue())).toString());
            return data;
        }

        if (d1.getType() == Parameter.STRING && d2.getType() == Parameter.DATE_UNIT) {
            java.util.Date date = Dates.testParse(d1.value());
            if (date == null) {
                throw new ExpressionException("expression.stdout.message044", d1.stringValue());
            }
            DateUnitParameter p = (DateUnitParameter) d2;
            data.setType(Parameter.DATE);
            data.setValue(Dates.calcDay(date, p.getUnit(), p.longValue().intValue()));
            return data;
        }

        if (d1.getType() == Parameter.STRING && d2.getType() == Parameter.NULL) {
            data.setType(Parameter.STRING);
            data.setValue(new StringBuilder().append(d1.stringValue()).append("null").toString());
            return data;
        }

        if (d1.getType() == Parameter.DOUBLE && d2.getType() == Parameter.STRING) {
            data.setType(Parameter.STRING);
            data.setValue(new StringBuilder().append(String.valueOf(d1.doubleValue().doubleValue())).append(d2.stringValue()).toString());
            return data;
        }

        if (d1.getType() == Parameter.DOUBLE && d2.getType() == Parameter.DOUBLE) {
            data.setType(Parameter.DOUBLE);
            data.setValue(Numbers.plus(d1.doubleValue(), d2.doubleValue()));
            return data;
        }

        if (d1.getType() == Parameter.DOUBLE && d2.getType() == Parameter.LONG) {
            data.setType(Parameter.DOUBLE);
            data.setValue(Numbers.plus(d1.doubleValue(), Double.valueOf(d2.longValue().doubleValue())));
            return data;
        }

        if (d1.getType() == Parameter.LONG && d2.getType() == Parameter.STRING) {
            data.setType(Parameter.STRING);
            data.setValue(new StringBuilder().append(String.valueOf(d1.longValue().longValue())).append(d2.stringValue()).toString());
            return data;
        }
        if (d1.getType() == Parameter.LONG && d2.getType() == Parameter.DOUBLE) {
            data.setType(Parameter.DOUBLE);
            data.setValue(Numbers.plus(Double.valueOf(d1.longValue().doubleValue()), d2.doubleValue()));
            return data;
        }

        if (d1.getType() == Parameter.LONG && d2.getType() == Parameter.LONG) {
            data.setType(Parameter.LONG);
            data.setValue(Numbers.plus(d1.longValue(), d2.longValue()));
            return data;
        }

        if (d1.getType() == Parameter.LONG && d2.getType() == Parameter.DATE_UNIT) {
            java.util.Date date = Dates.testParse(d1.longValue());
            if (date == null) {
                throw new ExpressionException("expression.stdout.message044", d1.stringValue());
            }
            DateUnitParameter p = (DateUnitParameter) d2;
            data.setType(Parameter.DATE);
            data.setValue(Dates.calcDay(date, p.getUnit(), p.longValue().intValue()));
            return data;
        }

        if (d1.getType() == Parameter.DATE && d2.getType() == Parameter.DATE_UNIT) {
            java.util.Date date = d1.dateValue();
            DateUnitParameter p = (DateUnitParameter) d2;
            data.setType(Parameter.DATE);
            data.setValue(Dates.calcDay(date, p.getUnit(), p.longValue().intValue()));
            return data;
        }

        if (d1.getType() == Parameter.DATE && d2.getType() == Parameter.STRING) {
            data.setType(Parameter.STRING);
            data.setValue(new StringBuilder().append(StringUtils.toString(d1.dateValue())).append(d2.stringValue()).toString());
            return data;
        }

        if (d1.getType() == Parameter.NULL && d2.getType() == Parameter.STRING) {
            data.setType(Parameter.STRING);
            data.setValue(new StringBuilder().append("null").append(d2.stringValue()).toString());
            return data;
        }

        throw new UnsupportedOperationException(d1 + " + " + d2);
    }

    public int getPriority() {
        return 4;
    }

    public String toString() {
        return ResourcesUtils.getMessage("expression.stdout.message017");
    }
}
