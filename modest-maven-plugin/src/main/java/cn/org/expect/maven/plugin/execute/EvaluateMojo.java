package cn.org.expect.maven.plugin.execute;

import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import cn.org.expect.util.CollectionUtils;
import cn.org.expect.util.StringUtils;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * 执行 Java 程序
 *
 * @author jeremy8551@gmail.com
 * @createtime 2025-11-29
 */
@Mojo(name = "evaluate", requiresProject = false, defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class EvaluateMojo extends ExecuteMojo {

    /**
     * 执行命令, 格式为：<br>
     * &lt;command&gt;&lt;/command&gt;<br>
     * &lt;os&gt;macos,linux,windows&lt;/os&gt;<br>
     * &lt;active&gt;true|false&lt;/active&gt;<br>
     * &lt;skip&gt;true|false&lt;/skip&gt;<br>
     */
    @Parameter
    private List<Job> eval;

    protected boolean ignore() {
        return CollectionUtils.isEmpty(this.eval);
    }

    protected void execute(ClassLoader classLoader) throws Exception {
        this.useMavenPluginLog(classLoader);
        ScriptEngineManager manager = new ScriptEngineManager(classLoader);
        ScriptEngine engine = manager.getEngineByExtension("usl");
        for (Job job : this.eval) {
            if (job.ignore(this)) {
                continue;
            }

            String cmd = StringUtils.trimBlank(job.getCommand());
            if (StringUtils.isBlank(cmd)) {
                continue;
            }

            engine.eval(cmd);
        }
    }
}
