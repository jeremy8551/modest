package cn.org.expect.maven.plugin.lastupdated;

import java.io.File;

import cn.org.expect.maven.plugin.MavenPluginLogImpl;
import cn.org.expect.util.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 删除本地仓库中依赖下载失败所产生的临时文件( lastupdated )
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/4
 */
@Mojo(name = "lastupdated", requiresProject = false, defaultPhase = LifecyclePhase.CLEAN)
public class LastupdatedMojo extends AbstractMojo {

    /**
     * 本地仓库的绝对路径
     */
    @Parameter(defaultValue = "${settings.localRepository}")
    private File localRepository;

    public void execute() {
        if (FileUtils.isDirectory(this.localRepository)) {
            new Lastupdated(new MavenPluginLogImpl(this)).execute(this.localRepository);
        }
    }
}
