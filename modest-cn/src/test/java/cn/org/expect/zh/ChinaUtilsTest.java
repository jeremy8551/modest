package cn.org.expect.zh;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.Property;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/2
 */
public class ChinaUtilsTest {
    private final static Log log = LogFactory.getLog(ChinaUtilsTest.class);

    @Test
    public void testTranslateChineseNumberChar() {
        Assert.assertEquals('0', ChinaUtils.replaceChineseNumber('零'));
        Assert.assertEquals('9', ChinaUtils.replaceChineseNumber('九'));
        Assert.assertEquals('9', ChinaUtils.replaceChineseNumber('玖'));
        Assert.assertEquals('1', ChinaUtils.replaceChineseNumber('壹'));
    }

    @Test
    public void testTranslateChineseNumberString() {
        Assert.assertNull(ChinaUtils.replaceChineseNumber(null));
        Assert.assertEquals("1998", ChinaUtils.replaceChineseNumber("一九九八"));
        Assert.assertEquals("1998 2月 9 0", ChinaUtils.replaceChineseNumber("一九九八 贰月 玖 零"));
        Assert.assertEquals(" ", ChinaUtils.replaceChineseNumber(" "));
    }

    @Test
    public void testTranslateTraditionalChineseNumberBigDecimal() {
        Assert.assertNull(ChinaUtils.toChineseNumber(null));
        Assert.assertEquals("壹仟元", ChinaUtils.toChineseNumber(new BigDecimal("1000")));
        Assert.assertEquals("壹拾元", ChinaUtils.toChineseNumber(new BigDecimal("10")));
        Assert.assertEquals("壹拾壹元", ChinaUtils.toChineseNumber(new BigDecimal("11")));
        Assert.assertEquals("捌拾壹元", ChinaUtils.toChineseNumber(new BigDecimal("81")));
        Assert.assertEquals("负壹万贰仟叁佰肆拾伍元陆角柒分", ChinaUtils.toChineseNumber(new BigDecimal("-12345.67")));
        Assert.assertEquals("壹万贰仟叁佰肆拾伍元陆角柒分", ChinaUtils.toChineseNumber(new BigDecimal("12345.67")));
        Assert.assertEquals("壹拾贰万叁仟肆佰伍拾陆元壹角贰分", ChinaUtils.toChineseNumber(new BigDecimal("123456.12")));
        Assert.assertEquals("壹佰贰拾叁万肆仟伍佰陆拾柒元壹角贰分", ChinaUtils.toChineseNumber(new BigDecimal("1234567.12")));
        Assert.assertEquals("壹仟贰佰叁拾肆万伍仟陆佰柒拾捌元壹角贰分", ChinaUtils.toChineseNumber(new BigDecimal("12345678.12")));
        Assert.assertEquals("壹亿贰仟叁佰肆拾伍万陆仟柒佰捌拾玖元壹角贰分", ChinaUtils.toChineseNumber(new BigDecimal("123456789.12")));
        Assert.assertEquals("壹拾贰亿叁仟肆佰伍拾陆万柒仟捌佰玖拾元零壹角贰分", ChinaUtils.toChineseNumber(new BigDecimal("1234567890.12")));
        Assert.assertEquals("壹佰贰拾叁亿肆仟伍佰陆拾柒万捌仟玖佰零壹元壹角贰分", ChinaUtils.toChineseNumber(new BigDecimal("12345678901.12")));

        Assert.assertEquals("肆仟陆佰肆拾万零贰仟玖佰壹拾伍元", ChinaUtils.toChineseNumber(new BigDecimal("46402915.00")));
        Assert.assertEquals("壹仟玖佰玖拾万零陆仟零捌拾元", ChinaUtils.toChineseNumber(new BigDecimal("19906080")));
    }

