package cn.org.expect.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.org.expect.mail.HutoolMailV57;
import cn.org.expect.mail.Mail;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * 为独立编译的邮件接口实现类增加 Mail 接口
 */
public class MailEnhancer {

    /** 邮件接口的字节码名称 */
    private static final String MAIL_INTERFACE = Mail.class.getName().replace('.', '/');

    /** 邮件接口实现类的字节码名称 */
    private static final String MAIL_CLASS = HutoolMailV57.class.getName().replace('.', '/');

    public static void main(String[] args) throws IOException {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("target path is required");
        }

        File classFile = new File(new File(args[0]), MAIL_CLASS + ".class");
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
        OutputStream out = new FileOutputStream(file);
        try {
            out.write(bytes);
        } finally {
            out.close();
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
            if (!MAIL_CLASS.equals(name)) {
                throw new IllegalArgumentException("Unexpected class: " + name);
            }

            if (!contains(interfaces)) {
                String[] enhancedInterfaces = new String[interfaces.length + 1];
                System.arraycopy(interfaces, 0, enhancedInterfaces, 0, interfaces.length);
                enhancedInterfaces[interfaces.length] = MAIL_INTERFACE;
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
                if (MailEnhancer.MAIL_INTERFACE.equals(value)) {
                    return true;
                }
            }
            return false;
        }
    }
}
