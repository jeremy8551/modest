package cn.org.expect.database.load;

import java.util.List;

/**
 * 数据装载范围
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-07-16
 */
public class LoadFileRange {

    private long start;

    private long end;

    private int status;

    public LoadFileRange(long start, long end, int status) {
        this.start = start;
        this.end = end;
        this.status = status;
    }

    /**
     * 返回起始位置
     *
     * @return 起始位置，从1开始
     */
    public long getStart() {
        return start;
    }

    /**
     * 返回终止位置
     *
     * @return 终止位置，从1开始
     */
    public long getEnd() {
        return end;
    }

    /**
     * 返回数据装载状态
     *
     * @return -1表示还未开始装载数据 0表示正在装载数据 1表示发生错误 2表示装载完毕
     */
    public int getStatus() {
        return status;
    }

    /**
     * 将范围集合专为字符串
     *
     * @param list 范围集合
     * @return 字符串
     */
    public static String toString(List<LoadFileRange> list) {
        if (list == null) {
            return "";
        }

        StringBuilder buf = new StringBuilder();
        for (LoadFileRange range : list) {
            buf.append(range.start).append(",");
            buf.append(range.end).append(",");
            buf.append(range.status).append(";");
        }
        return buf.toString();
    }
}
