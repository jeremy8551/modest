package cn.org.expect.database.export.converter;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import javax.sql.rowset.serial.SerialBlob;

import org.junit.Assert;
import org.junit.Test;

/**
 * 验证文本模式与原有二进制模式相互独立。
 */
public class BlobConverterTest {

    /**
     * 未启用文本模式的 BLOB 仍导出十六进制。
     *
     * @throws Exception 转换失败
     */
    @Test
    public void testBinaryExport() throws Exception {
        ResultSet result = (ResultSet) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{ResultSet.class}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Assert.assertEquals("getBlob", method.getName());
                return new SerialBlob(new byte[]{0, 1, 127});
            }
        });
        BlobConverter converter = new BlobConverter();
        String[] buffer = new String[2];
        converter.setAttribute(AbstractConverter.PARAM_COLUMN, 1);
        converter.setAttribute(AbstractConverter.PARAM_RESULT, result);
        converter.setAttribute(AbstractConverter.PARAM_BUFFER, buffer);
        converter.init();
        converter.execute();
        Assert.assertEquals("00017F", buffer[1].toUpperCase());
    }
}