    @Test
    public void testisChineseLetter() {
        Assert.assertTrue(ChinaUtils.isChineseLetter('一'));
        Assert.assertTrue(ChinaUtils.isChineseLetter('飞'));
        Assert.assertTrue(ChinaUtils.isChineseLetter('中'));
        Assert.assertTrue(ChinaUtils.isChineseLetter('国'));
        Assert.assertTrue(ChinaUtils.isChineseLetter(''));
        Assert.assertTrue(ChinaUtils.isChineseLetter(''));
        Assert.assertTrue(ChinaUtils.isChineseLetter(''));
        Assert.assertFalse(ChinaUtils.isChineseLetter('1'));
        Assert.assertFalse(ChinaUtils.isChineseLetter('='));
        Assert.assertFalse(ChinaUtils.isChineseLetter('。'));
    }

    @Test
    public void testParseBigDecimalString() {
        Assert.assertEquals("12345678901.12345", ChinaUtils.parseChineseNumber("壹佰贰拾叁亿肆仟伍佰陆拾柒万捌仟玖佰壹元壹角贰分叁厘肆豪伍丝").toString());
        Assert.assertEquals(BigDecimal.ZERO, ChinaUtils.parseChineseNumber("0"));
        Assert.assertEquals(ChinaUtils.parseChineseNumber("0.1"), new BigDecimal("0.1"));
        Assert.assertEquals(ChinaUtils.parseChineseNumber("0.12"), new BigDecimal("0.12"));
        Assert.assertEquals(ChinaUtils.parseChineseNumber("120.00"), new BigDecimal("120.0"));
        Assert.assertEquals(ChinaUtils.parseChineseNumber("120.12345"), new BigDecimal("120.12345"));
        Assert.assertEquals(ChinaUtils.parseChineseNumber("一百二十"), new BigDecimal("120"));

        Assert.assertEquals(ChinaUtils.parseChineseNumber("一万二千三百四十五兆 六千7百八十九亿 一千二百三十四万 五千六百七十八元 9角1分2厘3豪4丝56"), new BigDecimal("12345678912345678.9123456"));
        Assert.assertEquals(ChinaUtils.parseChineseNumber("负一万二千三百四十五兆 六千7百八十九亿 一千二百三十四万 五千六百七十八元 9角1分2厘3豪4丝56"), new BigDecimal("-12345678912345678.9123456"));
        Assert.assertEquals(ChinaUtils.parseChineseNumber("负一万二千三百四十五兆 六千7百八十九亿 一千二百三十四万 五千六百七十八元"), new BigDecimal("-12345678912345678"));
        Assert.assertEquals(ChinaUtils.parseChineseNumber("9角1分2厘3豪4丝56789"), new BigDecimal("0.9123456789"));

        Assert.assertEquals(ChinaUtils.parseChineseNumber("二千三百四十五万"), new BigDecimal("23450000"));
        Assert.assertEquals(ChinaUtils.parseChineseNumber("一万二千三百四十五万"), new BigDecimal("62340"));
        Assert.assertEquals(ChinaUtils.parseChineseNumber("一万二千三百四十"), new BigDecimal("12340"));
        Assert.assertEquals(ChinaUtils.parseChineseNumber("一万二十"), new BigDecimal("10020"));
        Assert.assertEquals(ChinaUtils.parseChineseNumber("一万零二十"), new BigDecimal("10020"));
        Assert.assertEquals(ChinaUtils.parseChineseNumber("一万零二兆"), new BigDecimal("10002000000000000"));
        Assert.assertEquals(ChinaUtils.parseChineseNumber("贰仟伍佰伍拾元"), new BigDecimal("2550"));
        Assert.assertEquals(ChinaUtils.parseChineseNumber("壹仟叁佰陆拾肆元伍角"), new BigDecimal("1364.5"));
    }

    @Test
    public void testCheckIdCard() {
        Assert.assertFalse(ChinaUtils.isIdCard(null));
        Assert.assertFalse(ChinaUtils.isIdCard("xxxx"));
        Assert.assertTrue(ChinaUtils.isIdCard("350424870506202"));
        Assert.assertTrue(ChinaUtils.isIdCard("350424198705062025"));
        Assert.assertTrue(ChinaUtils.isIdCard("110101196510022029"));
        Assert.assertFalse(ChinaUtils.isIdCard("350424198705062026"));
    }

