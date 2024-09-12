package cn.org.expect.maven;

import java.io.File;
import java.io.IOException;

import cn.org.expect.Modest;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * 生成 POM 文件的属性类
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/12/16
 */
@Mojo(name = "pom", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class PomMojo extends AbstractMojo {

    /** 类名 */
    public static String CLASS_NAME = "ProjectPom";

    /**
     * 当前插件信息
     */
    @Parameter(defaultValue = "${plugin}", readonly = true)
    private PluginDescriptor plugin;

    /**
     * 项目信息
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * 属性类的类名（不含包名）
     */
    @Parameter
    private String className;

    /**
     * 属性类的包名
     */
    @Parameter
    private String packageName;

    /**
     * 工程源代码字符集
     */
    @Parameter(defaultValue = "${project.build.sourceEncoding}")
    private String charsetName;

    public void execute() throws MojoExecutionException {
        try {
            this.run();
        } catch (Throwable e) {
            String message = this.plugin.getGroupId() + ":" + this.plugin.getArtifactId() + ":" + this.plugin.getVersion();
            throw new MojoExecutionException(message, e);
        }
    }

    private void run() throws IOException {
        String projectGroupId = this.project.getGroupId();
        String projectArtifactId = this.project.getArtifactId();
        String projectVersion = this.project.getVersion();
        String sourceDir = this.project.getBuild().getSourceDirectory();
        FileUtils.assertCreateDirectory(sourceDir);
        String charsetName = StringUtils.defaultString(this.charsetName, Settings.getFileEncoding());

        // 创建目录
        String packageName = StringUtils.defaultString(this.packageName, Modest.class.getPackage().getName()); // 包名
        String filepath = FileUtils.joinPath(sourceDir, packageName.replace('.', '/'));
        File dir = new File(filepath);
        FileUtils.assertCreateDirectory(dir);

        // 生成 POM 类
        String name = StringUtils.defaultString(this.className, PomMojo.CLASS_NAME); // 类名
        String javaFile = name + ".java"; // java文件名
        File classfile = new File(dir, javaFile); // 类文件
        this.getLog().info("Generating Java Class of POM: " + classfile.getAbsolutePath() + " ..");

        String uri = "/" + PomMojo.class.getPackage().getName().replace('.', '/') + "/ProjectPom.txt";
        String str = new String(ClassUtils.getResource(uri, this), charsetName);
        String src = StringUtils.replaceVariable(str, "packageName", packageName, "className", name, "groupId", projectGroupId, "artifactId", projectArtifactId, "version", projectVersion);
        FileUtils.assertWrite(classfile, charsetName, false, src);
    }

}
