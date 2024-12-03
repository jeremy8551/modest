package cn.org.expect.intellij.idea.plugin.maven;

import java.util.Collections;

import cn.org.expect.maven.repository.Artifact;
import cn.org.expect.util.ClassUtils;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;

public class IdeaMavenUtils {

    /**
     * 判断是否已安装 Maven 官方插件
     *
     * @return 返回true表示已安装 false表示未安装
     */
    public static boolean hasSetupMavenPlugin() {
        return ClassUtils.forName("org.jetbrains.idea.maven.project.MavenProjectsManager") != null;
    }

    /**
     * 下载工件
     *
     * @param plugin   搜索接口
     * @param artifact 工件信息
     */
    public static void download(MavenSearchPlugin plugin, Artifact artifact) {
        MavenRunnerParameters params = new MavenRunnerParameters();
        params.setGoals(Collections.singletonList("dependency:get"));
        params.setCmdOptions("-Dartifact=" + artifact.toStandardString());

        Project project = plugin.getContext().getActionEvent().getProject();
        MavenRunner runner = MavenRunner.getInstance(project);
        MavenRunnerSettings settings = runner.getSettings();
        runner.run(params, settings, plugin::asyncDisplay);
        plugin.display();
    }
}
