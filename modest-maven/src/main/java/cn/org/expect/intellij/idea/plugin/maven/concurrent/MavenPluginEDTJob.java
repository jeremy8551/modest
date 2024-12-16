package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import cn.org.expect.maven.concurrent.MavenJob;
import cn.org.expect.util.Ensure;

public class MavenPluginEDTJob extends MavenJob implements EDTJob {

    private final Task command;

    public MavenPluginEDTJob(Task command, String description, Object... descriptionParams) {
        super(description, descriptionParams);
        this.command = Ensure.notNull(command);
    }

    public final int execute() throws Exception {
        this.command.run();
        return 0;
    }

    public interface Task {
        void run() throws Exception;
    }
}
