package cn.org.expect.collection;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * 提供集合视图及其边界访问能力
 */
public class SubList<E> extends RandomAccessList<E> {
    private final static long serialVersionUID = 1L;

    private final RandomAccessList<E> list;

    private final int offset;

    private int size;

    private int expectedModCount;

    /**
     * 初始化 SubList
     */
    public SubList(RandomAccessList<E> list, int from, int index) {
        if (from < 0) {
            throw new IndexOutOfBoundsException(String.valueOf(from));
        }
        if (index > list.size) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        if (from > index) {
            throw new IllegalArgumentException(from + ", " + index);
        }

        this.list = list;
        this.offset = from;
        this.size = index - from;
        this.expectedModCount = this.list.modCount;
    }

    public E set(int index, E element) {
        rangeCheck(index);
        checkFor();
        return list.set(index + offset, element);
    }

    public E get(int index) {
        rangeCheck(index);
        checkFor();
        return list.get(index + offset);
    }

    /** {@inheritDoc} */
    public int size() {
        checkFor();
        return size;
    }

    /** {@inheritDoc} */
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        checkFor();
        list.add(index + offset, element);
        expectedModCount = list.modCount;
        size++;
        modCount++;
    }

    /** {@inheritDoc} */
    public E remove(int index) {
        rangeCheck(index);
        checkFor();
        E result = list.remove(index + offset);
        expectedModCount = list.modCount;
        size--;
        modCount++;
        return result;
    }

    /** {@inheritDoc} */
    protected void removeRange(int fromIndex, int toIndex) {
        checkFor();
        list.removeRange(fromIndex + offset, toIndex + offset);
        expectedModCount = list.modCount;
        size -= (toIndex - fromIndex);
        modCount++;
    }

    /** {@inheritDoc} */
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    /** {@inheritDoc} */
    public boolean addAll(int index, Collection<? extends E> c) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        int cSize = c.size();
        if (cSize == 0) {
            return false;
        }

        checkFor();
        list.addAll(offset + index, c);
        expectedModCount = list.modCount;
        size += cSize;
        modCount++;
        return true;
    }

    /** {@inheritDoc} */
    public Iterator<E> iterator() {
        return listIterator();
    }

    /** {@inheritDoc} */
    public ListIterator<E> listIterator(final int index) {
        checkFor();
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        return new ListIterator<E>() {
            private ListIterator<E> i = list.listIterator(index + offset);

            /** {@inheritDoc} */
            public boolean hasNext() {
                return nextIndex() < size;
            }

            /** {@inheritDoc} */
            public E next() {
                if (hasNext()) {
                    return i.next();
                } else {
                    throw new NoSuchElementException();
                }
            }

            /** {@inheritDoc} */
            public boolean hasPrevious() {
                return previousIndex() >= 0;
            }

            /** {@inheritDoc} */
            public E previous() {
                if (hasPrevious()) {
                    return i.previous();
                } else {
                    throw new NoSuchElementException();
                }
            }

            /** {@inheritDoc} */
            public int nextIndex() {
                return i.nextIndex() - offset;
            }

            /** {@inheritDoc} */
            public int previousIndex() {
                return i.previousIndex() - offset;
            }

            /** {@inheritDoc} */
            public void remove() {
                i.remove();
                expectedModCount = list.modCount;
                size--;
                modCount++;
            }

            public void set(E o) {
                i.set(o);
            }

            /** {@inheritDoc} */
            public void add(E o) {
                i.add(o);
                expectedModCount = list.modCount;
                size++;
                modCount++;
            }
        };
    }

    /** {@inheritDoc} */
    public List<E> subList(int fromIndex, int toIndex) {
        return new SubList<E>(this, fromIndex, toIndex);
    }

    private void rangeCheck(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ",Size: " + size);
        }
    }

    private void checkFor() {
        if (list.modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
}
