package cn.org.expect.maven.plugin.pom;

import java.io.File;

import cn.org.expect.maven.plugin.MavenUtils;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
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
 * @author jeremy8551@gmail.com
 * @createtime 2023/12/16
 */
@Mojo(name = "pom", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class MavenPomMojo extends AbstractMojo {

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
    private String pomClassName;

    /**
     * 属性类的包名
     */
    @Parameter
    private String pomPackageName;

    /**
     * 工程源代码字符集
     */
    @Parameter(defaultValue = "${project.build.sourceEncoding}")
    private String charsetName;

    public void execute() throws MojoExecutionException {
        try {
            String projectGroupId = this.project.getGroupId();
            String projectArtifactId = this.project.getArtifactId();
            String projectVersion = this.project.getVersion();

            // 包名
            String packageName = this.getPackageName();
            getLog().info("Generating Java Class packageName: " + packageName);

            // 创建目录
            String sourceDir = MavenUtils.getGeneratedSources(this.project);
            String filepath = FileUtils.joinPath(sourceDir, packageName.replace('.', '/'));
            File dir = new File(filepath);
            FileUtils.assertCreateDirectory(dir);

            // 生成 POM 类
            String name = StringUtils.coalesce(this.pomClassName, MavenPom.CLASS_NAME); // 类名
            String javaFile = name + ".java"; // java文件名
            File classfile = new File(dir, javaFile); // 类文件
            this.getLog().info("Generating Java Class: " + classfile.getAbsolutePath() + " ..");

            // 读取模版文件
            String str = new String(IO.read(MavenPomMojo.class.getResourceAsStream("PomTemplate.txt")), CharsetName.UTF_8);
            String src = StringUtils.replaceVariable(str, "packageName", packageName, "className", name, "groupId", projectGroupId, "artifactId", projectArtifactId, "version", projectVersion);
            String charsetName = StringUtils.coalesce(this.charsetName, Settings.getFileEncoding());
            FileUtils.assertWrite(classfile, charsetName, false, src);
        } catch (Throwable e) {
            String message = this.plugin.getGroupId() + ":" + this.plugin.getArtifactId() + ":" + this.plugin.getVersion();
            throw new MojoExecutionException(message, e);
        }
    }

    /**
     * 返回Java类的包名
     *
     * @return Java包名
     */
    private String getPackageName() {
        String packageName = this.pomPackageName;

        if (StringUtils.isBlank(packageName)) {
            String sourceDirectory = this.project.getBuild().getSourceDirectory();
            File dir = new File(sourceDirectory);
            if (FileUtils.isDirectory(dir)) {
                File mainPackageDir = this.findMainPackageDir(dir);
                if (mainPackageDir != null) {
                    String part = StringUtils.removePrefix(mainPackageDir.getAbsolutePath(), dir.getAbsolutePath());
                    packageName = StringUtils.trim(FileUtils.replaceFolderSeparator(part, '.'), '.');
                }
            }
        }

        if (StringUtils.isBlank(packageName)) {
            packageName = Settings.getPackageName();
        }
        return packageName;
    }

    /**
     * 查找 main package 目录 <br>
     * <br>
     * 如下所示：cn.org.expect 是 main package <br>
     * cn<br>
     * &nbsp;└── org<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└── expect<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;├── b1<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;├── b2<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└── b3<br>
     *
     * @param dir 目录
     * @return main package 目录
     */
    private File findMainPackageDir(File dir) {
        int fileCount = 0, dirCount = 0;
        File[] files = FileUtils.array(dir.listFiles());
        for (File file : files) {
            if (file == null) {
                continue;
            }

            if (file.isFile()) {
                fileCount++;
            }

            if (file.isDirectory()) {
                dirCount++;
            }
        }

        if (fileCount > 0 || dirCount > 1) {
            return dir;
        }

        for (File file : files) {
            if (file == null) {
                continue;
            }

            if (file.isDirectory()) {
                return this.findMainPackageDir(file);
            }
        }

        return null;
    }
}
