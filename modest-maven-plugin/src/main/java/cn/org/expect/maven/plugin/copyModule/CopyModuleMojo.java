package cn.org.expect.maven.plugin.copyModule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import cn.org.expect.maven.plugin.MavenUtils;
import cn.org.expect.util.CollectionUtils;
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
 * 将聚合工程中指定子模块的源代码和资源文件复制到当前模块中
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023-10-01
 */
@Mojo(name = "copyModule", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class CopyModuleMojo extends AbstractMojo {

    /** 当前插件信息 */
    @Parameter(defaultValue = "${plugin}", readonly = true)
    private PluginDescriptor plugin;

    /** 项目信息 */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /** 源代码文件的字符集 */
    @Parameter(defaultValue = "${project.build.sourceEncoding}")
    private String charsetName;

    /** 源项目名集合（复制哪些项目中的源代码） */
    @Parameter
    private List<Module> copyModule;

    /** （复制模块中的）代码路径 */
    @Parameter(defaultValue = "src/main/java", required = true)
    private String copyModuleJava;

    /** （复制模块中的）资源文件路径 */
    @Parameter(defaultValue = "src/main/resources", required = true)
    private String copyModuleResources;

    public void execute() throws MojoExecutionException {
        try {
            if (StringUtils.isBlank(this.charsetName)) {
                this.charsetName = Settings.getFileEncoding();
            }

            getLog().info("Project sourceFileEncoding: " + this.charsetName);
            getLog().info("copyModuleSrc: " + this.copyModuleJava);
            getLog().info("copyModuleDest: " + this.copyModuleResources);
            this.execute(this.project);
        } catch (Throwable e) {
            String message = this.plugin.getGroupId() + ":" + this.plugin.getArtifactId() + ":" + this.plugin.getVersion();
            throw new MojoExecutionException(message, e);
        }
    }

    /**
     * 复制代码与资源文件
     *
     * @param dest Maven项目信息
     * @throws IOException 复制文件发生错误
     */
    protected void execute(MavenProject dest) throws IOException {
        String generatedSourceDir = MavenUtils.getGeneratedSources(dest);
        for (Module module : this.copyModule) {
            File baseDir = this.findBasedir(this.project, module.getName());
            this.copy(baseDir, generatedSourceDir, module);
        }
    }

    /**
     * 复制代码到指定项目
     *
     * @param copyBaseDir 项目信息
     * @param destBaseDir 复制代码与文件的目标项目
     * @param module      复制模块
     * @throws IOException 复制文件发生错误
     */
    protected void copy(File copyBaseDir, String destBaseDir, Module module) throws IOException {
        List<Path> paths = module.getPaths();
        if (CollectionUtils.isEmpty(paths)) {
            this.copyDir(new File(copyBaseDir, this.copyModuleJava), destBaseDir);
            this.copyDir(new File(copyBaseDir, this.copyModuleResources), destBaseDir);
        } else {
            for (Path path : paths) {
                this.copyDir(new File(copyBaseDir, path.getSrc()), destBaseDir);
            }
        }
    }

    /**
     * 复制目录
     *
     * @param srcDir  源
     * @param destDir 目标
     * @throws IOException 写文件发生错误
     */
    protected void copyDir(File srcDir, String destDir) throws IOException {
        if (srcDir.exists()) {
            if (!FileUtils.isDirectory(destDir)) {
                throw new FileNotFoundException(destDir);
            }

            getLog().info("Copy " + srcDir.getAbsolutePath() + " to " + destDir);
            this.copy(srcDir, new File(destDir));
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
                    getLog().warn("Merge " + file.getAbsolutePath() + " into " + newfile.getAbsolutePath());
                    this.merge(file, newfile);
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

    /**
     * 查找项目
     *
     * @param project 项目集合
     * @param name    项目名
     * @return 项目信息
     */
    public File findBasedir(MavenProject project, String name) {
        File basedir = project.getBasedir();

        // 子模块
        File child = new File(basedir, name);
        if (FileUtils.isDirectory(child)) {
            return child;
        }

        // 平级模块
        File peerLevel = new File(basedir.getParentFile(), name);
        if (FileUtils.isDirectory(peerLevel)) {
            return peerLevel;
        }

        throw new IllegalArgumentException(name);
    }
}
