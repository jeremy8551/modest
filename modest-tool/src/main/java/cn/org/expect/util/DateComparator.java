package cn.org.expect.util;

import java.util.Comparator;
import java.util.Date;

/**
 * 日期时间比较规则
 *
 * @author jeremy8551@gmail.com
 * @createtime 2017-03-27
 */
public class DateComparator implements Comparator<Date>, Cloneable {

    public final static DateComparator INSTANCE = new DateComparator();

    public int compare(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return 0;
        } else if (date1 == null) {
            return -1;
        } else if (date2 == null) {
            return 1;
        } else {
            return date1.compareTo(date2);
        }
    }

    public DateComparator reversed() {
        return new ReverseComparator();
    }

    public boolean equals(Object obj) {
        return (obj instanceof DateComparator);
    }

    public DateComparator clone() {
        return new DateComparator();
    }

    public String toString() {
        return DateComparator.class.getName();
    }

    protected static class ReverseComparator extends DateComparator {

        public int compare(Date date1, Date date2) {
            return -super.compare(date1, date2);
        }
    }
}
