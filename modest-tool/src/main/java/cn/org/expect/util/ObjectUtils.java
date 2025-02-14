package cn.org.expect.util;

/**
 * Object帮助类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2011-11-02
 */
public class ObjectUtils {

    /** 空参数 */
    private final static Object[] BLANK = new Object[0];

    /**
     * 返回一个空数组
     *
     * @return 空数组
     */
    public static Object[] of() {
        return BLANK;
    }

    /**
     * 如果参数 {@code obj} 不为null，则返回参数本身，否则返回参数 {@code def}
     *
     * @param obj 对象
     * @param def 默认值
     * @param <E> 对象类型
     * @return 对象或默认值
     */
    public static <E> E coalesce(E obj, E def) {
        return obj == null ? def : obj;
    }
}
