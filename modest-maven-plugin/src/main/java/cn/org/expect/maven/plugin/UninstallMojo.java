package cn.org.expect.maven.plugin;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.apache.maven.execution.MavenSession;
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
     * Maven会话信息
     */
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    /**
     * 卸载本地仓库中哪个版本的 jar
     */
    @Parameter
    private String uninstall;

    public void execute() {
        String groupId = this.project.getGroupId();
        String artifactId = this.project.getArtifactId();
        String version = this.project.getVersion();
        File localRepository = new File(this.session.getLocalRepository().getBasedir());

        FileUtils.assertExists(localRepository);
        File rep1 = new File(localRepository, groupId.replace('.', File.separatorChar));
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
            getLog().info("Remove the component " + groupId + ":" + artifactId + " from localRepository " + localRepository.getAbsolutePath());
            for (Iterator<File> it = Arrays.asList(FileUtils.array(rep2.listFiles())).iterator(); it.hasNext(); ) {
                File file = it.next();
                if (file.isDirectory()) {
                    this.uninstall(rep2, file.getName());
                }
            }
            this.clean(rep2);
        } else { // 卸载某个版本
            getLog().info("Remove the component " + groupId + ":" + artifactId + ":" + version + " from localRepository " + localRepository.getAbsolutePath());
            this.uninstall(rep2, version);
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
