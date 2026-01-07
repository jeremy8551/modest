package cn.org.expect.expression;

import java.math.BigDecimal;
import java.math.RoundingMode;

import cn.org.expect.util.StringUtils;

/**
 * 计算机中数据单位表达式
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023-09-18
 */
public class DataUnitExpression {

    private BigDecimal value;

    /**
     * 将 1kb 或 1mb, 1gb, 1tb 转为字节数 <br>
     * <p>
     * 将容量字节转为可读性字符串详见 {@linkplain #toString(BigDecimal, boolean)} 方法
     *
     * @param expression 表达式
     */
    public DataUnitExpression(CharSequence expression) {
        this.value = parse(expression);
    }

    public BigDecimal getValue() {
        return value;
    }

    /**
     * 将 1kb 或 1mb, 1gb, 1tb 转为字节数 <br>
     * <p>
     * 将容量字节转为可读性字符串详见 {@linkplain #toString(BigDecimal, boolean)} 方法
     *
     * @param str 字符串
     * @return 总字节数
     */
    public static BigDecimal parse(CharSequence str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }

        String value = str.toString().toUpperCase().replace('B', ' ');
        for (int i = 0; i < "KMGTPEZYND".length(); i++) {
            char c = "KMGTPEZYND".charAt(i);
            StringBuilder req = new StringBuilder();
            for (int j = 0; j <= i; j++) {
                req.append(" * 1024");
            }
            value = StringUtils.replaceAll(value, String.valueOf(c), req.toString());
        }

        return new Expression(value).decimalValue();
    }

    /**
     * 将字节容量转为可读性字符串<br>
     * 1056 == 1.03 KB<br>
     * 663040000 == 632.32 MB<br>
     * 678952960000 == 632.32 GB<br>
     * 695247831040000 == 632.32 TB<br>
     * <p>
     * 将可读性字符串转为容量详见 {@linkplain #parse(CharSequence)} 方法
     *
     * @param value 数值
     * @param isIEC 计算单位：true表示使用 1024 IEC 二进制单位，false表示用 1000 SI十进制单位
     * @return 字符串
     */
    public static String toString(BigDecimal value, boolean isIEC) {
        if (value == null) {
            return null;
        }

        int count = -1;
        BigDecimal x = value;
        BigDecimal m = new BigDecimal(isIEC ? "1024" : "1000");
        while (x.compareTo(m) >= 0) {
            x = x.divide(m, 4, RoundingMode.HALF_UP);
            count++;
        }

        String[] units = isIEC ? new String[]{"B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"} : new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        return x.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + units[count + 1];
    }
}
