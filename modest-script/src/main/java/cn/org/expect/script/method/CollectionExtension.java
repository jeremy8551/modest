package cn.org.expect.script.method;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.org.expect.script.annotation.EasyVariableExtension;

@EasyVariableExtension
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class CollectionExtension {

    /**
     * 返回集合长度
     *
     * @param collection 集合
     * @return 集合长度
     */
    public static int size(Collection collection) {
        return collection.size();
    }

    /**
     * 返回集合长度
     *
     * @param map 集合
     * @return 集合长度
     */
    public static int size(Map map) {
        return map.size();
    }

    /**
     * 在集合中添加元素
     *
     * @param collection 集合
     * @param object     元素
     * @return 返回true表示成功，false表示失败
     */
    public static boolean add(Collection collection, Object object) {
        return collection.add(object);
    }

    /**
     * 返回集合中某个位置上的元素
     *
     * @param list  集合
     * @param index 位置信息，从 0 开始
     * @return 集合元素
     */
    public static Object get(List list, int index) {
        if (index < 0 || index >= list.size()) {
            return null;
        } else {
            return list.get(index);
        }
    }

    /**
     * @Ignore 因为脚本引擎中 int 使用 long 存储，所以定义重载方法
     */
    public static Object get(List list, long index) {
        return get(list, (int) index);
    }
}
