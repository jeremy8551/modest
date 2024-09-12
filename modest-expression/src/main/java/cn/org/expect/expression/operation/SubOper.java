package cn.org.expect.expression.operation;

import java.util.Date;

import cn.org.expect.expression.ExpressionException;
import cn.org.expect.expression.parameter.DateUnitParameter;
import cn.org.expect.expression.parameter.ExpressionParameter;
import cn.org.expect.expression.parameter.Parameter;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Numbers;
import cn.org.expect.util.ResourcesUtils;

/**
 * 减法运算
 *
 * @author jeremy8551@qq.com
 * @createtime 2014-05-16 20:30:30
 */
public class SubOper implements Operator {

    public Parameter execute(Parameter d1, Parameter d2) {
        ExpressionParameter data = new ExpressionParameter();
        if (d1.getType() == Parameter.DOUBLE && d2.getType() == Parameter.DOUBLE) {
            data.setType(Parameter.DOUBLE);
            data.setValue(Numbers.subtract(d1.doubleValue(), d2.doubleValue()));
            return data;
        }
        if (d1.getType() == Parameter.DOUBLE && d2.getType() == Parameter.LONG) {
            data.setType(Parameter.DOUBLE);
            data.setValue(Numbers.subtract(d1.doubleValue(), Double.valueOf(d2.longValue().doubleValue())));
            return data;
        }
        if (d1.getType() == Parameter.LONG && d2.getType() == Parameter.DOUBLE) {
            data.setType(Parameter.DOUBLE);
            data.setValue(Numbers.subtract(Double.valueOf(d1.longValue().doubleValue()), d2.doubleValue()));
            return data;
        }
        if (d1.getType() == Parameter.LONG && d2.getType() == Parameter.LONG) {
            data.setType(Parameter.LONG);
            data.setValue(Numbers.subtract(d1.longValue(), d2.longValue()));
            return data;
        }
        if (d1.getType() == Parameter.LONG && d2.getType() == Parameter.DATEUNIT) { // 19000101 - 1 month
            Date date = Dates.testParse(d1.longValue());
            if (date == null) {
                throw new ExpressionException(ResourcesUtils.getMessage("expression.standard.output.msg062", d1.stringValue()));
            }
            DateUnitParameter p = (DateUnitParameter) d2;
            data.setType(Parameter.DATE);
            data.setValue(Dates.calcDay(date, p.getUnit(), -p.longValue().intValue()));
            return data;
        }
        if (d1.getType() == Parameter.STRING && d2.getType() == Parameter.DATEUNIT) {
            Date date = Dates.testParse(d1.value());
            if (date == null) {
                throw new ExpressionException(ResourcesUtils.getMessage("expression.standard.output.msg062", d1.stringValue()));
            }
            DateUnitParameter p = (DateUnitParameter) d2;
            data.setType(Parameter.DATE);
            data.setValue(Dates.calcDay(date, p.getUnit(), -p.longValue().intValue()));
            return data;
        }
        if (d1.getType() == Parameter.DATE && d2.getType() == Parameter.DATEUNIT) {
            Date date = d1.dateValue();
            DateUnitParameter p = (DateUnitParameter) d2;
            data.setType(Parameter.DATE);
            data.setValue(Dates.calcDay(date, p.getUnit(), -p.longValue().intValue()));
            return data;
        }

        throw new UnsupportedOperationException(d1 + " - " + d2);
    }

    public int getPriority() {
        return 4;
    }

    public String toString() {
        return ResourcesUtils.getMessage("expression.standard.output.msg020");
    }
}
