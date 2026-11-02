package cn.org.expect.database.export.converter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.nio.charset.CharacterCodingException;

import org.junit.Assert;
import org.junit.Test;

/**
 * 验证文本十六进制的编码、还原与无效输入处理。
 */
public class TextConverterTest {

    /**
     * 使用固定 UTF-8 字节，覆盖中文、补充平面字符、空值和空字符串。
     *
     * @throws Exception 转换失败
     */
    @Test
    public void testExportAndLoad() throws Exception {
        String[] texts = {"中文", "A\ud83d\ude00", "<a>\"x\",1</a>", "", null};
        String[] hexValues = {"E4B8ADE69687", "41F09F9880", "3C613E2278222C313C2F613E", "", ""};
        for (int i = 0; i < texts.length; i++) {
            final String text = texts[i];
            ResultSet result = (ResultSet) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{ResultSet.class}, new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) {
                    Assert.assertEquals("getString", method.getName());
                    return text;
                }
            });
            TextConverter exporter = new TextConverter();
            String[] buffer = new String[2];
            exporter.setAttribute("position", 1);
            exporter.setAttribute("resultSet", result);
            exporter.setAttribute("buffer", buffer);
            exporter.init();
            exporter.execute();
            Assert.assertEquals(hexValues[i], buffer[1].toUpperCase());
            PreparedStatement statement = (PreparedStatement) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{PreparedStatement.class}, new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) {
                    if (text == null || text.length() == 0) {
                        Assert.assertEquals("setNull", method.getName());
                        Assert.assertEquals(Types.OTHER, args[1]);
                    } else {
                        Assert.assertEquals("setObject", method.getName());
                        Assert.assertEquals(text, args[1]);
                        Assert.assertEquals(Types.OTHER, args[2]);
                    }
                    return null;
                }
            });
            cn.org.expect.database.load.converter.TextConverter loader = new cn.org.expect.database.load.converter.TextConverter();
            loader.setAttribute("position", 1);
            loader.setAttribute("STATEMENTNAME", statement);
            loader.init();
            loader.execute(buffer[1].toLowerCase());
        }
    }

    /**
     * 拒绝半字节、非十六进制字符和损坏的 UTF-8，避免静默修改数据。
     *
     * @throws Exception 转换失败
     */
    @Test
    public void testInvalidInput() throws Exception {
        cn.org.expect.database.load.converter.TextConverter loader = new cn.org.expect.database.load.converter.TextConverter();
        loader.init();
        for (String value : new String[]{"F", "GG", "00 1"}) {
            try {
                loader.execute(value);
                Assert.fail("应拒绝无效的十六进制字符串");
            } catch (IllegalArgumentException expected) {
                Assert.assertNotNull(expected.getMessage());
            }
        }
        try {
            loader.execute("FF");
            Assert.fail("应拒绝无效 UTF-8");
        } catch (CharacterCodingException expected) {
            Assert.assertNotNull(expected);
        }
    }
}
