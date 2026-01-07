package cn.org.expect.maven.plugin.rm;

import java.io.File;
import java.util.List;

import cn.org.expect.maven.plugin.MavenPluginLog;
import cn.org.expect.maven.plugin.MavenPluginLogImpl;
import cn.org.expect.util.CollectionUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 删除工程中的指定文件
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023-10-01
 */
@Mojo(name = "rm", requiresProject = false, defaultPhase = LifecyclePhase.CLEAN)
public class DeleteFileMojo extends AbstractMojo {

    /**
     * 当前插件信息
     */
    @Parameter(defaultValue = "${plugin}", readonly = true)
    private PluginDescriptor plugin;

    /**
     * 删除指定目录（与子目录）下的文件 <br>
     * 默认值是当前工程根目录
     */
    @Parameter(defaultValue = "${project.basedir}")
    private File rmBasedir;

    /**
     * 文件名匹配规则，正则表达式
     */
    @Parameter
    private List<String> rmfile;

    /**
     * 文件夹名匹配规则，正则表达式
     */
    @Parameter
    private List<String> rmdir;

    public void execute() throws MojoExecutionException {
        try {
            MavenPluginLog log = new MavenPluginLogImpl(this);
            if (!CollectionUtils.isEmpty(this.rmfile)) {
                new DeleteFile(log).rmfile(this.rmBasedir, this.rmfile);
            }

            if (!CollectionUtils.isEmpty(this.rmdir)) {
                new DeleteFile(log).rmdir(this.rmBasedir, this.rmdir);
            }
        } catch (Throwable e) {
            String message = this.plugin.getGroupId() + ":" + this.plugin.getArtifactId() + ":" + this.plugin.getVersion();
            throw new MojoExecutionException(message, e);
        }
    }
}
