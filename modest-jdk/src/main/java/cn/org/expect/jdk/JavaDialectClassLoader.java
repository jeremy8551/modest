package cn.org.expect.jdk;

import java.io.IOException;
import java.io.InputStream;

import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.IO;

public class JavaDialectClassLoader extends ClassLoader {

    public JavaDialectClassLoader() {
        super(ClassUtils.getClassLoader());
    }

    @SuppressWarnings("unchecked")
    public Class<JavaDialect> loadClass(String className, InputStream in) throws IOException {
        if (in != null) {
            byte[] bytes = IO.read(in);
            return (Class<JavaDialect>) this.defineClass(className, bytes, 0, bytes.length);
        } else {
            return null;
        }
    }
}
