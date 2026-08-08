package cn.org.expect.zh;

import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/2
 */
public class ChinaUtilsTest {

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
}
