package cn.org.expect.script;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import cn.org.expect.util.IO;

/**
 * @author jeremy8551@qq.com
 * @createtime 2024/1/30 16:54
 */
public class Test {

    public static void main(String[] args) throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource("script/testNoDB.sql");
        System.out.println(url);

        Enumeration<URL> enu = cl.getResources("script/");
        while (enu.hasMoreElements()) {
            URL u = enu.nextElement();
            System.out.println(u);
        }

        InputStream in = cl.getResourceAsStream("script/testNoDB.sql");
        byte[] bytes = IO.read(in);
        String str = new String(bytes, "utf-8");
        System.out.println(str);

        URL url1 = Test.class.getResource("readme.md");
        System.out.println(url1);
    }
}
