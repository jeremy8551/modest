package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import cn.org.expect.util.Ensure;

public class MavenSearchEDTJob extends MavenSearchPluginJob implements EDTJob {

    private final Runnable command;

    public MavenSearchEDTJob(Runnable command, String description, Object... descriptionParams) {
        super(description, descriptionParams);
        this.command = Ensure.notNull(command);
    }

    public final int execute() throws Exception {
        this.command.run();
        return 0;
    }
}
