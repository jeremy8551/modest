package cn.org.expect.intellij.idea.plugin.maven.listener;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunConfiguration;

public class ExecutionListenerImpl implements ExecutionListener {
    private final static Log log = LogFactory.getLog(ExecutionListenerImpl.class);

    public void processStarting(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
        if (env.getRunProfile() instanceof MavenRunConfiguration) {
            MavenRunConfiguration mavenConfig = (MavenRunConfiguration) env.getRunProfile();
            if (mavenConfig.getRunnerParameters().getGoals().contains("install")) {
                log.info("Detected Maven Install command. Executing custom logic...");
            }
        }
    }
}
