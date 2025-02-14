package cn.org.expect.script.method;

import cn.org.expect.script.annotation.EasyVariableExtension;

@EasyVariableExtension
@SuppressWarnings(value = {"unchecked", "rawTypes"})
public class ClassExtension {

    /**
     * 返回对象的类信息
     *
     * @param object 对象
     * @return 类信息
     */
    public static Class<?> getClass(Object object) {
        return object.getClass();
    }

    /**
     * 返回枚举常量
     *
     * @param type 枚举的类信息
     * @param name 枚举常量名
     * @return 枚举常量
     */
    public static Object getEnum(Class<? extends Enum> type, String name) {
        return Enum.valueOf(type, name);
    }
}
