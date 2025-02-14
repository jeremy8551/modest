package cn.org.expect.maven.plugin;

import java.io.File;

import cn.org.expect.util.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 删除工程中 target 目录
 *
 * @author jeremy8551@gmail.com
 * @createtime 2024-08-05
 */
@Mojo(name = "clean", defaultPhase = LifecyclePhase.VALIDATE)
public class CleanMojo extends AbstractMojo {

    /**
     * 项目构建输出目录，默认为target/.
     */
    @Parameter(defaultValue = "${project.build.directory}")
    private File target;

    public void execute() {
        if (FileUtils.isDirectory(this.target)) {
            getLog().info("Delete targetDirectory " + this.target.getAbsolutePath() + " " + (FileUtils.deleteDirectory(this.target) ? "[success]" : "[fail]"));
        } else {
            getLog().info("skip not existing targetDirectory  " + this.target.getAbsolutePath());
        }
    }
}
