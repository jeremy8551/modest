package cn.org.expect.annotation.processor.scan;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import com.google.auto.service.AutoService;

/**
 * 根据外部设置的包名扫描 Dao 接口，并生成方法参数索引
 *
 * <p>一行记录一个方法，格式为：类全名,方法名,参数类型=参数名,参数类型=参数名</p>
 */
@AutoService(Processor.class)
@SupportedOptions(ScanMethodProcessor.DAO_PACKAGES_OPTION)
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ScanMethodProcessor extends AbstractProcessor {

    /** 需要扫描的 Dao 包配置项，多个包使用逗号分隔 */
    public static final String DAO_PACKAGES_OPTION = "scan.daoPackages";

    /** Dao 方法参数索引文件 */
    public static final String DAO_METHOD_PARAMETER_FILE = "META-INF/" + ScanMethodProcessor.class.getName() + "/DaoMethodParameter.txt";

    /** 已生成的方法记录 */
    private final Set<String> methodRecords = new TreeSet<String>();

    /**
     * 声明处理所有注解，使没有注解的 Dao 接口也会被扫描
     *
     * @return 支持的注解类型
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    /**
     * 扫描当前编译轮中的 Dao 接口，并在最后一轮写入索引
     *
     * @param annotations 当前轮发现的注解
     * @param roundEnv    当前处理轮环境
     * @return false，允许其他处理器继续处理注解
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            if (!this.methodRecords.isEmpty()) {
                this.write(this.methodRecords);
            }
            return false;
        }

        Set<String> packageNames = this.parseOption();
        if (packageNames.isEmpty()) {
            return false;
        }

        for (Element rootElement : roundEnv.getRootElements()) {
            this.scanElement(rootElement, packageNames);
        }
        return false;
    }

    /**
     * 扫描元素及其成员接口
     *
     * @param element      待扫描元素
     * @param packageNames Dao 包名
     */
    private void scanElement(Element element, Set<String> packageNames) {
        if (!(element instanceof TypeElement)) {
            return;
        }

        TypeElement typeElement = (TypeElement) element;
        if (typeElement.getKind() == ElementKind.INTERFACE && this.matchesPackage(typeElement, packageNames)) {
            this.collectMethods(typeElement);
        }

        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (enclosedElement instanceof TypeElement) {
                this.scanElement(enclosedElement, packageNames);
            }
        }
    }

    /**
     * 判断接口是否位于配置的包或其子包中
     *
     * @param typeElement  接口元素
     * @param packageNames Dao 包名
     * @return true 表示需要扫描
     */
    private boolean matchesPackage(TypeElement typeElement, Set<String> packageNames) {
        String packageName = this.processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
        for (String configuredPackage : packageNames) {
            if (packageName.equals(configuredPackage) || packageName.startsWith(configuredPackage + ".")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将接口方法转换为索引记录
     *
     * @param daoType Dao 接口
     */
    private void collectMethods(TypeElement daoType) {
        String className = this.processingEnv.getElementUtils().getBinaryName(daoType).toString();
        for (Element member : this.processingEnv.getElementUtils().getAllMembers(daoType)) {
            if (member.getKind() != ElementKind.METHOD || member.getEnclosingElement().getKind() != ElementKind.INTERFACE) {
                continue;
            }

            ExecutableElement method = (ExecutableElement) member;
            StringBuilder record = new StringBuilder(className).append(',').append(method.getSimpleName());
            for (VariableElement parameter : method.getParameters()) {
                record.append(',').append(this.processingEnv.getTypeUtils().erasure(parameter.asType())).append('=').append(parameter.getSimpleName());
            }
            this.methodRecords.add(record.toString());
        }
    }

    /**
     * 解析 Dao 包配置
     *
     * @return 去重并排序后的包名
     */
    private Set<String> parseOption() {
        Set<String> packageNames = new TreeSet<String>();
        String option = this.processingEnv.getOptions().get(DAO_PACKAGES_OPTION);
        if (option == null) {
            return packageNames;
        }

        String[] values = StringUtils.removeBlank(StringUtils.trimBlank(StringUtils.split(option, ',')));
        packageNames.addAll(Arrays.asList(values));
        return packageNames;
    }

    /**
     * 写入 Dao 方法参数索引
     *
     * @param lines 方法参数索引记录
     */
    private void write(Set<String> lines) {
        Filer filer = this.processingEnv.getFiler();
        Writer writer = null;
        try {
            writer = filer.createResource(StandardLocation.CLASS_OUTPUT, "", DAO_METHOD_PARAMETER_FILE).openWriter();
            for (String record : lines) {
                writer.write(record);
                writer.write(FileUtils.LINE_SEPARATOR_UNIX);
            }
            writer.flush();
        } catch (IOException exception) {
            this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "生成 " + DAO_METHOD_PARAMETER_FILE + " 方法参数索引失败: " + exception.getMessage());
        } finally {
            IO.close(writer);
        }
    }
}
