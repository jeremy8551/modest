package cn.org.expect.collection;

import java.util.List;

public class RandomAccessSubList<E> extends SubList<E> {
    private final static long serialVersionUID = 1L;

    public RandomAccessSubList(RandomAccessList<E> list, int fromIndex, int toIndex) {
        super(list, fromIndex, toIndex);
    }

    public List<E> subList(int fromIndex, int toIndex) {
        return new RandomAccessSubList<E>(this, fromIndex, toIndex);
    }
}
