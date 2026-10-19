package cn.org.expect.annotation.processor.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.google.auto.service.AutoService;

/**
 * 根据编译类路径中的 Spring Boot 能力生成自动配置注册文件
 *
 * <p>仅在配置编译参数 {@code -Aspring.autoConfiguration} 时启用，参数值不参与判断</p>
 *
 * <p>Spring Boot 2.7 及以上使用 imports，旧版本使用 spring.factories
 * 只扫描当前编译的配置类，已有注册项与其他工厂条目会合并保留</p>
 */
@AutoService(Processor.class)
@SupportedOptions(AutoConfigurationProcessor.AUTO_CONFIGURATION_OPTION)
public class AutoConfigurationProcessor extends AbstractProcessor {

    /** 启用 Spring 自动配置注册文件生成的编译参数 */
    public static final String AUTO_CONFIGURATION_OPTION = "spring.autoConfiguration";

    /** Spring Boot 2.7 引入的自动配置注解 */
    private static final String AUTO_CONFIGURATION = "org.springframework.boot.autoconfigure.AutoConfiguration";

    /** 旧版本使用的配置注解 */
    private static final String CONFIGURATION = "org.springframework.context.annotation.Configuration";

    /** 自动配置工厂键 */
    private static final String ENABLE_AUTO_CONFIGURATION = "org.springframework.boot.autoconfigure.EnableAutoConfiguration";

    /** 新版自动配置注册文件 */
    private static final String IMPORTS = "META-INF/spring/" + AUTO_CONFIGURATION + ".imports";

    /** 旧版工厂注册文件 */
    private static final String FACTORIES = "META-INF/spring.factories";

    /** 当前编译收集到的配置类 */
    private final Set<String> configurationNames = new TreeSet<String>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!this.processingEnv.getOptions().containsKey(AUTO_CONFIGURATION_OPTION)) {
            return false;
        }

        if (roundEnv.processingOver()) {
            if (!roundEnv.errorRaised() && !this.configurationNames.isEmpty()) {
                this.writeConfigurations();
            }
            return false;
        }

        // 从被编译工程的类型模型判断，避免误用处理器自身类加载器中的 Spring 版本
        if (this.processingEnv.getElementUtils().getTypeElement(ENABLE_AUTO_CONFIGURATION) == null) {
            return false;
        }

        for (Element element : roundEnv.getRootElements()) {
            this.collectConfigurations(element);
        }
        return false;
    }

    /**
     * 收集配置类及其成员类，排除无法实例化的抽象类型
     *
     * @param element 当前编译元素，不允许为 null
     */
    private void collectConfigurations(Element element) {
        if (!(element instanceof TypeElement)) {
            return;
        }

        TypeElement type = (TypeElement) element;
        if (type.getKind() == ElementKind.CLASS && !type.getModifiers().contains(Modifier.ABSTRACT)) {
            for (AnnotationMirror annotation : type.getAnnotationMirrors()) {
                String name = ((TypeElement) annotation.getAnnotationType().asElement()).getQualifiedName().toString();
                if (AUTO_CONFIGURATION.equals(name) || CONFIGURATION.equals(name)) {
                    this.configurationNames.add(this.processingEnv.getElementUtils().getBinaryName(type).toString());
                    break;
                }
            }
        }

        for (Element enclosed : type.getEnclosedElements()) {
            this.collectConfigurations(enclosed);
        }
    }

    /**
     * 合并输出目录中已有的注册文件，并在最后一轮一次性写入
     */
    private void writeConfigurations() {
        boolean modern = this.processingEnv.getElementUtils().getTypeElement(AUTO_CONFIGURATION) != null;
        String path = modern ? IMPORTS : FACTORIES;
        try {
            Properties factories = new Properties();
            FileObject existing = this.processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", path);

            // Maven 会在编译前复制资源，读取输出目录可保留手工配置而不引入依赖 jar 的条目
            InputStream input = null;
            try {
                input = existing.toUri().toURL().openStream();
                if (modern) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String name = line.split("#", 2)[0].trim();
                        if (!name.isEmpty()) {
                            this.configurationNames.add(name);
                        }
                    }
                } else {
                    factories.load(input);
                }
            } catch (FileNotFoundException ignored) {
                // 首次编译尚未生成资源
            } finally {
                if (input != null) {
                    input.close();
                }
            }

            if (!modern) {
                String registered = factories.getProperty(ENABLE_AUTO_CONFIGURATION, "");
                for (String name : Arrays.asList(registered.split(","))) {
                    if (!name.trim().isEmpty()) {
                        this.configurationNames.add(name.trim());
                    }
                }

                StringBuilder names = new StringBuilder();
                for (String name : this.configurationNames) {
                    if (names.length() > 0) {
                        names.append(',');
                    }
                    names.append(name);
                }
                factories.setProperty(ENABLE_AUTO_CONFIGURATION, names.toString());
            }

            Writer writer = this.processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", path).openWriter();
            try {
                if (modern) {
                    for (String name : this.configurationNames) {
                        writer.write(name + "\n");
                    }
                } else {
                    factories.store(writer, "Generated auto-configuration registrations");
                }
            } finally {
                writer.close();
            }
        } catch (IOException exception) {
            this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "生成自动配置注册文件失败: " + path + ", " + exception.getMessage());
        }
    }
}
