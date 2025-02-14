package cn.org.expect.maven.plugin;

import java.io.File;

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

    /** true表示已经执行过一次当前插件目标，false表示还未执行 */
    public static volatile boolean EXECUTED = false;

    /**
     * 本地仓库的绝对路径
     */
    @Parameter(defaultValue = "${settings.localRepository}")
    private File localRepository;

    public void execute() {
        if (EXECUTED) {
            getLog().info("Skip this goal!");
            return;
        } else {
            EXECUTED = true;
        }

        getLog().info("Check localRepository: " + this.localRepository.getAbsolutePath());
        FileUtils.assertDirectory(this.localRepository);
        this.clear(this.localRepository);
    }

    /**
     * 清空目录中的所有文件
     *
     * @param dir 目录
     */
    public void clear(File dir) {
        File[] files = FileUtils.array(dir.listFiles());
        for (File file : files) {
            if (file == null) {
                continue;
            }

            if (file.isDirectory()) {
                this.clear(file);
                continue;
            }

            if (file.isFile() && file.getName().endsWith(".lastupdated")) {
                getLog().info("Delete file " + file.getAbsolutePath() + " " + (file.delete() ? "[success]" : "[fail]"));
                continue;
            }
        }
    }
}
