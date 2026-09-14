package cn.org.expect.util;

import java.util.Comparator;

/**
 * 按照目标数据的业务排序规则比较两个值
 */
public class ReverseComparator<E> implements Comparator<E> {

    private final Comparator<E> comparator;

    /**
     * 初始化 ReverseComparator
     */
    public ReverseComparator(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    /** {@inheritDoc} */
    public int compare(E o1, E o2) {
        return this.comparator.compare(o2, o1);
    }
}
