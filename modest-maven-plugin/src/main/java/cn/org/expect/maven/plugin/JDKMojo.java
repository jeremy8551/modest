package cn.org.expect.maven.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * 在 {@linkplain LifecyclePhase#GENERATE_SOURCES} 阶段，根据编译器的大版本号（5，6，7，8）选择对应的JDK适配器方言类，并复制到源代码包中
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023-10-01
 */
@Mojo(name = "jdk", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class JDKMojo extends AbstractMojo {

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
     * Maven会话信息
     */
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    /**
     * 源代码文件的字符集
     */
    @Parameter(defaultValue = "${project.build.sourceEncoding}")
    private String charsetName;

    /**
     * JDK编译后的版本号
     */
    @Parameter(defaultValue = "${maven.compiler.target}")
    private String jdkTarget;

    public void execute() throws MojoExecutionException {
        try {
            this.run();
        } catch (Throwable e) {
            String message = this.plugin.getGroupId() + ":" + this.plugin.getArtifactId() + ":" + this.plugin.getVersion();
            throw new MojoExecutionException(message, e);
        }
    }

    private void run() throws MojoFailureException, IOException {
        getLog().info("This plugin automatically switch JDK dialect class based on the JDK version!");
        getLog().info("Project basedir: " + this.project.getBasedir());
        getLog().info("Project sourceFileEncoding: " + this.charsetName);
        getLog().info("Project sourceDirectory: " + this.project.getBuild().getSourceDirectory());

        // 搜索 JDK适配器实现类所在的目录
        File dir = this.findDialectDir();
        getLog().info("JDK dialect directory: " + dir.getAbsolutePath());

        // 查询所有方言类
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                File file = new File(dir, name);
                return file.isFile() && isJDK(name, ".java");
            }
        });

        if (files == null) {
            files = new File[0];
        }

        // 按版本号排序
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File o1, File o2) {
                return parseVersion(o1.getName()) - parseVersion(o2.getName());
            }
        });

        // 复制方言接口的实现类
        List<File> copyClasses = this.copyFiles(new File(this.project.getBuild().getSourceDirectory()), files);
        this.appendIgnorefile(copyClasses);
    }

    /**
     * 查找 JDK 方言类所在的目录
     *
     * @return 目录
     */
    private File findDialectDir() throws FileNotFoundException {
        List<Resource> resources = this.project.getBuild().getResources();
        for (Resource resource : resources) {
            // 搜索资源文件所在目录
            File resourceDir = new File(resource.getDirectory());
            FileUtils.assertDirectory(resourceDir);
            getLog().info("Project resourceDirectory: " + resourceDir.getAbsolutePath());

            // 搜索 JDK适配器实现类所在的目录
            File dir = search(resourceDir, "JDK", ".java");
            if (dir != null) {
                return dir;
            }
        }

        throw new FileNotFoundException("The directory where the JDK dialect class is located was not found!");
    }

    /**
     * 因为JDK版本不同，对应的方言实现类也不同
     * 所以需要将方言接口的实现类写入 .gitignore 中，防止自动提交，只保留编译后的class文件
     *
     * @param copyfiles 复制的方言实现类文件
     * @throws IOException 访问文件错误
     */
    protected void appendIgnorefile(List<File> copyfiles) throws IOException {
        File ignorefile = new File(this.project.getBasedir(), ".gitignore");
        if (ignorefile.exists() && ignorefile.isFile()) {
            getLog().info("Project ignorefile: " + ignorefile.getAbsolutePath());

            Set<String> patterns = JDKMojoUtils.readPatterns(copyfiles, this.project.getBasedir());
            Set<String> rules = JDKMojoUtils.readIgnorefile(ignorefile, this.charsetName);
            patterns.removeAll(rules);

            if (patterns.isEmpty()) {
                return;
            }

            String ls = FileUtils.readLineSeparator(ignorefile);
            StringBuilder buf = new StringBuilder();
            buf.append(ls);
            buf.append("### ").append(this.plugin.getGroupId()).append(":").append(this.plugin.getArtifactId()).append(":").append(this.plugin.getVersion()).append(" ###").append(ls);
            for (String pattern : patterns) {
                buf.append(pattern).append(ls);
            }
            buf.append(ls);
            getLog().info("append to " + ignorefile.getAbsolutePath() + ": \n" + buf);
            FileUtils.write(ignorefile, this.charsetName, true, buf);
        }
    }

    /**
     * 将JDK适配器方言接口实现类 {@code files}，复制到目录 {@code dir} 中
     *
     * @param dir   主要源文件的目录, src/main/java
     * @param files JDK适配器方言接口实现类
     * @return 复制后的文件集合
     */
    private List<File> copyFiles(File dir, File[] files) throws IOException {
        int major = Settings.getJDKVersion(); // JDK大版本号，如: 5, 6, 7, 8 ..
        if (StringUtils.isNotBlank(this.jdkTarget)) {
            major = Integer.parseInt(this.jdkTarget);
        }

        getLog().info("Java Compiler Major version: " + major);
        List<File> list = new ArrayList<File>(files.length);
        for (File file : files) {
            String packageName = JDKMojoUtils.readPackageName(file, this.charsetName);
            if (packageName == null) {
                getLog().warn("Failed to read the package from the Java source file: " + file.getAbsolutePath());
                continue;
            }

            File newfile = new File(dir, packageName.replace('.', '/') + "/" + FileUtils.changeFilenameExt(file.getName(), "java"));
            if (newfile.exists()) {
                FileUtils.assertFile(newfile);
            }

            int version = parseVersion(file.getName());
            if (version <= major) {
                getLog().info("JDK dialect class file: " + file.getAbsolutePath());
                if (newfile.exists()) {
                    // 如果在源代码中JDK适配器方言接口实现类已经存在了
                    // 就判断一下是否有变化：
                    // 如果最近修改了 resources 目录下的类，则用 resources 目录下的类覆盖到源代码中
                    // 如果最近修改了源代码目录下的类信息，则用源代码中的类，覆盖到 resources 目录下
                    if (newfile.length() != file.length()) {
                        if (newfile.lastModified() >= file.lastModified()) {
                            getLog().info("Copy " + newfile.getAbsolutePath() + " to " + file.getAbsolutePath() + " " + (FileUtils.copy(newfile, file) ? "[success]" : "[fail]"));
                        } else {
                            getLog().info("Copy " + file.getAbsolutePath() + " to " + newfile.getAbsolutePath() + " " + (FileUtils.copy(file, newfile) ? "[success]" : "[fail]"));
                        }
                    }
                } else {
                    FileUtils.assertCreateFile(newfile);
                    getLog().info("Copy " + file.getAbsolutePath() + " to " + newfile.getAbsolutePath() + " " + (FileUtils.copy(file, newfile) ? "[success]" : "[fail]"));
                }
                list.add(newfile);
            } else {
                if (newfile.exists()) {
                    getLog().info("Delete JavaFile " + newfile.getAbsolutePath() + " " + (newfile.delete() ? "[success]" : "[fail]"));
                }
            }
        }
        return list;
    }

    /**
     * 判断字符串参数 {@code name} 是否是一个JDK适配器方言类名
     *
     * @param name 字符串
     * @param ext  文件扩展名
     * @return true表示是JDK适配器方言类的类名
     */
    public static boolean isJDK(String name, String ext) {
        return name.startsWith("JDK") //
            && name.endsWith(ext) //
            && StringUtils.isNumber(name.substring("JDK".length(), name.length() - ext.length()) //
        );
    }

    /**
     * 解析JDK适配器方言类名中的版本号
     *
     * @param name JDK适配器方言类名（含扩展名）
     * @return 版本号
     */
    public static int parseVersion(String name) {
        String str = name.substring("JDK".length(), name.length() - ".java".length());
        return Integer.parseInt(str);
    }

    /**
     * 在参数 dir 中搜索文件
     *
     * @param dir    目录
     * @param prefix 文件名的前缀
     * @param ext    文件名的扩展名
     * @return 文件
     */
    public static File search(File dir, String prefix, String ext) {
        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        }

        for (File file : files) {
            String name = file.getName();
            if (name.startsWith(prefix) && name.endsWith(ext)) {
                return dir;
            }

            if (file.isDirectory()) {
                File result = search(file, prefix, ext);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }
}
