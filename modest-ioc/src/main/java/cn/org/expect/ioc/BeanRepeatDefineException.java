package cn.org.expect.ioc;

import java.util.List;

import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * 重复定义组件错误
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/25
 */
public class BeanRepeatDefineException extends IocException {

    /**
     * 重复定义组件错误
     *
     * @param type 组件类
     * @param name 组件名
     * @param list 重复的组件
     */
    public BeanRepeatDefineException(Class<?> type, String name, List<?> list) {
        super("ioc.stdout.message026", type.getName(), name, toString(list));
    }

    /**
     * 将组件信息转为字符串
     *
     * @param list 组件信息集合
     * @return 字符串
     */
    protected static String toString(List<?> list) {
        StringBuilder buf = new StringBuilder();
        for (Object obj : list) {
            buf.append(Settings.LINE_SEPARATOR);
            buf.append(toString(obj));
        }
        return StringUtils.rtrimBlank(buf);
    }

    /**
     * 将实例对象转为字符串
     *
     * @param obj 实例对象
     * @return 字符串
     */
    protected static String toString(Object obj) {
        if (obj instanceof Class) {
            return ((Class<?>) obj).getName();
        }
        if (obj instanceof EasyBeanEntry) {
            return ((EasyBeanEntry) obj).getType().getName();
        }
        return obj.getClass().getName();
    }
}
