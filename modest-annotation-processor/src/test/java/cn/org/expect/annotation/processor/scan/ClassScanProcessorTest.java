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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 类索引处理器测试
 */
class ClassScanProcessorTest {

    /** 编译输出目录 */
    @TempDir
    private File outputDirectory;

    /**
     * 验证父类型和注解索引生成、间接继承、抽象类过滤及结果排序
     *
     * @throws IOException 读取索引失败
     */
    @Test
    void shouldGenerateSuperTypeAndAnnotationIndexes() throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        List<SourceFile> sourceFiles = Arrays.asList(source("example.ScanService", "package example; public interface ScanService<T> {}"), source("example.ChildService", "package example; public interface ChildService extends ScanService<String> {}"), source("example.AbstractService", "package example; public abstract class AbstractService implements ScanService<String> {}"), source("example.FirstService", "package example; public class FirstService extends AbstractService {}"), source("example.SecondService", "package example; public class SecondService implements ScanService<String> {}"), source("example.Indexed", "package example; public @interface Indexed {}"), source("example.AnnotatedService", "package example; @Indexed public class AnnotatedService {}"));

        List<String> options = Arrays.asList("-d", outputDirectory.toString(), "-A" + ClassScanProcessor.SUPER_TYPES_OPTION + "=example.ScanService", "-A" + ClassScanProcessor.ANNOTATIONS_OPTION + "=example.Indexed");
        JavaCompiler.CompilationTask task = compiler.getTask(null, null, null, options, null, sourceFiles);
        task.setProcessors(Collections.singletonList(new ClassScanProcessor()));

        assertTrue(task.call());
        assertEquals(Arrays.asList("example.ChildService", "example.FirstService", "example.SecondService"), readIndex(ClassScanProcessor.SUPER_OUTPUT_PATH + "example.ScanService"));
        assertEquals(Collections.singletonList("example.AnnotatedService"), readIndex(ClassScanProcessor.ANNOTATION_OUTPUT_PATH + "example.Indexed"));
    }

    /**
     * 验证配置的父类型不存在时仍处理可解析的注解
     *
     * @throws IOException 读取索引失败
     */
    @Test
    void shouldProcessAvailableAnnotationWhenConfiguredSuperTypeDoesNotExist() throws IOException {
        List<String> options = Arrays.asList("-A" + ClassScanProcessor.SUPER_TYPES_OPTION + "=missing.ScanService", "-A" + ClassScanProcessor.ANNOTATIONS_OPTION + "=example.Indexed");
        List<SourceFile> sourceFiles = Arrays.asList(source("example.Indexed", "package example; public @interface Indexed {}"), source("example.Service", "package example; @Indexed public class Service {}"));

        assertTrue(compile(options, sourceFiles));
        assertFalse(new File(outputDirectory, ClassScanProcessor.SUPER_OUTPUT_PATH + "missing.ScanService").exists());
        assertEquals(Collections.singletonList("example.Service"), readIndex(ClassScanProcessor.ANNOTATION_OUTPUT_PATH + "example.Indexed"));
    }

    /**
     * 验证配置的注解类型不存在时仍处理可解析的父类型
     *
     * @throws IOException 读取索引失败
     */
    @Test
    void shouldProcessAvailableSuperTypeWhenConfiguredAnnotationDoesNotExist() throws IOException {
        List<String> options = Arrays.asList("-A" + ClassScanProcessor.SUPER_TYPES_OPTION + "=example.ScanService", "-A" + ClassScanProcessor.ANNOTATIONS_OPTION + "=missing.Indexed");
        List<SourceFile> sourceFiles = Arrays.asList(source("example.ScanService", "package example; public interface ScanService {}"), source("example.Service", "package example; public class Service implements ScanService {}"));

        assertTrue(compile(options, sourceFiles));
        assertEquals(Collections.singletonList("example.Service"), readIndex(ClassScanProcessor.SUPER_OUTPUT_PATH + "example.ScanService"));
        assertFalse(new File(outputDirectory, ClassScanProcessor.ANNOTATION_OUTPUT_PATH + "missing.Indexed").exists());
    }

    /**
     * 验证没有扫描到实现类或注解类时不生成空索引
     */
    @Test
    void shouldNotGenerateEmptyIndexes() {
        List<String> options = Arrays.asList("-A" + ClassScanProcessor.SUPER_TYPES_OPTION + "=example.ScanService", "-A" + ClassScanProcessor.ANNOTATIONS_OPTION + "=example.Indexed");
        List<SourceFile> sourceFiles = Arrays.asList(source("example.ScanService", "package example; public interface ScanService {}"), source("example.Indexed", "package example; public @interface Indexed {}"), source("example.Other", "package example; public class Other {}"));

        assertTrue(compile(options, sourceFiles));
        assertFalse(new File(outputDirectory, ClassScanProcessor.SUPER_OUTPUT_PATH + "example.ScanService").exists());
        assertFalse(new File(outputDirectory, ClassScanProcessor.ANNOTATION_OUTPUT_PATH + "example.Indexed").exists());
    }

    /**
     * 使用类索引处理器编译内存 Java 源文件
     *
     * @param options     额外编译参数
     * @param sourceFiles Java 源文件
     * @return true 表示编译成功
     */
    private boolean compile(List<String> options, List<SourceFile> sourceFiles) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        List<String> compilerOptions = new ArrayList<String>();
        compilerOptions.add("-d");
        compilerOptions.add(outputDirectory.toString());
        compilerOptions.addAll(options);
        JavaCompiler.CompilationTask task = compiler.getTask(null, null, null, compilerOptions, null, sourceFiles);
        task.setProcessors(Collections.singletonList(new ClassScanProcessor()));
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
     * 读取生成的索引
     *
     * @param relativePath 索引相对路径
     * @return 索引中的类名
     * @throws IOException 读取失败
     */
    private List<String> readIndex(String relativePath) throws IOException {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(outputDirectory, relativePath)), "UTF-8"));
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
            return content;
        }
    }
}
