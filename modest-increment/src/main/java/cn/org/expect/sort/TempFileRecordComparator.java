package cn.org.expect.sort;

import java.util.Comparator;

import cn.org.expect.io.TableLine;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ReverseComparator;

public class TempFileRecordComparator implements Comparator<TableLine> {

    /** 字段位置的数组，元素都是位置信息，从1开始 */
    private final int[] positions;

    /** 字符串比较方法 */
    private final Comparator<String>[] comparators;

    /** 容量，从0开始 */
    private int count;

    /**
     * 初始化
     */
    @SuppressWarnings("unchecked")
    public TempFileRecordComparator(int size) {
        Ensure.fromZero(size);
        this.positions = new int[size];
        this.comparators = new Comparator[size];
        this.count = 0;
    }

    /**
     * 添加排序字段
     *
     * @param position   字段位置信息，从 1 开始
     * @param comparator 排序规则
     * @param asc        true 表示正序，false 表示倒序
     */
    public void add(int position, Comparator<String> comparator, boolean asc) {
        this.positions[this.count] = position;
        this.comparators[this.count] = asc ? comparator : new ReverseComparator<String>(comparator);
        this.count++;
    }

    public int compare(TableLine record1, TableLine record2) {
        for (int i = 0; i < this.count; i++) {
            int position = this.positions[i];
            String col1 = record1.getColumn(position);
            String col2 = record2.getColumn(position);
            int v = this.comparators[i].compare(col1, col2);
            if (v != 0) {
                return v;
            }
        }
        return 0;
    }
}
