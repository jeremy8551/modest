package cn.org.expect.maven.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * 将聚合工程中所有子模块（除了当前模块以外的所有子模块）的源代码和资源文件复制到当前模块中
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023-10-01
 */
@Mojo(name = "copySource", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class CopySourceMojo extends AbstractMojo {

    /** true表示已经执行过一次当前插件目标，false表示还未执行 */
    public static volatile boolean EXECUTED = false;

    /**
     * 当前插件信息
     */
    @Parameter(defaultValue = "${plugin}", readonly = true)
    private PluginDescriptor plugin;

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
     * 源项目名集合（复制哪些项目中的源代码）
     */
    @Parameter
    private List<String> copySource;

    /**
     * 目标项目名（复制源代码复制到哪个项目中）
     */
    @Parameter
    private List<String> pasteModule;

    public void execute() throws MojoExecutionException {
        if (EXECUTED) {
            getLog().info("Skip this goal!");
            return;
        } else {
            EXECUTED = true;
        }

        try {
            this.run();
        } catch (Throwable e) {
            String message = this.plugin.getGroupId() + ":" + this.plugin.getArtifactId() + ":" + this.plugin.getVersion();
            throw new MojoExecutionException(message, e);
        }
    }

    public void run() throws Exception {
        List<MavenProject> allProjects = this.session.getAllProjects();
        if (StringUtils.isBlank(this.charsetName)) {
            this.charsetName = Settings.getFileEncoding();
        }
        getLog().info("Project sourceFileEncoding: " + this.charsetName);

        for (String module : this.pasteModule) {
            MavenProject dest = MavenUtils.find(allProjects, module);
            String source = dest.getBuild().getSourceDirectory(); // src/main/java
            String resource = dest.getResources().isEmpty() ? null : dest.getResources().get(0).getDirectory();

            File sourceDir = StringUtils.isBlank(source) ? null : FileUtils.assertCreateDirectory(new File(source));
            File resourceDir = StringUtils.isBlank(resource) ? null : FileUtils.assertCreateDirectory(new File(resource));

            if (sourceDir != null) {
                getLog().info("Clear " + sourceDir.getAbsolutePath());
                FileUtils.clearDirectory(sourceDir);
            }
            if (resourceDir != null) {
                getLog().info("Clear " + resourceDir.getAbsolutePath());
                FileUtils.clearDirectory(resourceDir);
            }

            // 从各个模块中复制代码与资源文件
            for (String name : this.copySource) {
                MavenProject project = MavenUtils.find(allProjects, name);
                this.copy(project, sourceDir, resourceDir);
            }
        }
    }

    /**
     * 复制代码到指定项目
     *
     * @param project          项目信息
     * @param srcMainJava      代码复制后的目标目录
     * @param srcMainResources 资源文件复制的目标目录
     * @throws IOException 复制文件发生错误
     */
    protected void copy(MavenProject project, File srcMainJava, File srcMainResources) throws IOException {
        if (srcMainJava != null) {
            File moduleSrcMainJava = new File(project.getBuild().getSourceDirectory());
            getLog().info("Copy " + moduleSrcMainJava.getAbsolutePath() + " to " + srcMainJava.getAbsolutePath());
            this.copy(moduleSrcMainJava, srcMainJava);
        }

        if (srcMainResources != null) {
            List<Resource> childResources = project.getResources();
            for (Resource resource : childResources) {
                File childResource = new File(resource.getDirectory());
                if (childResource.exists()) {
                    getLog().info("Copy " + childResource.getAbsolutePath() + " to " + srcMainResources.getAbsolutePath());
                    this.copy(childResource, srcMainResources);
                }
            }
        }
    }

    /**
     * 复制文件参数file 到文件参数newFile
     *
     * @param fileOrDir 文件或目录
     * @param dest      复制后的文件
     * @throws IOException 写文件发生错误
     */
    public void copy(File fileOrDir, File dest) throws IOException {
        if (fileOrDir == null || !fileOrDir.exists() || fileOrDir.equals(dest)) {
            throw new IllegalArgumentException(StringUtils.toString(fileOrDir));
        }
        if (dest == null) {
            throw new NullPointerException();
        }

        // 复制文件
        if (fileOrDir.isFile()) {
            FileUtils.copy(fileOrDir, dest);
            return;
        }

        // 复制目录
        if (fileOrDir.isDirectory()) {
            if (dest.exists()) {
                FileUtils.assertDirectory(dest);
            } else {
                FileUtils.assertCreateDirectory(dest);
            }

            // 复制子文件
            File[] files = FileUtils.array(fileOrDir.listFiles());
            for (File file : files) {
                File newfile = new File(dest, file.getName());
                if (newfile.exists() && !newfile.isDirectory()) {
                    // 如果是POM属性类
                    if (newfile.getName().equals(PomMojo.CLASS_NAME + ".java")) {
                        continue;
                    }

                    // 如过是 SPI 文件，需要合并 SPI 配置文件
                    if (StringUtils.rtrim(file.getParentFile().getAbsolutePath(), '/', '\\').endsWith(FileUtils.replaceFolderSeparator("META-INF/services"))) { // 对SPI配置文件进行合并
                        getLog().info("Merge " + file.getAbsolutePath() + " into " + newfile.getAbsolutePath());
                        this.merge(file, newfile);
                        continue;
                    }

                    throw new IOException("Copy " + file.getAbsolutePath() + " fail，file " + newfile.getAbsolutePath() + " already exists!");
                } else {
                    this.copy(file, newfile);
                }
            }
        }

        // 不支持的文件类型
        else {
            throw new UnsupportedOperationException("Unsupported copy " + fileOrDir.getAbsolutePath());
        }
    }

    /**
     * 将文件 {@code file} 内容追加到文件参数 {@code dest} 中
     *
     * @param file 文件
     * @param dest 文件
     * @throws IOException 访问文件错误
     */
    public void merge(File file, File dest) throws IOException {
        String content = FileUtils.readline(dest, this.charsetName, 0);
        String lineSeparator = FileUtils.LINE_SEPARATOR_UNIX;
        boolean addLineSeparator = !StringUtils.endWithLineSeparator(content); // 如果文件没有以回车或换行符结尾，则返回true

        BufferedReader in = IO.getBufferedReader(file, this.charsetName);
        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(dest, true), this.charsetName);
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    if (addLineSeparator) {
                        out.write(lineSeparator);
                        addLineSeparator = false; // 只添加一次换行符
                    }

                    out.write(line);
                    out.write(lineSeparator);
                }
                out.flush();
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
