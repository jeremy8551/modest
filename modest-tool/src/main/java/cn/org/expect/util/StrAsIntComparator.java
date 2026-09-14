package cn.org.expect.util;

import java.util.Comparator;

/**
 * 按照目标数据的业务排序规则比较两个值
 */
public class StrAsIntComparator implements Comparator<String> {

    /** {@inheritDoc} */
    public int compare(String o1, String o2) {
        return Integer.parseInt(StringUtils.trimBlank(o1)) - Integer.parseInt(StringUtils.trimBlank(o2));
    }
}
