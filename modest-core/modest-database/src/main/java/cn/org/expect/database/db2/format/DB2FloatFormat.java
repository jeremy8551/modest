package cn.org.expect.database.db2.format;

/**
 * 把 Float 转成字符串，格式：+0.00000E+000 <br>
 * 如： <br>
 * 0.0 +0.00000E+000 <br>
 * 1.234567E-11 +1.23457E-011 <br>
 * 1.234567E-10 +1.23457E-010 <br>
 * 1.234567E-9 +1.23457E-009 <br>
 * 1.234567E-8 +1.23457E-008 <br>
 * 1.234567E-7 +1.23457E-007 <br>
 * 1.234567E-6 +1.23457E-006 <br>
 * 1.234567E-5 +1.23457E-005 <br>
 * 1.234567E-4 +1.23457E-004 <br>
 * 0.001234567 +1.23457E-003 <br>
 * 0.01234567 +1.23457E-002 <br>
 * 0.1234567 +1.23457E-001 <br>
 * 1.1234567 +1.12346E+000 <br>
 * 12.345678 +1.23457E+001 <br>
 * 123.45678 +1.23457E+002 <br>
 * 1234.5677 +1.23457E+003 <br>
 * 12345.678 +1.23457E+004 <br>
 * 123456.78 +1.23457E+005 <br>
 * 1234567.8 +1.23457E+006 <br>
 * 1.2345679E7 +1.23457E+007 <br>
 * 1.23456792E8 +1.23457E+008 <br>
 * 1.23456794E9 +1.23457E+009 <br>
 * 1.2345679E31 +1.23457E+031 <br>
 *
 * @author jeremy8551@qq.com
 * @createtime 2012-04-03
 */
public class DB2FloatFormat {
    public final static float[] sizeTable = new float[32];
    public final static float[] pointSize = new float[30];

    static {
        for (int i = 0; i < pointSize.length; i++) {
            StringBuilder str = new StringBuilder("0.");
            for (int j = 0; j < i; j++) {
                str.append("0");
            }
            str.append("9");
            pointSize[i] = new Float(str.toString());
        }

        for (int i = 0; i < sizeTable.length; i++) {
            StringBuilder str = new StringBuilder("9");
            for (int j = 0; j < i; j++) {
                str.append("9");
            }
            sizeTable[i] = new Float(str.toString());
        }
    }

    /**
     * 求整数的位置
     *
     * @param x 整数
     * @return 位置
     */
    static int stringSize(float x) {
        for (int i = 0; ; i++)
            if (x <= sizeTable[i])
                return i + 1;
    }

    /**
     * 返回点号的位置 <br>
     * 0.101f == 1 <br>
     * 0.01 == 2 <br>
     *
     * @param f 数值
     * @return 位置
     */
    static int pointPosition(float f) {
        for (int i = pointSize.length - 1; i >= 0; i--) {
            if (f <= pointSize[i]) {
                return i + 1;
            }
        }
        throw new IllegalArgumentException(Float.toString(f));
    }

    public StringBuilder format(Float f) {
        StringBuilder buf = new StringBuilder(13);
        float val = f;
        if (val == 0.0F) {
            buf.append("+0.00000E+000");
            return buf;
        }

        if (val < 0.0F) {
            val = 0.0F - val;
            buf.append('-');
        } else {
            buf.append('+');
        }
        boolean zf = false;
        int size = stringSize(val) - 1; // 乘以0的个数
        if (val < 1) {
            int pLen = pointPosition(val); // 小数点后0的位数
            size += pLen;
            for (int j = 1; j <= 6 + pLen - 1; j++) {
                val = val * 10;
            }
            zf = true;
        } else {
            if (size < 6) {
                for (int j = 1; j <= 6 - size - 1; j++) {
                    val = val * 10;
                }
            } else {
                for (int j = 1; j <= size - 6 + 1; j++) {
                    val = val / 10;
                }
            }
        }

        buf.append(Math.round(val));
        buf.insert(2, '.');
        buf.append('E');
        if (zf) {
            buf.append('-');
        } else {
            buf.append('+');
        }

        if (size <= 9) {
            buf.append("00");
        } else if (size <= 99) {
            buf.append('0');
        }
        buf.append(size);
        return buf;
    }

}
