package cn.org.expect.maven.plugin;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * 从本地仓库中删除当前工程已安装的jar文件
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023-11-04
 */
@Mojo(name = "uninstall", defaultPhase = LifecyclePhase.CLEAN)
public class UninstallMojo extends AbstractMojo {

    /**
     * 项目信息
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * 本地仓库的绝对路径
     */
    @Parameter(defaultValue = "${settings.localRepository}")
    private File localRepository;

    /**
     * 卸载本地仓库中哪个版本的 jar
     */
    @Parameter
    private String uninstall;

    public void execute() {
        if (!FileUtils.isDirectory(this.localRepository)) {
            return;
        }

        String groupId = this.project.getGroupId();
        String artifactId = this.project.getArtifactId();
        String version = this.project.getVersion();

        File rep1 = new File(this.localRepository, groupId.replace('.', File.separatorChar));
        if (rep1.exists()) {
            FileUtils.assertDirectory(rep1);
        } else {
            getLog().info("Directory " + rep1.getAbsolutePath() + " not exists!");
            return;
        }

        File rep2 = new File(rep1, artifactId.replace('.', File.separatorChar));
        if (rep2.exists()) {
            FileUtils.assertDirectory(rep2);
        } else {
            getLog().info("Directory " + rep2.getAbsolutePath() + " not exists!");
            return;
        }

        // 删除全部版本
        if (StringUtils.inArrayIgnoreCase(this.uninstall, "all", "*")) {
            getLog().info("Remove the component " + groupId + ":" + artifactId + " from localRepository " + this.localRepository.getAbsolutePath());
            for (Iterator<File> it = Arrays.asList(FileUtils.array(rep2.listFiles())).iterator(); it.hasNext(); ) {
                File file = it.next();
                if (file.isDirectory()) {
                    this.uninstall(rep2, file.getName());
                }
            }
            this.clean(rep2);
        } else {
            // 卸载某个版本
            getLog().info("Remove the component " + groupId + ":" + artifactId + ":" + version + " from localRepository " + this.localRepository.getAbsolutePath());
            this.uninstall(rep2, version);

            // 卸载快照
            String snapshot = version + "-SNAPSHOT";
            if (FileUtils.isDirectory(new File(rep2, snapshot))) {
                this.uninstall(rep2, snapshot);
            }
        }
    }

    /**
     * 删除指定版本
     *
     * @param parent 本地仓库中工件目录
     * @param name   版本号
     */
    private void uninstall(File parent, String name) {
        File dir = new File(parent, name);
        if (dir.exists()) {
            FileUtils.assertDirectory(dir);
            this.clean(dir);
            FileUtils.delete(dir);
        } else {
            getLog().info("Directory " + dir.getAbsolutePath() + " not exists!");
        }
    }

    /**
     * 清空目录中的所有文件
     *
     * @param dir 目录
     */
    public void clean(File dir) {
        if (dir == null || !dir.exists() || dir.isFile()) {
            return;
        }

        File[] files = FileUtils.array(dir.listFiles());
        for (File file : files) {
            if (file == null) {
                continue;
            }

            if (file.isDirectory()) {
                this.clean(file);
            }

            getLog().info("Delete file " + file.getAbsolutePath() + " " + (file.delete() ? "[success]" : "[fail]"));
        }
    }
}
