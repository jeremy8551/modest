package cn.org.expect.maven.plugin.extension;

import java.io.File;

import cn.org.expect.maven.plugin.MavenPluginLog;
import cn.org.expect.maven.plugin.lastupdated.Lastupdated;
import cn.org.expect.util.Logs;
import cn.org.expect.util.Settings;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;

/**
 * 删除本地仓库中依赖下载失败所产生的临时文件( lastupdated )
 */
@Component(role = AbstractMavenLifecycleParticipant.class, hint = LastupdatedLifecycleParticipant.ID)
public class LastupdatedLifecycleParticipant extends AbstractMavenLifecycleParticipant {
    private final static Logger log = new ConsoleLogger(Logger.LEVEL_INFO, LastupdatedLifecycleParticipant.ID);

    protected final static String ID = "modest.maven.lastupdated.extension";

    public void afterProjectsRead(MavenSession session) {
        if (Settings.containsVariable(LastupdatedLifecycleParticipant.ID) && !Boolean.parseBoolean(Settings.getProperty(LastupdatedLifecycleParticipant.ID))) {
            return;
        }

        String localRepository = session.getLocalRepository().getBasedir();
        log.info("localRepository: " + localRepository);
        new Lastupdated(this.getMavenPluginLog()).execute(new File(localRepository));
    }

    private MavenPluginLog getMavenPluginLog() {
        return new MavenPluginLog() {
            public String getName() {
                return LastupdatedLifecycleParticipant.class.getName();
            }

            public boolean isTraceEnabled() {
                return false;
            }

            public boolean isDebugEnabled() {
                return false;
            }

            public boolean isInfoEnabled() {
                return true;
            }

            public boolean isWarnEnabled() {
                return true;
            }

            public boolean isErrorEnabled() {
                return true;
            }

            public boolean isFatalEnabled() {
                return true;
            }

            public void trace(Object message, Object... args) {
            }

            public void debug(Object message, Object... args) {
                log.debug(Logs.toString(message, args));
            }

            public void info(Object message, Object... args) {
                log.info(Logs.toString(message, args));
            }

            public void warn(Object message, Object... args) {
                log.warn(Logs.toString(message, args));
            }

            public void error(Object message, Object... args) {
                log.error(Logs.toString(message, args));
            }

            public void fatal(Object message, Object... args) {
                log.error(Logs.toString(message, args));
            }
        };
    }
}
