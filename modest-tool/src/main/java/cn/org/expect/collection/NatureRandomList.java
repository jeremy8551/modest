package cn.org.expect.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * 自然方式访问数据
 * 特点如下：
 * 固定长度的集合 （添加数据元到List 不会自动扩展容量，需要调用 {@linkplain RandomAccessList#expandCapacity(int)}} 方法分配大小）
 * 添加值时自动从 1 开始 （而不是0）;
 * 访问值时自动从 1 开始 （而不是0）;
 *
 * @param <E>
 * @author jeremy8551@gmail.com
 */
public class NatureRandomList<E> extends RandomAccessList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    private final static long serialVersionUID = 1L;

    /**
     * 初始化
     *
     * @param size 初始大小
     */
    public NatureRandomList(int size) {
        super(size + 1);
        this.size = 1;
    }

    public boolean add(E element) {
        return super.add(element);
    }

    public void add(int index, E element) {
        if (index == 0) {
            throw new IllegalArgumentException();
        } else {
            super.add(index, element);
        }
    }

    public boolean addAll(Collection<? extends E> c) {
        if (c == null) {
            return true;
        }

        boolean modified = false;
        Iterator<? extends E> e = c.iterator();
        while (e.hasNext()) {
            if (super.add(e.next())) {
                modified = true;
            }
        }
        return modified;
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        if (index == 0) {
            throw new IllegalArgumentException();
        }
        if (c == null) {
            return true;
        }

        boolean modified = false;
        Iterator<? extends E> e = c.iterator();
        while (e.hasNext()) {
            super.add(index++, e.next());
            modified = true;
        }
        return modified;
    }

    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex == 0 || toIndex == 0) {
            throw new IllegalArgumentException();
        } else {
            return new RandomAccessSubList<E>(this, fromIndex, toIndex);
        }
    }

    public ListIterator<E> listIterator(int index) {
        if (index == 0) {
            throw new IllegalArgumentException();
        } else {
            return new NatureDefaultListItr(index);
        }
    }

    public ListIterator<E> listIterator() {
        return this.isEmpty() ? this.listIterator(0) : this.listIterator(1);
    }

    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> e = iterator();
        while (e.hasNext()) {
            if (!c.contains(e.next())) {
                e.remove();
                modified = true;
            }
        }
        return modified;
    }

    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        Iterator<?> e = this.iterator();
        while (e.hasNext()) {
            if (c.contains(e.next())) {
                e.remove();
                modified = true;
            }
        }
        return modified;
    }

    public int size() {
        return this.size - 1;
    }

    public void clear() {
        this.modCount++;
        for (int i = 1; i < size; i++) {
            this.elementData[i] = null;
        }
        this.size = 1;
    }

    public NatureRandomList<E> clone() {
        NatureRandomList<E> list = new NatureRandomList<E>(this.size());
        System.arraycopy(this.elementData, 0, list.elementData, 0, this.size);
        list.size = this.size;
        list.modCount = 0;
        return list;
    }

    public boolean containsAll(Collection<?> c) {
        for (Iterator<?> it = c.iterator(); it.hasNext(); ) {
            Object key = it.next();
            if (!this.contains(key)) {
                return false;
            }
        }
        return true;
    }

    public Iterator<E> iterator() {
        return new DefaultItr(1);
    }

    public boolean contains(Object elem) {
        return this.indexOf(elem) >= 0;
    }

    public E get(int index) {
        return this.elementData[index];
    }

    public int indexOf(Object elem) {
        if (elem == null) {
            for (int i = 1; i < this.size; i++) {
                if (this.elementData[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 1; i < this.size; i++) {
                if (elem.equals(this.elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean isEmpty() {
        return this.size <= 1;
    }

    public int lastIndexOf(Object elem) {
        if (elem == null) {
            for (int i = this.size - 1; i >= 1; i--) {
                if (this.elementData[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = this.size - 1; i >= 1; i--) {
                if (elem.equals(this.elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public E remove(int index) {
        if (index == 0) {
            throw new IllegalArgumentException();
        } else {
            return super.remove(index);
        }
    }

    public boolean remove(Object o) {
        if (o == null) {
            for (int index = 1; index < this.size; index++) {
                if (this.elementData[index] == null) {
                    fastRemove(index);
                    return true;
                }
            }
        } else {
            for (int index = 1; index < this.size; index++) {
                if (o.equals(this.elementData[index])) {
                    fastRemove(index);
                    return true;
                }
            }
        }
        return false;
    }

    public E set(int index, E element) {
        if (index == 0) {
            throw new IllegalArgumentException();
        } else {
            return super.set(index, element);
        }
    }

    public Object[] toArray() {
        int length = this.size();
        Object[] array = new Object[length];
        System.arraycopy(this.elementData, 1, array, 0, length);
        return array;
    }

    public <T> T[] toArray(T[] array) {
        int length = Math.min(this.size(), array.length); // 最小长度作为复制长度
        System.arraycopy(this.elementData, 1, array, 0, length);
        return array;
    }

    public boolean equals(Object o) {
        return super.equals(o);
    }

    protected class NatureDefaultListItr extends DefaultListItr implements ListIterator<E> {

        NatureDefaultListItr(int index) {
            super(index);
        }

        public boolean hasNext() {
            return cursor != NatureRandomList.this.size();
        }
    }
}
