package cn.org.expect.crypto;

import org.junit.Assert;
import org.junit.Test;

public class DESEncryptTest {

    private final static String key = "测试密钥1"; // 8 个字节 64 的密钥

    @Test
    public void test() {
        String message = "hello world. 你好，DES";
        byte[] encryptBytes = DESEncrypt.encrypt(message.getBytes(), key.getBytes());
        String encryptStr = new String(encryptBytes);
        Assert.assertNotEquals(message, encryptStr);

        byte[] deMsgBytes = DESEncrypt.decrypt(encryptBytes, key.getBytes());
        Assert.assertEquals(message, new String(deMsgBytes));
    }
}