    @Test
    public void testCheck18IdCard() {
        Assert.assertFalse(ChinaUtils.isIdCard18("350X24198705062025"));
        Assert.assertTrue(ChinaUtils.isIdCard18("350424198705062025"));
        Assert.assertTrue(ChinaUtils.isIdCard18("110101196505014024"));
        Assert.assertFalse(ChinaUtils.isIdCard18("110101196505324024"));
        Assert.assertFalse(ChinaUtils.isIdCard18("x10101196505324024"));
        Assert.assertFalse(ChinaUtils.isIdCard18("xxxx"));
    }

    @Test
    public void testIdCard15to18() {
        Assert.assertNull(ChinaUtils.idCard15to18(null));
        Assert.assertNull(ChinaUtils.idCard15to18("x50424870506202"));
        Assert.assertEquals("350424198705062025", ChinaUtils.idCard15to18("350424870506202"));
        Assert.assertEquals("110101196505014024", ChinaUtils.idCard15to18("110101650501402"));
    }

    @Test
    public void testCheckUniformSocialCreditCode() {
        Assert.assertFalse(ChinaUtils.isUniformSocialCreditCode(null));
        Assert.assertTrue(ChinaUtils.isUniformSocialCreditCode("91231084MA19MPDK19"));
        Assert.assertTrue(ChinaUtils.isUniformSocialCreditCode("91230822069156716N"));
        Assert.assertTrue(ChinaUtils.isUniformSocialCreditCode("92230822MA19GE5T1B"));
        Assert.assertTrue(ChinaUtils.isUniformSocialCreditCode("92230822MA19GFP20W"));
        Assert.assertTrue(ChinaUtils.isUniformSocialCreditCode("91230822MA19DAQ94X"));
        Assert.assertTrue(ChinaUtils.isUniformSocialCreditCode("91230822098997389B"));
        Assert.assertTrue(ChinaUtils.isUniformSocialCreditCode("91230822MA19MKRA5N"));
        Assert.assertFalse(ChinaUtils.isUniformSocialCreditCode("0011141032317A5272"));

        Assert.assertTrue(ChinaUtils.isUniformSocialCreditCode("91230822MA1AW7M36X"));
        Assert.assertTrue(ChinaUtils.isUniformSocialCreditCode("91230822MA1AXF877W"));
        Assert.assertTrue(ChinaUtils.isUniformSocialCreditCode("92230822MA1ARU5B9T"));
    }

    @Test
    public void testgetPropertys() {
        int count = 0;
        List<Property> list = ChinaUtils.getProperties("5527");
        Assert.assertFalse(list.isEmpty());
        for (Property property : list) {
            Ensure.notBlank(property.getKey());
            Ensure.notBlank(property.getValue());
            if (++count >= 20) {
                break;
            }
        }
    }

    @Test
    public void testIsIdCard() {
        ChineseRandom random = new ChineseRandom();
        for (int i = 0; i < 10; i++) {
            String name = random.nextName();
            String idCard = random.nextIdCard();
            String mobile = random.nextMobile();

            // 校验随机生成的身份证号 随机生成的姓名与手机号
            Ensure.isTrue(ChinaUtils.isIdCard(idCard));
            Ensure.isTrue(mobile != null && mobile.length() == 11);
            Ensure.isTrue(StringUtils.isNotBlank(name) && name.length() <= 4);
        }
    }

    @Test
    public void testIsChineseLetter() throws IOException {
        File file = FileUtils.createTempFile("chinese_charactors.txt");
        log.info("汉字字符文件 file://{}", file);

        OutputStreamWriter out = IO.getFileWriter(file, Settings.getFileEncoding(), false);
        try {
            for (int i = 0; i < 65536; i++) {
                char c = (char) i;
                boolean letter = ChinaUtils.isChineseLetter(c);
                if (letter) {
                    String line = "汉字字符: " + Long.toHexString(i) + " " + i + " " + c + " " + letter;
                    out.write(line);
                    out.write(Settings.LINE_SEPARATOR);
                }
            }
            out.flush();
        } finally {
            out.close();
        }
    }
}
