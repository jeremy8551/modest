package cn.org.expect.crypto;

import java.util.Map;

import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class RSAEncryptTest {

    @Test
    public void test() throws Exception {
        Map<String, Object> keyMap = RSAEncrypt.initKey();

        // 公钥
        byte[] publicKey = RSAEncrypt.getPublicKey(keyMap);

        // 私钥
        byte[] privateKey = RSAEncrypt.getPrivateKey(keyMap);

        // 公钥字符串
        String publicKeyHexStr = StringUtils.toHexString(publicKey);

        // 私钥字符串
        String privateKeyHexStr = StringUtils.toHexString(privateKey);

//        Logs.info("crypto.stdout.message009", publicKeyHexStr);
//        Logs.info("crypto.stdout.message010", privateKeyHexStr);
//        Logs.info("crypto.stdout.message011");

        String str = ResourcesUtils.getMessage("crypto.stdout.message012");
//        Logs.info("crypto.stdout.message013");
//        Logs.info("crypto.stdout.message019", str);

        // 甲方进行数据的加密
        byte[] code1 = RSAEncrypt.encryptByPrivateKey(str.getBytes(), privateKey);
//        Logs.info(ResourcesUtils.getMessage("crypto.stdout.message014", StringUtils.toHexString(code1)));
//        Logs.info(ResourcesUtils.getMessage("crypto.stdout.message015"));

        // 乙方进行数据的解密
        byte[] decode1 = RSAEncrypt.decryptByPublicKey(code1, StringUtils.parseHexString(publicKeyHexStr));
//        Logs.info(ResourcesUtils.getMessage("crypto.stdout.message016", new String(decode1)));

//        Logs.info(ResourcesUtils.getMessage("crypto.stdout.message017"));
        str = ResourcesUtils.getMessage("crypto.stdout.message018");
//        Logs.info(ResourcesUtils.getMessage("crypto.stdout.message019", str));

        // 乙方使用公钥对数据进行加密
        byte[] code2 = RSAEncrypt.encryptByPublicKey(str.getBytes(), publicKey);
//        Logs.info("crypto.stdout.message020");
//        Logs.info("crypto.stdout.message021", StringUtils.toHexString(code2)); // 吧字节数组转为64位字符串

//        Logs.info("crypto.stdout.message022");
//        Logs.info("crypto.stdout.message023");

        // 甲方使用私钥对数据进行解密
        byte[] decode2 = RSAEncrypt.decryptByPrivateKey(code2, StringUtils.parseHexString(privateKeyHexStr));
//        Logs.info("crypto.stdout.message024", new String(decode2));
        Assert.assertEquals(str, new String(decode2));
    }
}
