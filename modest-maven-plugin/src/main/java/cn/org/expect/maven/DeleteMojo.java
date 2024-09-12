package cn.org.expect.maven;

import java.io.File;
import java.util.Arrays;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 删除工程中的指定文件
 *
 * @author jeremy8551@qq.com
 * @createtime 2023-10-01
 */
@Mojo(name = "del", requiresProject = false)
public class DeleteMojo extends AbstractMojo {

    /**
     * 当前插件信息
     */
    @Parameter(defaultValue = "${plugin}", readonly = true)
    private PluginDescriptor plugin;

    /**
     * 要删除哪个目录下的文件 <br>
     * 如果没有设置这个参数，则默认删除当前工程根目录下的文件
     */
    @Parameter(defaultValue = "${basedir}")
    private String delDir;

    /**
     * 文件名匹配规则，正则表达式，多个正则表达式用 && 分隔
     */
    @Parameter
    private String delPattern;

    /** 正则表达式数组 */
    private String[] patterns;

    public void execute() throws MojoExecutionException {
        try {
            this.run();
        } catch (Throwable e) {
            String message = this.plugin.getGroupId() + ":" + this.plugin.getArtifactId() + ":" + this.plugin.getVersion();
            throw new MojoExecutionException(message, e);
        }
    }

    public void run() {
        this.patterns = StringUtils.removeBlank(StringUtils.trimBlank(StringUtils.split(this.delPattern, "||")));
        getLog().info("delete files in " + this.delDir + ", Regex Pattern is " + Arrays.toString(this.patterns));
        this.clear(new File(this.delDir));
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

