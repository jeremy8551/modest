package cn.org.expect.maven.plugin.plugOut;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.maven.plugin.MavenUtils;
import cn.org.expect.util.CollectionUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.ObjectUtils;
import cn.org.expect.util.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "plugOut", defaultPhase = LifecyclePhase.INITIALIZE)
public class PlugOutMojo extends AbstractMojo {

    /** true表示已经执行过一次当前插件目标，false表示还未执行 */
    public static volatile boolean EXECUTED = false;

    /**
     * 插件信息
     */
    @Parameter(defaultValue = "${plugin}", readonly = true)
    private PluginDescriptor plugin;

    /**
     * Maven会话信息
     */
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    /**
     * 禁用插件的项目
     */
    @Parameter
    private List<String> plugOutModule;

    /**
     * 复制哪些项目中的代码
     */
    @Parameter
    private List<String> copySource;

    /**
     * 需要禁用的插件，可用 plugOutModule 标签（如果未设置，则禁用 copySource 标签）指定禁用哪些项目中的哪些插件
     */
    @Parameter
    private List<DisablePlugin> plugOut;

    public void execute() throws MojoExecutionException {
        if (EXECUTED) {
            getLog().info("Skip this goal!");
            return;
        } else {
            EXECUTED = true;
        }

        try {
            this.run();
        } catch (Throwable e) {
            String message = this.plugin.getGroupId() + ":" + this.plugin.getArtifactId() + ":" + this.plugin.getVersion();
            throw new MojoExecutionException(message, e);
        }
    }

    public void run() throws Exception {
        List<MavenProject> allProjects = this.session.getAllProjects();
        List<String> modules = ObjectUtils.coalesce(this.plugOutModule, this.copySource);

        for (String module : modules) {
            getLog().info("Disable Module Plugin: " + module);
        }

        for (DisablePlugin plugin : this.plugOut) {
            getLog().info("Disable Plugin " + plugin.getGroupId() + ":" + plugin.getArtifactId() + (CollectionUtils.isEmpty(plugin.getGoals()) ? "" : ", goals: " + StringUtils.join(plugin.getGoals(), ", ")));
        }

        for (String module : modules) {
            MavenProject project = MavenUtils.find(allProjects, module);
            this.run(project);
        }
    }

    private void run(MavenProject project) throws Exception {
        Model model = project.getModel();
        Model newModel = model.clone();

        // 禁用插件
        for (DisablePlugin hp : this.plugOut) {
            List<String> removeGoals = hp.getGoals();
            List<Plugin> plugins = newModel.getBuild().getPlugins();
            List<Plugin> list = new ArrayList<Plugin>(plugins);
            for (Plugin plugin : list) {
                if (plugin.getGroupId().equals(hp.getGroupId()) && plugin.getArtifactId().equals(hp.getArtifactId())) {
                    if (removeGoals == null || removeGoals.isEmpty()) {
                        plugins.remove(plugin); // 禁用整个插件
                    } else {
                        List<PluginExecution> executions = plugin.getExecutions();
                        for (PluginExecution pluginExecution : executions) {
                            pluginExecution.getGoals().removeAll(removeGoals); // 禁用插件的部分目标
                        }
                    }
                }
            }
        }

        MavenXpp3Writer writer = new MavenXpp3Writer();
        StringWriter buffer = new StringWriter(1024 * 50);
        writer.write(buffer, newModel);
        String pom = buffer.getBuffer().toString();

        File target = new File(project.getBuild().getDirectory());
        File pomfile = new File(target, ".plug-out-pom.xml");
        getLog().info("Generating " + pomfile.getAbsolutePath() + " ..");
        FileUtils.createFile(pomfile);
        boolean b = FileUtils.write(pomfile, newModel.getModelEncoding(), false, pom);
        if (!b) {
            getLog().error("Generating " + pomfile + " fail!");
        }

        project.setPomFile(pomfile);
        project.setOriginalModel(newModel);
        project.setModel(newModel);
    }
}
