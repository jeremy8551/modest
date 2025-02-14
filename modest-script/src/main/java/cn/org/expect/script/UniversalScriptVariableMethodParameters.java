package cn.org.expect.script;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 变量方法参数
 */
public interface UniversalScriptVariableMethodParameters {

    /**
     * 判断指定位置上的参数两端是否有引号
     *
     * @param index 参数位置信息，从0开始
     * @return 返回true表示有引号，false表示没有
     */
    boolean isString(int index);

    /**
     * 返回参数
     *
     * @param index 位置信息，从0开始
     * @return 参数值（如果两端有引号，则保留引号）
     */
    String get(int index);

    /**
     * 将参数转为指定类信息的对象
     *
     * @param index 位置信息，从0开始
     * @param type  类信息
     * @return 参数对象
     */
    Object getValue(int index, Class<?> type);

    /**
     * 返回参数
     *
     * @param index 参数位置信息，从0开始
     * @return 参数（两端没有引号）
     */
    String getString(int index);

    /**
     * 返回整数
     *
     * @param index 位置信息，从0开始
     * @return 参数值
     */
    int getInt(int index);

    /**
     * 返回长整数
     *
     * @param index 位置信息，从0开始
     * @return 参数值
     */
    long getLong(int index);

    /**
     * 返回 BigDecimal
     *
     * @param index 位置信息，从0开始
     * @return 参数值
     */
    BigDecimal getDecimal(int index);

    /**
     * 返回日期时间
     *
     * @param index 位置信息，从0开始
     * @return 参数值
     */
    Date getDate(int index);

    /**
     * 返回布尔值
     *
     * @param index 位置信息，从0开始
     * @return 参数值
     */
    boolean getBoolean(int index);

    /**
     * 返回参数个数
     *
     * @return 参数个数
     */
    int size();

    /**
     * 判断参数类型与数组匹配
     *
     * @param types 参数类型数组
     * @return 返回true表示匹配，false表示不匹配
     */
    boolean startsWith(Class<?>[] types);

    /**
     * 判断参数类型与类信息匹配
     *
     * @param type 类信息
     * @param from 起始位置
     * @return 返回true表示匹配，false表示不匹配
     */
    boolean startsWith(Class<?> type, int from);

    /**
     * 判断参数类型是否匹配
     *
     * @param index 位置信息，从0开始
     * @param type  类信息
     * @return 返回true表示匹配，false表示不匹配
     */
    boolean match(int index, Class<?> type);

    /**
     * 判断是否与方法参数匹配
     *
     * @param method 方法信息
     * @return 返回true表示匹配，false表示不匹配
     */
    boolean match(Method method);

    /**
     * 将参数值转为 types 类实例对象，并保存到参数数组 array 中
     *
     * @param types 参数类型
     * @return 参数值数组
     */
    Object[] toArray(Class<?>[] types);

    /**
     * 将参数转为字符串
     *
     * @return 字符串
     */
    String toStandardString();
}
