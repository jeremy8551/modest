package cn.org.expect.maven.plugin;

import java.io.File;
import java.util.Arrays;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * 删除工程中的指定文件
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023-10-01
 */
@Mojo(name = "deleteFile", requiresProject = false, defaultPhase = LifecyclePhase.CLEAN)
public class DeleteFileMojo extends AbstractMojo {

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
     * 删除指定目录（与子目录）下的文件 <br>
     * 默认值是当前工程根目录
     */
    @Parameter
    private File deleteDirectory;

    /**
     * 文件名匹配规则，正则表达式，多个正则表达式用 || 分隔
     */
    @Parameter
    private String deletePattern;

    /** 正则表达式数组 */
    private String[] patterns;

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

    public void run() {
        MavenProject project = this.session.getTopLevelProject();
        if (this.deleteDirectory == null && project != null) {
            this.deleteDirectory = project.getBasedir();
        }
        if (this.deleteDirectory == null) {
            this.deleteDirectory = this.session.getCurrentProject().getBasedir();
        }

        this.patterns = StringUtils.removeBlank(StringUtils.trimBlank(StringUtils.split(this.deletePattern, "||")));
        getLog().info("delete files in " + this.deleteDirectory + ", Regex Pattern is " + Arrays.toString(this.patterns));
        this.clear(this.deleteDirectory);
    }

    /**
     * 删除所有匹配的文件
     *
     * @param fileOrDir 文件或目录
     */
    public void clear(File fileOrDir) {
        if (!fileOrDir.exists()) {
            return;
        }

        // 如过文件是一个目录
        if (fileOrDir.isDirectory()) {
            File[] files = FileUtils.array(fileOrDir.listFiles());
            for (File file : files) {
                if (file.isDirectory()) {
                    this.clear(file);
                } else if (this.match(file)) {
                    boolean value = FileUtils.delete(file, 10, 100);
                    getLog().info("Delete file " + file.getAbsolutePath() + " " + (value ? "[success]" : "[fail]"));
                }
            }
        }

        if (this.match(fileOrDir)) {
            boolean value = FileUtils.delete(fileOrDir, 10, 100);
            getLog().info("Delete " + (fileOrDir.isDirectory() ? "directory" : "file") + " " + fileOrDir.getAbsolutePath() + " " + (value ? "[success]" : "[fail]"));
        }
    }

    /**
     * 判断文件是否是一个临时文件
     *
     * @param file 文件
     * @return 返回true表示匹配文件，返回false表示不匹配文件
     */
    protected boolean match(File file) {
        for (String patten : this.patterns) {
            if (file.getName().matches(patten)) {
                return true;
            }
        }
        return false;
    }
}
