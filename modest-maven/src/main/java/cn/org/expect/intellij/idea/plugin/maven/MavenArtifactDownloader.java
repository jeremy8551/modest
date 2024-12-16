package cn.org.expect.intellij.idea.plugin.maven;

import java.io.File;
import java.util.Collections;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenPluginEDTJob;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.repository.AbstractArtifactDownloader;
import cn.org.expect.maven.search.ArtifactSearchNotification;
import cn.org.expect.util.Dates;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

@EasyBean(value = "download.use.maven", priority = Integer.MAX_VALUE)
public class MavenArtifactDownloader extends AbstractArtifactDownloader {

    private volatile MavenPluginEDTJob job;

    private volatile boolean running;

    public MavenArtifactDownloader(EasyContext ioc) {
        super(ioc);
    }

    public String getAddress() {
        return "";
    }

    public void execute(Artifact artifact, File parent, boolean downloadSources, boolean downloadDocs, boolean downloadAnnotation) throws Exception {
        this.running = true;
        MavenSearchPlugin plugin = (MavenSearchPlugin) this.getSearch();
        if (plugin.getIdeaMavenPlugin().isMavenPluginEnable()) {
            this.job = new MavenPluginEDTJob(() -> { // MavenRunner 必须使用 EDT 线程执行
                MavenRunnerParameters params = new MavenRunnerParameters();
                params.setGoals(Collections.singletonList("dependency:get"));
                params.setCmdOptions("-Dartifact=" + artifact.toMavenId() + ":" + artifact.getType());

                Project project = plugin.getContext().getActionEvent().getProject();
                assert project != null;
                MavenRunner runner = MavenRunner.getInstance(project);
                MavenRunnerSettings settings = runner.getSettings();
                runner.run(params, settings, () -> { // 任务运行成功执行的操作
                    running = false;
                    plugin.display();
                });
            }, "maven.search.job.download.artifact.description", artifact.toMavenId()) {
                public void terminate() {
                    super.terminate();
                    try {
                        Thread.currentThread().interrupt();
                    } catch (Throwable e) {
                        log.error(e.getLocalizedMessage(), e);
                    }
                }
            };

            String taskName = this.job.getName(); // 任务名
            plugin.execute(this.job);
            plugin.asyncDisplay(); // 先刷新搜索结果

            // 等待下载任务执行完毕
            Throwable e = Dates.waitFor(() -> this.running, 500, 15 * 1000);
            if (e != null) {
                log.error(e.getLocalizedMessage(), e);
                plugin.getService().terminate(job.getClass(), task -> task.getName().equals(taskName)); // 终止运行中的任务
            }

            // 下载任务发生错误，刷新搜索结果
            plugin.asyncDisplay();
        } else {
            plugin.sendNotification(ArtifactSearchNotification.ERROR, "maven.search.error.cannot.setup.maven.plugin");
        }
    }

    public void terminate() {
        super.terminate();
        if (this.job != null && !this.job.isTerminate()) {
            this.job.terminate();
        }
    }
}
