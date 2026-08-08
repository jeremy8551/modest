package cn.org.expect.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

/**
 * 根据 javax Servlet 工具类生成 Jakarta Servlet 工具类字节码
 */
public class JakartaHttpUtilsGenerator {

    /** 工具类所在的字节码包路径 */
    private static final String PACKAGE_PATH = "cn/org/expect/util/";

    /** javax Servlet 字节码包前缀 */
    private static final String JAVAX_SERVLET_PREFIX = "javax/servlet/";

    /** Jakarta Servlet 字节码包前缀 */
    private static final String JAKARTA_SERVLET_PREFIX = "jakarta/servlet/";

    /** 原工具类与 Jakarta 工具类的名称映射 */
    private static final Map<String, String> CLASS_NAMES = new HashMap<String, String>();

    static {
        CLASS_NAMES.put(PACKAGE_PATH + "HttpRequestUtils", PACKAGE_PATH + "JakartaRequestUtils");
        CLASS_NAMES.put(PACKAGE_PATH + "HttpServletUtils", PACKAGE_PATH + "JakartaServletUtils");
        CLASS_NAMES.put(PACKAGE_PATH + "HttpSessionUtils", PACKAGE_PATH + "JakartaSessionUtils");
    }

    /**
     * 生成 Jakarta Servlet 工具类
     *
     * @param args 第一个参数为编译输出目录
     * @throws IOException 读取或写入 class 文件失败
     */
    public static void main(String[] args) throws IOException {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("Compile output directory is required");
        }

        File outputDirectory = new File(args[0]);
        Remapper remapper = new JakartaRemapper();
        for (Map.Entry<String, String> entry : CLASS_NAMES.entrySet()) {
            generate(outputDirectory, entry.getKey(), entry.getValue(), remapper);
        }
    }

    /**
     * 生成单个 Jakarta 工具类
     *
     * @param outputDirectory 编译输出目录
     * @param sourceName      原类字节码名称
     * @param targetName      目标类字节码名称
     * @param remapper        字节码名称映射器
     * @throws IOException 读取或写入 class 文件失败
     */
    private static void generate(File outputDirectory, String sourceName, String targetName, Remapper remapper) throws IOException {
        File sourceFile = new File(outputDirectory, sourceName + ".class");
        if (!sourceFile.isFile()) {
            throw new IOException("Source class file not found: " + sourceFile.getAbsolutePath());
        }

        InputStream input = new FileInputStream(sourceFile);
        byte[] bytes;
        try {
            ClassReader reader = new ClassReader(input);
            ClassWriter writer = new ClassWriter(0);
            reader.accept(new ClassRemapper(writer, remapper), 0);
            bytes = writer.toByteArray();
        } finally {
            input.close();
        }

        File targetFile = new File(outputDirectory, targetName + ".class");
        OutputStream output = new FileOutputStream(targetFile);
        try {
            output.write(bytes);
        } finally {
            output.close();
        }
    }

    /**
     * 映射工具类名称及 Servlet API 包名
     */
    private static class JakartaRemapper extends Remapper {

        /** {@inheritDoc} */
        public String map(String internalName) {
            String targetName = CLASS_NAMES.get(internalName);
            if (targetName != null) {
                return targetName;
            }
            if (internalName != null && internalName.startsWith(JAVAX_SERVLET_PREFIX)) {
                return JAKARTA_SERVLET_PREFIX + internalName.substring(JAVAX_SERVLET_PREFIX.length());
            }
            return internalName;
        }
    }
}
