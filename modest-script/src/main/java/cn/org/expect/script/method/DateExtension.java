package cn.org.expect.script.method;

import java.util.Date;

import cn.org.expect.script.annotation.EasyVariableExtension;
import cn.org.expect.util.Dates;

@EasyVariableExtension
public class DateExtension {

    /**
     * 将对象解析为日期时间
     *
     * @param value 对象（字符串、日期、时间、long、日历）
     * @return 日期时间
     */
    public static Date date(Object value) {
        return Dates.parse(value);
    }

    /**
     * 按指定格式打印日期时间
     *
     * @param value   对象（字符串、日期、时间、long、日历）
     * @param pattern 格式, 如: yyyy-MM-dd 详见: SimpleDateFormat
     * @return 字符串
     */
    public static String format(Object value, String pattern) {
        return Dates.format(Dates.testParse(value), pattern);
    }

    /**
     * 返回日期是月份中的第几天
     *
     * @param value 对象（字符串、日期、时间、long、日历）
     * @return 月份中的第几天
     */
    public static int getDay(Object value) {
        return Dates.getDayOfMonth(Dates.parse(value));
    }

    /**
     * 返回日期从1970年开始的第几天
     *
     * @param value 对象（字符串、日期、时间、long、日历）
     * @return 整数
     */
    public static long getDays(Object value) {
        return (Dates.parse(value).getTime() / 86400000) + 1;
    }

    /**
     * 返回小时
     *
     * @param value 对象（字符串、日期、时间、long、日历）
     * @return 小时
     */
    public static int getHour(Object value) {
        return Dates.getHour(Dates.parse(value));
    }

    /**
     * 返回毫秒数
     *
     * @param value 对象（字符串、日期、时间、long、日历）
     * @return 毫秒数
     */
    public static int getMillis(Object value) {
        return Dates.getMillisecond(Dates.parse(value));
    }

    /**
     * 返回分钟
     *
     * @param value 对象（字符串、日期、时间、long、日历）
     * @return 分钟
     */
    public static int getMinute(Object value) {
        return Dates.getMinute(Dates.parse(value));
    }

    /**
     * 返回月份
     *
     * @param value 对象（字符串、日期、时间、long、日历）
     * @return 月份
     */
    public static int getMonth(Object value) {
        return Dates.getMonth(Dates.parse(value));
    }

    /**
     * 返回秒钟
     *
     * @param value 对象（字符串、日期、时间、long、日历）
     * @return 秒钟
     */
    public static int getSecond(Object value) {
        return Dates.getSecond(Dates.parse(value));
    }

    /**
     * 返回年份
     *
     * @param value 对象（字符串、日期、时间、long、日历）
     * @return 年份
     */
    public static int getYear(Object value) {
        return Dates.getYear(Dates.parse(value));
    }
}
