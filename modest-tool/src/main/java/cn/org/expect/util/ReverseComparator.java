package cn.org.expect.util;

import java.util.Comparator;

public class ReverseComparator<E> implements Comparator<E> {

    private final Comparator<E> comparator;

    public ReverseComparator(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    public int compare(E o1, E o2) {
        return this.comparator.compare(o2, o1);
    }
}
