package cn.org.expect.annotation.processor.scan;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.JavaDialectFactory;
import cn.org.expect.util.StringUtils;
import com.google.auto.service.AutoService;

/**
 * 根据父类型或类注解生成编译期类索引
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions({ScanClassProcessor.SUPER_TYPES_OPTION, ScanClassProcessor.ANNOTATIONS_OPTION})
public class ScanClassProcessor extends AbstractProcessor {

    /** 需要扫描的父类型配置项 */
    public static final String SUPER_TYPES_OPTION = "scan.superTypes";

    /** 需要扫描的注解配置项 */
    public static final String ANNOTATIONS_OPTION = "scan.annotations";

    /** 父类型索引文件的配置文件 */
    public static final String SCAN_SUPER_CONFIG_FILE = "META-INF/scan/superTypes.txt";

    /** 注解索引文件的配置文件 */
    public static final String SCAN_ANNOTATION_CONFIG_FILE = "META-INF/scan/annotations.txt";

    /** 父类型索引文件输出目录 */
    public static final String SUPER_OUTPUT_PATH = "META-INF/" + ScanClassProcessor.class.getPackage().getName() + "/super/";

    /** 注解索引文件输出目录 */
    public static final String ANNOTATION_OUTPUT_PATH = "META-INF/" + ScanClassProcessor.class.getPackage().getName() + "/annotation/";

    /** 按父类型分组的类索引 */
    public final Map<String, Set<String>> superTypeIndexes;

    /** 按注解分组的类索引 */
    public final Map<String, Set<String>> annotationIndexes;

    public ScanClassProcessor() {
        super();
        this.superTypeIndexes = new LinkedHashMap<String, Set<String>>();
        this.annotationIndexes = new LinkedHashMap<String, Set<String>>();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    /**
     * 声明处理所有注解，确保没有注解的实现类也能被父类型扫描
     *
     * @return 支持的注解类型
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    /**
     * 收集当前编译模块中的具体类，并在最后一轮生成索引文件
     *
     * @param annotations 当前轮发现的注解
     * @param roundEnv    当前处理轮环境
     * @return false，允许其他处理器继续处理注解
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            this.writeIndexes(SUPER_OUTPUT_PATH, superTypeIndexes);
            this.writeIndexes(ANNOTATION_OUTPUT_PATH, annotationIndexes);
            return false;
        }

        Map<String, TypeMirror> configuredSuperTypes = this.resolveConfiguredTypes(roundEnv);
        Set<String> configuredAnnotations = this.resolveConfiguredTypeNames(roundEnv);
        for (Element rootElement : roundEnv.getRootElements()) {
            this.collectType(rootElement, configuredSuperTypes, configuredAnnotations);
        }
        return false;
    }

    /**
     * 递归检查当前编译轮是否声明了指定类型
     *
     * @param elements 当前轮的根元素或成员元素
     * @param typeName 类型全限定名
     * @return 找到的类型元素，不存在时返回 null
     */
    private TypeElement findType(Iterable<? extends Element> elements, String typeName) {
        TypeElement knownType = this.processingEnv.getElementUtils().getTypeElement(typeName);
        if (knownType != null) {
            return knownType;
        }

        for (Element element : elements) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                if (typeName.equals(typeElement.getQualifiedName().toString())) {
                    return typeElement;
                }

                TypeElement enclosedType = findType(typeElement.getEnclosedElements(), typeName);
                if (enclosedType != null) {
                    return enclosedType;
                }
            }
        }
        return null;
    }

    /**
     * 递归收集顶层类和成员类
     *
     * @param element               待检查的元素
     * @param configuredSuperTypes  配置的父类型
     * @param configuredAnnotations 配置的注解
     */
    private void collectType(Element element, Map<String, TypeMirror> configuredSuperTypes, Set<String> configuredAnnotations) {
        if (element instanceof TypeElement) {
            TypeElement typeElement = (TypeElement) element;
            if (JavaDialectFactory.get().isTypeElement(typeElement)) {
                this.collectSuperTypes(typeElement, configuredSuperTypes);
            }

            if (JavaDialectFactory.get().isTypeElement(typeElement)) {
                this.collectAnnotations(typeElement, configuredAnnotations);
            }

            for (Element enclosed : typeElement.getEnclosedElements()) {
                if (enclosed instanceof TypeElement) {
                    this.collectType(enclosed, configuredSuperTypes, configuredAnnotations);
                }
            }
        }
    }

    /**
     * 收集实现指定父类型的类
     *
     * @param typeElement          类型元素
     * @param configuredSuperTypes 配置的父类型
     */
    private void collectSuperTypes(TypeElement typeElement, Map<String, TypeMirror> configuredSuperTypes) {
        for (Map.Entry<String, TypeMirror> entry : configuredSuperTypes.entrySet()) {
            String typeName = entry.getKey();
            TypeMirror typeMirror = entry.getValue();
            TypeMirror erasedType = this.processingEnv.getTypeUtils().erasure(typeElement.asType());
            TypeMirror erasedSuperType = this.processingEnv.getTypeUtils().erasure(typeMirror);
            if (!this.processingEnv.getTypeUtils().isSameType(erasedType, erasedSuperType) && this.processingEnv.getTypeUtils().isAssignable(erasedType, erasedSuperType)) {
                this.addIndex(this.superTypeIndexes, typeName, typeElement);
            }
        }
    }

    /**
     * 收集直接配置了指定注解的类
     *
     * @param typeElement           类型元素
     * @param configuredAnnotations 配置的注解
     */
    private void collectAnnotations(TypeElement typeElement, Set<String> configuredAnnotations) {
        for (AnnotationMirror annotationMirror : typeElement.getAnnotationMirrors()) {
            Element annotationElement = annotationMirror.getAnnotationType().asElement();
            if (annotationElement instanceof TypeElement) {
                TypeElement annotationType = (TypeElement) annotationElement;
                String annotationName = annotationType.getQualifiedName().toString();
                if (configuredAnnotations.contains(annotationName)) {
                    addIndex(this.annotationIndexes, annotationName, typeElement);
                }
            }
        }
    }

    /**
     * 解析配置的父类型
     *
     * @param roundEnv 当前处理轮环境
     * @return 父类型名称与类型信息
     */
    private Map<String, TypeMirror> resolveConfiguredTypes(RoundEnvironment roundEnv) {
        Map<String, TypeMirror> result = new LinkedHashMap<String, TypeMirror>();
        Set<String> set = this.parseOption(SUPER_TYPES_OPTION);
        set.addAll(this.readConfigFile(this.openConfigFile(SCAN_SUPER_CONFIG_FILE)));

        for (String typeName : set) {
            TypeElement typeElement = this.findType(roundEnv.getRootElements(), typeName);
            if (typeElement != null) {
                result.put(typeName, typeElement.asType());
            }
        }
        return result;
    }

    /**
     * 获取配置项中当前模块可以解析的类型名称
     *
     * @param roundEnv 当前处理轮环境
     * @return 已去重的类型名称
     */
    private Set<String> resolveConfiguredTypeNames(RoundEnvironment roundEnv) {
        Set<String> result = new TreeSet<String>();
        Set<String> set = this.parseOption(ANNOTATIONS_OPTION);
        set.addAll(this.readConfigFile(this.openConfigFile(SCAN_ANNOTATION_CONFIG_FILE)));

        for (String typeName : set) {
            if (this.findType(roundEnv.getRootElements(), typeName) != null) {
                result.add(typeName);
            }
        }
        return result;
    }

    /**
     * 读取配置文件内容
     *
     * @param content 配置文件内容
     * @return 配置项列表
     */
    private List<String> readConfigFile(String content) {
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }

        List<String> list = new ArrayList<String>();
        StringUtils.splitLines(content, list);
        return StringUtils.trimBlank(list);
    }

    /**
     * 打开配置文件并读取内容
     *
     * @param file 配置文件名
     * @return 配置文件内容
     */
    private String openConfigFile(String file) {
        try {
            FileObject fileObject = this.processingEnv.getFiler().getResource(StandardLocation.CLASS_PATH, "", file);
            if (fileObject != null) {
                return new String(IO.read(fileObject.openInputStream()), CharsetName.UTF_8);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 解析处理器配置项
     *
     * @param optionName 配置项名称
     * @return 配置项值集合
     */

    private Set<String> parseOption(String optionName) {
        String optionValue = StringUtils.trimBlank(this.processingEnv.getOptions().get(optionName));

        Set<String> result = new HashSet<String>();
        if (StringUtils.isBlank(optionValue)) {
            return result;
        }

        // 如果配置项的值是一个文件，则读取文件内容
        if (FileUtils.isFile(optionValue)) {
            try {
                result.addAll(this.readConfigFile(FileUtils.readline(new File(optionValue), CharsetName.UTF_8, 0)));
            } catch (Exception ignored) {
            }
            return result;
        }

        String[] values = StringUtils.split(optionValue, ',');
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            if (StringUtils.isNotBlank(value)) {
                result.add(StringUtils.trimBlank(value));
            }
        }
        return result;
    }

    /**
     * 将类加入对应索引
     *
     * @param indexes     索引集合
     * @param targetName  父类型或注解名称
     * @param typeElement 被扫描到的类型
     */
    private void addIndex(Map<String, Set<String>> indexes, String targetName, TypeElement typeElement) {
        ensureIndex(indexes, targetName).add(processingEnv.getElementUtils().getBinaryName(typeElement).toString());
    }

    /**
     * 获取索引集合，不存在时创建
     *
     * @param indexes    索引集合
     * @param targetName 父类型或注解名称
     * @return 目标对应的类名集合
     */
    private Set<String> ensureIndex(Map<String, Set<String>> indexes, String targetName) {
        Set<String> classNames = indexes.get(targetName);
        if (classNames == null) {
            classNames = new TreeSet<String>();
            indexes.put(targetName, classNames);
        }
        return classNames;
    }

    /**
     * 写入所有非空索引
     *
     * @param outputPath 输出目录
     * @param indexes    索引集合
     */
    private void writeIndexes(String outputPath, Map<String, Set<String>> indexes) {
        for (Map.Entry<String, Set<String>> entry : indexes.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                writeIndex(outputPath, entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 写入单个索引文件
     *
     * @param outputPath 输出目录
     * @param targetName 父类型或注解名称
     * @param classNames 类名集合
     */
    private void writeIndex(String outputPath, String targetName, Set<String> classNames) {
        String resourcePath = outputPath + targetName;
        Filer filer = processingEnv.getFiler();
        Writer writer = null;
        try {
            writer = filer.createResource(StandardLocation.CLASS_OUTPUT, "", resourcePath).openWriter();
            for (String className : classNames) {
                writer.write(className);
                writer.write(System.getProperty("line.separator"));
            }
        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "生成类扫描索引失败: " + resourcePath + ", " + exception.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException exception) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "关闭类扫描索引失败: " + resourcePath + ", " + exception.getMessage());
                }
            }
        }
    }
}
