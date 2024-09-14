package cn.org.expect.maven;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.maven.entity.HidePlugin;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.ObjectUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "plug-out", defaultPhase = LifecyclePhase.INITIALIZE)
public class PlugOutMojo extends AbstractMojo {

    /**
     * 禁用插件的模块
     */
    @Parameter
    private List<String> plugOutModules;

    /**
     * 复制源代码的模块名集合
     */
    @Parameter
    private List<String> sourceModules;

    /**
     * 需要禁用的插件
     */
    @Parameter
    private List<HidePlugin> hidePlugins;

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

    public void execute() throws MojoExecutionException {
        List<String> modules = ObjectUtils.coalesce(this.plugOutModules, this.sourceModules);
        MavenUtils.assertContains(this.session.getAllProjects(), modules);
        try {
            this.run(modules);
        } catch (Throwable e) {
            String message = this.plugin.getGroupId() + ":" + this.plugin.getArtifactId() + ":" + this.plugin.getVersion();
            throw new MojoExecutionException(message, e);
        }
    }

    public void run(List<String> modules) throws Exception {
        for (String module : modules) {
            getLog().info("Disable Module Plugin: " + module);
        }

        for (HidePlugin plugin : this.hidePlugins) {
            getLog().info("Disable Plugin " + plugin.getGroupId() + ":" + plugin.getArtifactId());
        }

        List<MavenProject> allProjects = this.session.getAllProjects();
        for (MavenProject project : allProjects) {
            if (modules.contains(project.getName())) {
                this.run(project);
            }
        }
    }

    private void run(MavenProject project) throws IOException {
        Model model = project.getModel();
        Model newModel = model.clone();

        // 禁用插件
        for (HidePlugin hp : this.hidePlugins) {
            List<Plugin> plugins = newModel.getBuild().getPlugins();
            List<Plugin> list = new ArrayList<Plugin>(plugins);
            for (Plugin plugin : list) {
                if (plugin.getGroupId().equals(hp.getGroupId()) && plugin.getArtifactId().equals(hp.getArtifactId())) {
                    plugins.remove(plugin);
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
