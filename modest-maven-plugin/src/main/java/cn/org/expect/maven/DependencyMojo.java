package cn.org.expect.maven;

import java.io.File;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "dependency", defaultPhase = LifecyclePhase.INITIALIZE)
public class DependencyMojo extends AbstractMojo {

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
     * 从哪个模块复制依赖
     */
    @Parameter
    private String dependencyModule;

    /**
     * 将依赖复制到哪个模块
     */
    @Parameter
    private String dependencyModuleDest;

    /**
     * 复制源代码的模块名集合
     */
    @Parameter
    private List<String> sourceModules;

    private Comparator<Dependency> comparator = new Comparator<Dependency>() {
        public int compare(Dependency o1, Dependency o2) {
            int g = o1.getGroupId().compareTo(o2.getGroupId());
            if (g != 0) {
                return g;
            }

            int a = o1.getArtifactId().compareTo(o2.getArtifactId());
            if (a != 0) {
                return a;
            }

            return o1.getVersion().compareTo(o2.getVersion());
        }
    };

    public void execute() throws MojoExecutionException {
        try {
            this.run();
        } catch (Throwable e) {
            String message = this.plugin.getGroupId() + ":" + this.plugin.getArtifactId() + ":" + this.plugin.getVersion();
            throw new MojoExecutionException(message, e);
        }
    }

    public void run() throws Exception {
        List<MavenProject> allProjects = this.session.getAllProjects();
        MavenProject project = MavenUtils.find(allProjects, this.dependencyModule);
        Ensure.notNull(project);
        Model model = project.getModel();
        List<Dependency> list = model.getDependencies();

        MavenProject dest = MavenUtils.find(allProjects, this.dependencyModuleDest);
        Model newModel = Ensure.notNull(dest).getModel().clone();
        newModel.getDependencies().addAll(this.parse(list));

        // 排序，去重，去test
        List<Dependency> newModelDependencies = newModel.getDependencies();
        MavenUtils.removeDuplicate(newModelDependencies, this.comparator);
        MavenUtils.dealScope(newModelDependencies);

        MavenXpp3Writer writer = new MavenXpp3Writer();
        StringWriter buffer = new StringWriter(1024 * 50);
        writer.write(buffer, newModel);
        String pom = buffer.getBuffer().toString();

        File target = new File(dest.getBuild().getDirectory());
        File pomfile = new File(target, ".dependency-pom.xml");
        getLog().info("Generating " + pomfile.getAbsolutePath() + " ..");
        FileUtils.createFile(pomfile);
        boolean b = FileUtils.write(pomfile, newModel.getModelEncoding(), false, pom);
        if (!b) {
            getLog().error("Generating " + pomfile + " fail!");
        }

        dest.setPomFile(pomfile);
        dest.setOriginalModel(newModel);
        dest.setModel(newModel);
    }

    public List<Dependency> parse(List<Dependency> list) {
        List<Dependency> all = MavenUtils.copy(list);
        List<MavenProject> allProjects = this.session.getAllProjects();
        List<MavenProject> projects = MavenUtils.find3(allProjects, this.sourceModules);

        for (Dependency dependency : list) {
            MavenProject project = MavenUtils.find(projects, dependency);
            if (project != null) {
                MavenUtils.remove(all, dependency);
                List<Dependency> dependencyList = MavenUtils.copy(project.getModel().getDependencies());
                all.addAll(dependencyList);
            }
        }

        if (MavenUtils.count(all, projects) == 0) {
            return all;
        } else {
            return this.parse(all);
        }
    }

}
