package cn.org.expect.maven.plugin.format;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.org.expect.maven.plugin.MavenPluginLogImpl;
import cn.org.expect.util.CollectionUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * 格式化代码
 *
 * @author jeremy8551@gmail.com
 * @createtime 2025/2/1
 */
@Mojo(name = "format", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class FormatMojo extends AbstractMojo {

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
     * 工程源代码字符集
     */
    @Parameter(defaultValue = "${project.build.sourceEncoding}")
    private String charsetName;

    /**
     * 输出详细信息
     */
    @Parameter
    private boolean formatVerbose;

    /**
     * 待格式化文件所在的目录
     */
    @Parameter
    private List<String> formatBasedir;

    public void execute() throws MojoExecutionException {
        try {
            this.execute(this.formatBasedir);
        } catch (Throwable e) {
            String message = this.plugin.getGroupId() + ":" + this.plugin.getArtifactId() + ":" + this.plugin.getVersion();
            throw new MojoExecutionException(message, e);
        }
    }

    private void execute(List<String> fileList) throws IOException {
        Format format = new Format(new MavenPluginLogImpl(this), this.formatVerbose);

        if (CollectionUtils.isEmpty(fileList)) {
            List<String> compileSourceRoots = this.project.getCompileSourceRoots();
            for (String filepath : compileSourceRoots) {
                File file = new File(filepath);
                if (file.exists()) {
                    format.execute(file, this.charsetName);
                }
            }

            List<String> testCompileSourceRoots = this.project.getTestCompileSourceRoots();
            for (String filepath : testCompileSourceRoots) {
                File file = new File(filepath);
                if (file.exists()) {
                    format.execute(file, this.charsetName);
                }
            }
        } else {
            for (String filepath : fileList) {
                if (StringUtils.isBlank(filepath)) {
                    continue;
                }

                File file = new File(filepath);
                FileUtils.assertExists(file);
                format.execute(file, this.charsetName);
            }
        }
    }
}
