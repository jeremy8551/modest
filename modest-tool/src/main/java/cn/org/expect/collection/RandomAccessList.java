package cn.org.expect.collection;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * 固定长度的 List
 * 添加数据元到List 不会自动扩展容量，需要调用 ensureCapacity(length) 方法分配大小
 *
 * @param <E>
 * @author jeremy8551@gmail.com
 */
public class RandomAccessList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    private final static long serialVersionUID = 1L;

    protected int modCount = 0;
    protected E[] elementData;
    protected int size;

    /**
     * 初始化, 默认初始容量10
     */
    public RandomAccessList() {
        this(10);
    }

    /**
     * 初始化
     *
     * @param size 初始容量
     */
    @SuppressWarnings("unchecked")
    public RandomAccessList(int size) {
        if (size < 0) {
            throw new IllegalArgumentException(String.valueOf(size));
        } else {
            this.elementData = (E[]) new Object[size];
        }
    }

    /**
     * 扩展 List 列表长度为 length
     *
     * @param length 列表数组长度
     */
    @SuppressWarnings("unchecked")
    public void expandCapacity(int length) {
        int oldCapacity = this.elementData.length;
        if (length > oldCapacity) {
            this.modCount++;
            Object oldData[] = this.elementData;
            int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < length) {
                newCapacity = length;
            }

            this.elementData = (E[]) new Object[newCapacity];
            System.arraycopy(oldData, 0, this.elementData, 0, size);
        }
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public boolean contains(Object obj) {
        return this.indexOf(obj) >= 0;
    }

    public int indexOf(Object obj) {
        if (obj == null) {
            for (int i = 0; i < this.size; i++) {
                if (this.elementData[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < this.size; i++) {
                if (obj.equals(this.elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int lastIndexOf(Object obj) {
        if (obj == null) {
            for (int i = this.size - 1; i >= 0; i--) {
                if (this.elementData[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = this.size - 1; i >= 0; i--) {
                if (obj.equals(this.elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public E get(int index) {
        return this.elementData[index];
    }

    public E set(int index, E element) {
        E oldValue = this.elementData[index];
        this.elementData[index] = element;
        return oldValue;
    }

    public boolean add(E element) {
        this.modCount++;
        this.elementData[this.size++] = element;
        return true;
    }

    public void add(int index, E element) {
        if (index > this.size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
        }

        // ensureCapacity(size + 1); // Increments modCount!!
        this.modCount++;
        System.arraycopy(this.elementData, index, this.elementData, index + 1, size - index);
        this.elementData[index] = element;
        this.size++;
    }

    public boolean addAll(Collection<? extends E> c) {
        if (c == null) {
            return true;
        }

        Object[] a = c.toArray();
        int numNew = a.length;
        // ensureCapacity(size + numNew); // Increments modCount
        this.modCount++;
        System.arraycopy(a, 0, this.elementData, this.size, numNew);
        size += numNew;
        return numNew != 0;
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        if (index > this.size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
        }

        Object[] a = c.toArray();
        int numNew = a.length;
        this.modCount++;
        // ensureCapacity(size + numNew); // Increments modCount

        int numMoved = size - index;
        if (numMoved > 0) {
            System.arraycopy(this.elementData, index, this.elementData, index + numNew, numMoved);
        }

        System.arraycopy(a, 0, this.elementData, index, numNew);
        size += numNew;
        return numNew != 0;
    }

    public E remove(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
        }

        this.modCount++;
        E oldValue = this.elementData[index];

        int numMoved = this.size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(this.elementData, index + 1, this.elementData, index, numMoved);
        }
        this.elementData[--this.size] = null;
        return oldValue;
    }

    public boolean remove(Object obj) {
        if (obj == null) {
            for (int index = 0; index < this.size; index++)
                if (this.elementData[index] == null) {
                    this.fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < this.size; index++)
                if (obj.equals(this.elementData[index])) {
                    this.fastRemove(index);
                    return true;
                }
        }
        return false;
    }

    protected void fastRemove(int index) {
        modCount++;
        int numMoved = this.size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(this.elementData, index + 1, this.elementData, index, numMoved);
        }
        this.elementData[--this.size] = null; // Let gc do its work
    }

    public void clear() {
        this.modCount++;
        for (int i = 0; i < this.size; i++) {
            this.elementData[i] = null;
        }
        this.size = 0;
    }

    protected void removeRange(int fromIndex, int toIndex) {
        this.modCount++;
        int numMoved = this.size - toIndex;
        System.arraycopy(this.elementData, toIndex, this.elementData, fromIndex, numMoved);

        int newSize = this.size - (toIndex - fromIndex);
        while (this.size != newSize) {
            this.elementData[--this.size] = null;
        }
    }

    public boolean containsAll(Collection<?> c) {
        for (Object object : c) {
            if (!contains(object)) {
                return false;
            }
        }
        return true;
    }

    public Iterator<E> iterator() {
        return new DefaultItr(0);
    }

    public ListIterator<E> listIterator() {
        return this.listIterator(0);
    }

    public ListIterator<E> listIterator(int index) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        return new DefaultListItr(index);
    }

    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        Iterator<?> it = this.iterator();
        while (it.hasNext()) {
            if (c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> it = this.iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * 返回列表中指定的 fromIndex（包括 ）和 toIndex（不包括）之间的部分视图。
     */
    public List<E> subList(int fromIndex, int toIndex) {
        return (this instanceof RandomAccess ? new RandomAccessSubList<E>(this, fromIndex, toIndex) : new SubList<E>(this, fromIndex, toIndex));
    }

    public Object clone() {
        int length = this.size();
        RandomAccessList<E> list = new RandomAccessList<E>(length);
        System.arraycopy(this.elementData, 0, list.elementData, 0, length);
        list.modCount = 0;
        list.size = length;
        return list;
    }

    public Object[] toArray() {
        Object[] result = new Object[this.size];
        System.arraycopy(this.elementData, 0, result, 0, this.size);
        return result;
    }

    public <T> T[] toArray(T[] array) {
        System.arraycopy(this.elementData, 0, array, 0, this.size);
        return array;
    }

    public String toString() {
        int i = 0;
        StringBuilder buf = new StringBuilder(this.size * 5);
        buf.append(this.getClass().getName());
        buf.append(" ");
        Iterator<E> it = this.iterator();
        while (it.hasNext()) {
            E element = it.next();
            buf.append("${").append(i++).append("}=");
            buf.append(element);
            buf.append(" ");
        }
        return buf.toString();
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }

        Iterator<E> e1 = this.iterator();
        Iterator<E> e2 = ((List<E>) o).iterator();
        while (e1.hasNext() && e2.hasNext()) {
            E o1 = e1.next();
            E o2 = e2.next();
            if (!(o1 == null ? o2 == null : o1.equals(o2))) {
                return false;
            }
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    /**
     * 迭代器
     */
    protected class DefaultItr implements Iterator<E> {

        int cursor;
        int lastRet;
        int expectedModCount;

        public DefaultItr(int size) {
            this.cursor = size;
            this.lastRet = -1;
            expectedModCount = modCount;
        }

        public boolean hasNext() {
            return cursor != RandomAccessList.this.size;
        }

        public E next() {
            checkForComodification();
            try {
                E next = get(cursor);
                lastRet = cursor++;
                return next;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (lastRet == -1) {
                throw new IllegalStateException();
            }

            checkForComodification();
            try {
                RandomAccessList.this.remove(lastRet);
                if (lastRet < cursor) {
                    cursor--;
                }

                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    protected class DefaultListItr extends DefaultItr implements ListIterator<E> {

        public DefaultListItr(int index) {
            super(index);
        }

        public boolean hasNext() {
            return cursor != RandomAccessList.this.size() - 1;
        }

        public E next() {
            return super.next();
        }

        public void remove() {
            super.remove();
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public E previous() {
            checkForComodification();
            try {
                int i = cursor - 1;
                E previous = get(i);
                lastRet = cursor = i;
                return previous;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        public void set(E o) {
            if (lastRet == -1) {
                throw new IllegalStateException();
            }
            checkForComodification();

            try {
                RandomAccessList.this.set(lastRet, o);
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(E o) {
            checkForComodification();

            try {
                RandomAccessList.this.add(cursor++, o);
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
