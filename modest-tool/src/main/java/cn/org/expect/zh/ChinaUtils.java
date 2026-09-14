package cn.org.expect.zh;

import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;

/**
 * 中国/中文的帮助类
 *
 * @author jeremy8551@gmail.com
 */
public class ChinaUtils {

    /**
     * 统一社会信用代码加权因子
     */
    private final static int[] UNIFORM_SOCIAL_CREDITCODE_ARRAY = new int[]{1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};

    /**
     * 统一社会信用代码权重
     */
    private final static String UNIFORM_SOCIAL_CREDITCODE = "0123456789ABCDEFGHJKLMNPQRTUWXY";

    /**
     * 身份证校验用到的常数
     */
    public final static int[] IDNO_CONSTANTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1};

    /**
     * 身份证校验用到的常数
     */
    public final static char[] IDNO_PARITYBIT = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    public ChinaUtils() {
    }

    /**
     * 校验15位和18位身份证是否合法
     *
     * @param idCard 15位或18位身份证号
     * @return true表示身份证合法
     */
    public static boolean isIdCard(String idCard) {
        if (idCard == null) {
            return false;
        }

        int length = idCard.length();
        if (length == 18) { // 18位身份证
            return isIdCard18(idCard);
        } else if (length == 15) { // 15位身份证
            String id18no = idCard15to18(idCard);
            return ChinaUtils.isIdCard18(id18no);
        } else {
            return false;
        }
    }

    /**
     * 校验18位身份证号
     *
     * @param idCard 18位身份证号
     * @return 返回true表示合法身份证
     */
    public static boolean isIdCard18(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return false;
        }

        String date = idCard.substring(6, 14); // 出生日期
        if (Dates.testParse(date) == null) {
            return false;
        }

        int j = 0;
        for (int i = 0; i < idCard.length() - 1; i++) {
            try {
                j = j + Integer.parseInt(String.valueOf(idCard.charAt(i))) * IDNO_CONSTANTS[i];
            } catch (Throwable e) {
                return false;
            }
        }
        j %= 11;
        return idCard.charAt(idCard.length() - 1) == IDNO_PARITYBIT[j];
    }

    /**
     * 把15位身份证转为18位身份证
     *
     * @param idCard 15位身份证号
     * @return 18位身份证号; 返回null表示输入参数idCard非法
     */
    public static String idCard15to18(String idCard) {
        if (idCard == null || idCard.length() != 15) {
            return null;
        }

        try {
            String id17no = idCard.substring(0, 6) + "19" + StringUtils.right(idCard, 9);
            int j = 0;
            for (int i = 0; i < id17no.length(); i++) {
                j = j + Integer.parseInt(id17no.substring(i, i + 1)) * IDNO_CONSTANTS[i];
            }
            j %= 11;
            return id17no + IDNO_PARITYBIT[j];
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 校验统一社会信用代码编号 <br>
     * 营业执照号、组织机构号、税务号三证合一为18位的统一社会信用代码编号 <br>
     * 统一社会信用代码编号从右边倒数第二个字符开始向左数9个字符表示组织机构号 <br>
     * 统一社会信用代码编号中英文字母全部大写 <br>
     * 统一社会信用代码编号前17位字母校验位第18个字母 <br>
     *
     * @param str 统一社会信用代码编号
     * @return true表示统一社会信用代码合法 false表示统一社会信用代码不合法
     */
    public static boolean isUniformSocialCreditCode(String str) {
        if (str == null || str.length() != 18) {
            return false;
        }

        int ret = 0;
        for (int i = 0; i < str.length() - 1; i++) {
            char c = str.charAt(i);
            int pos = UNIFORM_SOCIAL_CREDITCODE.indexOf(c);
            ret += (pos * UNIFORM_SOCIAL_CREDITCODE_ARRAY[i]); // 权重与加权因子相乘之和
        }

        int checkCode = 31 - ret % 31;
        if (checkCode == 31) {
            checkCode = 0;
        }
        checkCode = UNIFORM_SOCIAL_CREDITCODE.charAt(checkCode);
        return str.charAt(17) == checkCode;
    }
}
