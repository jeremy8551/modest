package cn.org.expect.maven.plugin.extension;

import java.util.List;

import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;

/**
 * 执行 install comile test 之前先执行 clean
 */
@Component(role = AbstractMavenLifecycleParticipant.class, hint = CleanLifecycleParticipant.ID)
public class CleanLifecycleParticipant extends AbstractMavenLifecycleParticipant {
    private final static Logger log = new ConsoleLogger(Logger.LEVEL_INFO, CleanLifecycleParticipant.ID);

    protected final static String ID = "modest.maven.clean.extension";

    public void afterProjectsRead(MavenSession session) {
        if (Settings.containsVariable(CleanLifecycleParticipant.ID) && !Boolean.parseBoolean(Settings.getProperty(CleanLifecycleParticipant.ID))) {
            return;
        }

        List<String> goals = session.getRequest().getGoals();
        if (!goals.contains("clean")) {
            if (goals.contains("install") || goals.contains("compile") || goals.contains("test")) {
                goals.add(0, "clean");
                log.info("mvn " + StringUtils.join(goals, " "));
            }
        }
    }
}
