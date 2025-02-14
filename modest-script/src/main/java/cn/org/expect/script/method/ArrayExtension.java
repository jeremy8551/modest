package cn.org.expect.script.method;

import cn.org.expect.script.annotation.EasyVariableExtension;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.StringUtils;

@EasyVariableExtension
public class ArrayExtension {

    /**
     * 检查位置信息是否数组越界
     *
     * @param array 数组
     * @param index 位置信息
     * @return 返回true表示未越界，false表示越界
     */
    public static boolean check(Object[] array, int index) {
        return index >= 0 && index < array.length;
    }

    /**
     * @Ignore 因为脚本引擎中 int 使用 long 存储，所以定义重载方法
     */
    public static boolean check(Object[] array, long index) {
        return check(array, (int) index);
    }

    /**
     * 返回数组长度
     *
     * @param array 数组
     * @return 长度
     */
    public static int length(Object[] array) {
        return array.length;
    }

    /**
     * 删除数组中字符串左侧的空白字符
     *
     * @param array 数组
     * @return 新数组
     */
    public static Object[] ltrim(Object[] array) {
        Object[] result = new Object[array.length];
        for (int i = 0; i < result.length; i++) {
            Object element = array[i];

            if (element instanceof CharSequence) {
                result[i] = StringUtils.ltrimBlank(element);
            } else {
                result[i] = element;
            }
        }
        return result;
    }

    /**
     * 删除数组中字符串左侧的空白字符与指定字符
     *
     * @param array 数组
     * @param chars 待删除的字符
     * @return 新数组
     */
    public static Object[] ltrim(Object[] array, String chars) {
        Object[] result = new Object[array.length];
        for (int i = 0; i < result.length; i++) {
            Object element = array[i];

            if (element instanceof CharSequence) {
                result[i] = StringUtils.ltrimBlank(element, chars.toCharArray());
            } else {
                result[i] = element;
            }
        }
        return result;
    }

    /**
     * 删除数组中字符串右侧的空白字符
     *
     * @param array 数组
     * @return 新数组
     */
    public static Object[] rtrim(Object[] array) {
        Object[] result = new Object[array.length];
        for (int i = 0; i < result.length; i++) {
            Object element = array[i];

            if (element instanceof CharSequence) {
                result[i] = StringUtils.rtrimBlank(element);
            } else {
                result[i] = element;
            }
        }
        return result;
    }

    /**
     * 删除数组中字符串右侧的空白字符与指定字符
     *
     * @param array 数组
     * @param chars 待删除的字符
     * @return 新数组
     */
    public static Object[] rtrim(Object[] array, String chars) {
        Object[] result = new Object[array.length];
        for (int i = 0; i < result.length; i++) {
            Object element = array[i];

            if (element instanceof CharSequence) {
                result[i] = StringUtils.rtrimBlank(element, chars.toCharArray());
            } else {
                result[i] = element;
            }
        }
        return result;
    }

    /**
     * 删除数组中字符串二端的空白字符
     *
     * @param array 数组
     * @return 新数组
     */
    public static Object[] trim(Object[] array) {
        Object[] result = new Object[array.length];
        for (int i = 0; i < result.length; i++) {
            Object element = array[i];

            if (element instanceof CharSequence) {
                result[i] = StringUtils.trimBlank(element);
            } else {
                result[i] = element;
            }
        }
        return result;
    }

    /**
     * 删除数组中字符串二端的空白字符与指定字符
     *
     * @param array 数组
     * @param chars 待删除的字符
     * @return 新数组
     */
    public static Object[] trim(Object[] array, String chars) {
        Object[] result = new Object[array.length];
        for (int i = 0; i < result.length; i++) {
            Object element = array[i];

            if (element instanceof CharSequence) {
                result[i] = StringUtils.trimBlank(element, chars.toCharArray());
            } else {
                result[i] = element;
            }
        }
        return result;
    }

    /**
     * 截取数组
     *
     * @param array 数组
     * @param begin 截取数组的长度
     * @return 新数组
     */
    public static Object[] subArray(Object[] array, int begin) {
        return subArray(array, begin, array.length);
    }

    /**
     * 截取数组
     *
     * @param array 数组
     * @param begin 起始位置，从 0 开始
     * @param end   结束位置（不包括）
     * @return 新数组
     */
    public static Object[] subArray(Object[] array, int begin, int end) {
        return ArrayUtils.subArray(array, begin, end);
    }

    /**
     * @Ignore 因为脚本引擎中 int 使用 long 存储，所以定义重载方法
     */
    public static Object[] subArray(Object[] array, long begin) {
        return subArray(array, (int) begin);
    }

    /**
     * @Ignore 因为脚本引擎中 int 使用 long 存储，所以定义重载方法
     */
    public static Object[] subArray(Object[] array, long begin, long end) {
        return subArray(array, (int) begin, (int) end);
    }
}
