package cn.org.expect.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 提供 12 版本对应的 Java 平台能力适配
 */
public class Java12Dialect extends Java7Dialect {

    public void setField(Object obj, Field field, Object value) {
        try {
            field.setAccessible(true);
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers)) {
                field.set(null, value);
            } else {
                field.set(obj, value);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    public <E> E getField(Object obj, Field field) {
        try {
            field.setAccessible(true);
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers)) {
                return (E) field.get(null);
            } else {
                return (E) field.get(obj);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }
}
