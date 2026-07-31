package cn.org.expect.maven.plugin;

import java.io.File;

import cn.org.expect.util.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 删除 ClassScanProcessor 注解处理器使用的扫描配置目录
 */
@Mojo(name = "cleanScan", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class CleanScanMojo extends AbstractMojo {

    /** 编译后的 class 文件输出目录 */
    @Parameter(defaultValue = "${project.build.outputDirectory}", readonly = true, required = true)
    private File outputDirectory;

    /**
     * 删除 META-INF/scan 目录
     *
     * @throws MojoExecutionException 删除目录失败
     */
    public void execute() throws MojoExecutionException {
        File scanDirectory = new File(this.outputDirectory, "META-INF/scan");
        if (!scanDirectory.exists()) {
            getLog().info("skip non existing scan directory " + scanDirectory.getAbsolutePath());
            return;
        }

        if (scanDirectory.isDirectory() && !FileUtils.deleteDirectory(scanDirectory)) {
            throw new MojoExecutionException("Delete scan directory fail: " + scanDirectory.getAbsolutePath());
        }
        getLog().info("Delete scan directory " + scanDirectory.getAbsolutePath() + " [success]");
    }
}
