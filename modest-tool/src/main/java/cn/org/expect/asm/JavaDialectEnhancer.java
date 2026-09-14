package cn.org.expect.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.org.expect.util.Java5Dialect;
import cn.org.expect.util.JavaDialect;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * 为独立编译的 Java5 方言增加 JavaDialect 接口
 */
public class JavaDialectEnhancer {

    /** Java 方言接口的字节码名称 */
    private static final String JAVA_DIALECT_INTERFACE = JavaDialect.class.getName().replace('.', '/');

    /** Java5 方言类的字节码名称 */
    private static final String JAVA5_DIALECT_CLASS = Java5Dialect.class.getName().replace('.', '/');

    /**
     * 执行 Java5 方言字节码增强
     *
     * @param args 第一个参数为 Java5Dialect.class 文件路径
     * @throws IOException 读取或写入 class 文件失败
     */
    public static void main(String[] args) throws IOException {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("target path is required");
        }

        File classFile = new File(args[0], JAVA5_DIALECT_CLASS + ".class");
        if (!classFile.isFile()) {
            throw new IOException("class file not found: " + classFile.getAbsolutePath());
        }

        byte[] source = read(classFile);
        ClassReader reader = new ClassReader(source);
        ClassWriter writer = new ClassWriter(reader, 0);
        ClassVisitor visitor = new InterfaceVisitor(writer);
        reader.accept(visitor, 0);
        write(classFile, writer.toByteArray());
    }

    /**
     * 读取 class 文件
     *
     * @param file class 文件
     * @return class 文件内容
     * @throws IOException 读取失败
     */
    private static byte[] read(File file) throws IOException {
        InputStream input = new FileInputStream(file);
        try {
            byte[] bytes = new byte[(int) file.length()];
            int offset = 0;
            while (offset < bytes.length) {
                int length = input.read(bytes, offset, bytes.length - offset);
                if (length < 0) {
                    throw new IOException("Unexpected end of class file: " + file.getAbsolutePath());
                }
                offset += length;
            }
            return bytes;
        } finally {
            input.close();
        }
    }

    /**
     * 写入增强后的 class 文件
     *
     * @param file  class 文件
     * @param bytes class 文件内容
     * @throws IOException 写入失败
     */
    private static void write(File file, byte[] bytes) throws IOException {
        OutputStream output = new FileOutputStream(file);
        try {
            output.write(bytes);
        } finally {
            output.close();
        }
    }

    /**
     * 修改 class 接口表
     */
    private static class InterfaceVisitor extends ClassVisitor {

        /**
         * 初始化接口访问器
         *
         * @param visitor 下一个访问器
         */
        private InterfaceVisitor(ClassVisitor visitor) {
            super(Opcodes.ASM9, visitor);
        }

        /** {@inheritDoc} */
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            if (!JAVA5_DIALECT_CLASS.equals(name)) {
                throw new IllegalArgumentException("Unexpected class: " + name);
            }

            if (!contains(interfaces)) {
                String[] enhancedInterfaces = new String[interfaces.length + 1];
                System.arraycopy(interfaces, 0, enhancedInterfaces, 0, interfaces.length);
                enhancedInterfaces[interfaces.length] = JAVA_DIALECT_INTERFACE;
                interfaces = enhancedInterfaces;
            }
            super.visit(version, access, name, signature, superName, interfaces);
        }

        /**
         * 判断接口表是否已包含目标接口
         *
         * @param interfaces 接口表
         * @return true 表示已经包含
         */
        private static boolean contains(String[] interfaces) {
            for (String value : interfaces) {
                if (JavaDialectEnhancer.JAVA_DIALECT_INTERFACE.equals(value)) {
                    return true;
                }
            }
            return false;
        }
    }
}
