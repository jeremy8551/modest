package cn.org.expect.expression;

import cn.org.expect.util.StringUtils;

/**
 * 时间表达式
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/9/20
 */
public class MillisExpression {

    private final long value;

    /**
     * 将字符串转为毫秒 <br>
     * 如： <br>
     * 1 hour == 3600000 <br>
     * 1 min == 60000 <br>
     * 1 sec == 1000
     *
     * @param expression 时间表达式
     */
    public MillisExpression(String expression) {
        this.value = this.parse(expression);
    }

    /**
     * 返回表达式所表示的毫秒值
     *
     * @return 毫秒值
     */
    public long value() {
        return value;
    }

    /**
     * 将字符串转为毫秒 <br>
     * 如： <br>
     * 1 hour == 3600000 <br>
     * 1 min == 60000 <br>
     * 1 sec == 1000
     *
     * @param time 时间表达式
     * @return 时间表达式转为毫秒数
     */
    protected long parse(String time) {
        if (StringUtils.isNotBlank(time)) {
            time = time.toLowerCase();
            time = StringUtils.replaceAll(time, "millis", "");
            time = StringUtils.replaceAll(time, "seconds", " * 1000");
            time = StringUtils.replaceAll(time, "second", " * 1000");
            time = StringUtils.replaceAll(time, "sec", " * 1000");
            time = StringUtils.replaceAll(time, "s", " * 1000");
            time = StringUtils.replaceAll(time, "minutes", " * 60 * 1000");
            time = StringUtils.replaceAll(time, "minute", " * 60 * 1000");
            time = StringUtils.replaceAll(time, "min", " * 60 * 1000");
            time = StringUtils.replaceAll(time, "m", " * 60 * 1000");
            time = StringUtils.replaceAll(time, "hour", " * 3600 * 1000");
            time = StringUtils.replaceAll(time, "hou", " * 3600 * 1000");
            time = StringUtils.replaceAll(time, "h", " * 3600 * 1000");
            time = StringUtils.replaceAll(time, "day", " * 3600 * 1000 * 24");
            return new Expression(time).longValue();
        } else {
            return 0;
        }
    }
}
