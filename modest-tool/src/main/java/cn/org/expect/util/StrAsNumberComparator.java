package cn.org.expect.util;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * 按照目标数据的业务排序规则比较两个值
 */
public class StrAsNumberComparator implements Comparator<String> {

    /** {@inheritDoc} */
    public int compare(String o1, String o2) {
        return new BigDecimal(StringUtils.trimBlank(o1)).compareTo(new BigDecimal(StringUtils.trimBlank(o2)));
    }
}
