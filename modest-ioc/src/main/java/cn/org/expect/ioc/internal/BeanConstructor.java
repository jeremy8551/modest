package cn.org.expect.ioc.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.util.ClassUtils;

/**
 * 组件的构造方法
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/26
 */
public class BeanConstructor {

    /** 所有构造方法 */
    private final List<Constructor<?>> list;

    /** 外部传入的构造方法的参数 */
    private final BeanArgument argument;

    /** 有参构造方法 */
    private Constructor<?> matchConstrucetor;

    /** 无参构造方法 */
    private Constructor<?> baseConstructor;

    /**
     * 初始化
     *
     * @param type     类信息
     * @param argument 外部传入的构造方法参数
     */
    public BeanConstructor(Class<?> type, BeanArgument argument) {
        Constructor<?>[] array = type.getConstructors();
        this.list = new ArrayList<Constructor<?>>(array.length);
        this.argument = argument;
        this.parse(array);
    }

    /**
     * 分析构造方法
     *
     * @param array 构造方法数组
     */
    protected void parse(Constructor<?>[] array) {
        for (Constructor<?> constructor : array) {
            // 必须是public修饰的构造方法
            if (constructor.getModifiers() != Modifier.PUBLIC) {
                continue;
            }

            // 无参构造方法
            Class<?>[] types = constructor.getParameterTypes(); // 构造方法的参数类信息
            if (types.length == 0) {
                this.baseConstructor = constructor;
                continue;
            }

            // 外部参数与构造方法中的参数匹配
            if (types.length == this.argument.size()) {
                if (this.match(types)) {
                    this.matchConstrucetor = constructor;
                } else {
                    this.list.add(0, constructor); // 参数个数匹配的优先级高
                }
                continue;
            }

            this.list.add(constructor);
        }
    }

    /**
     * 判断构造方法参数类型与外部输入参数是否匹配
     *
     * @param array 构造方法的参数类型
     * @return 返回true表示匹配, false表示不匹配
     */
    protected boolean match(Class<?>[] array) {
        for (int i = 0; i < array.length; i++) {
            Class<?> type = array[i];
            if (type == null) {
                continue;
            }

            // 方法中的参数值
            Object value = this.argument.get(i);
            if (value == null) {
                continue;
            }

            Class<?> typeClass = ClassUtils.getReference(type);
            Class<?> valueClass = ClassUtils.getReference(value.getClass());

            if (!typeClass.isAssignableFrom(valueClass)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 返回有参构造方法
     *
     * @return 构造方法
     */
    public Constructor<?> getMatchConstructor() {
        return matchConstrucetor;
    }

    /**
     * 返回无参构造方法
     *
     * @return 构造方法
     */
    public Constructor<?> getBaseConstructor() {
        return baseConstructor;
    }

    /**
     * 返回所有构造方法
     *
     * @return 构造方法
     */
    public List<Constructor<?>> getConstructors() {
        return list;
    }
}
