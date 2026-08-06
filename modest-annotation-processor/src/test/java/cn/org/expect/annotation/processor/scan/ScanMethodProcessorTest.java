package cn.org.expect.annotation.processor.scan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 方法参数索引处理器测试
 */
class ScanMethodProcessorTest {

    /** 编译输出目录 */
    @TempDir
    private File outputDirectory;

    /**
     * 验证按包名扫描并生成方法参数索引
     *
     * @throws IOException 读取索引失败
     */
    @Test
    void shouldGenerateMethodIndexByPackage() throws IOException {
        List<String> options = Collections.singletonList(
                "-A" + ScanMethodProcessor.SCAN_CLASS_METHOD_PACKAGE_OPTION + "=example.dao");
        List<SourceFile> sourceFiles = Arrays.asList(
                source("example.dao.UserDao",
                        "package example.dao; public interface UserDao { void update(String name, long id); }"),
                source("example.service.UserService",
                        "package example.service; public class UserService { public void save(String name) {} }"));

        assertTrue(this.compile(options, sourceFiles));
        assertEquals(Collections.singletonList("example.dao.UserDao,update,java.lang.String=name,long=id"),
                this.readIndex());
    }

    /**
     * 验证按注解扫描并生成方法参数索引
     *
     * @throws IOException 读取索引失败
     */
    @Test
    void shouldGenerateMethodIndexByAnnotation() throws IOException {
        List<String> options = Collections.singletonList(
                "-A" + ScanMethodProcessor.SCAN_CLASS_METHOD_ANNOTATION_OPTION + "=example.Mapper");
        List<SourceFile> sourceFiles = Arrays.asList(
                source("example.Mapper", "package example; public @interface Mapper {}"),
                source("example.UserMapper",
                        "package example; @Mapper public interface UserMapper { String find(int id); }"));

        assertTrue(this.compile(options, sourceFiles));
        assertEquals(Collections.singletonList("example.UserMapper,find,int=id"), this.readIndex());
    }

    /**
     * 使用方法参数索引处理器编译内存 Java 源文件
     *
     * @param options     额外编译参数
     * @param sourceFiles Java 源文件
     * @return true 表示编译成功
     */
    private boolean compile(List<String> options, List<SourceFile> sourceFiles) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        List<String> compilerOptions = new ArrayList<String>();
        compilerOptions.add("-d");
        compilerOptions.add(this.outputDirectory.toString());
        compilerOptions.addAll(options);
        JavaCompiler.CompilationTask task = compiler.getTask(null, null, null, compilerOptions, null, sourceFiles);
        task.setProcessors(Collections.singletonList(new ScanMethodProcessor()));
        return task.call().booleanValue();
    }

    /**
     * 创建内存 Java 源文件
     *
     * @param className 类全限定名
     * @param content   源代码
     * @return 内存源文件
     */
    private SourceFile source(String className, String content) {
        return new SourceFile(className, content);
    }

    /**
     * 读取生成的方法参数索引
     *
     * @return 索引内容
     * @throws IOException 读取失败
     */
    private List<String> readIndex() throws IOException {
        File indexFile = new File(this.outputDirectory, ScanMethodProcessor.CLASS_METHOD_OUTPUT_PATH);
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(indexFile), "UTF-8"));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } finally {
            reader.close();
        }
        return lines;
    }

    /**
     * 内存 Java 源文件
     */
    private static final class SourceFile extends SimpleJavaFileObject {

        /** 源代码内容 */
        private final String content;

        /**
         * 创建内存 Java 源文件
         *
         * @param className 类全限定名
         * @param content   源代码
         */
        private SourceFile(String className, String content) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.content = content;
        }

        /**
         * 获取源代码
         *
         * @param ignoreEncodingErrors 是否忽略编码错误
         * @return 源代码
         */
        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return this.content;
        }
    }
}
