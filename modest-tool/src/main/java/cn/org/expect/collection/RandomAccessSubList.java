package cn.org.expect.collection;

import java.util.List;

/**
 * 提供集合视图及其边界访问能力
 */
public class RandomAccessSubList<E> extends SubList<E> {
    private final static long serialVersionUID = 1L;

    /**
     * 初始化 RandomAccessSubList
     */
    public RandomAccessSubList(RandomAccessList<E> list, int fromIndex, int toIndex) {
        super(list, fromIndex, toIndex);
    }

    /** {@inheritDoc} */
    public List<E> subList(int fromIndex, int toIndex) {
        return new RandomAccessSubList<E>(this, fromIndex, toIndex);
    }
}
