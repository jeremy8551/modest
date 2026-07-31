package cn.org.expect.io;

import cn.org.expect.util.StringComparator;
import cn.org.expect.util.StringUtils;

/**
 * 按照目标数据的业务排序规则比较两个值
 */
public class TableColumnComparator extends StringComparator {

    /** {@inheritDoc} */
    public int compare(String str1, String str2) {
        String value1 = StringUtils.trimBlank(str1);
        String value2 = StringUtils.trimBlank(str2);
        return value1.compareTo(value2);
    }
}
