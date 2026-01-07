package cn.org.expect.maven.plugin.copyDependency;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.maven.plugin.MavenUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.ObjectUtils;
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

@Mojo(name = "copyDependency", defaultPhase = LifecyclePhase.INITIALIZE)
public class CopyDependencyMojo extends AbstractMojo {

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
     * 源项目名集合（复制哪些项目中的依赖）
     */
    @Parameter
    private List<String> copySource;

    /**
     * 复制哪些项目中的依赖
     */
    @Parameter
    private List<String> copyDependency;

    /**
     * 将依赖复制到哪些项目中
     */
    @Parameter
    private List<String> pasteModule;

    /**
     * 依赖排序规则
     */
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
        if (EXECUTED) {
            getLog().info("Skip this goal!");
            return;
        } else {
            EXECUTED = true;
        }

        try {
            List<Dependency> list = this.merge(this.copyDependency);
            for (String name : this.pasteModule) {
                this.paste(list, name);
            }
        } catch (Throwable e) {
            String message = this.plugin.getGroupId() + ":" + this.plugin.getArtifactId() + ":" + this.plugin.getVersion();
            throw new MojoExecutionException(message, e);
        }
    }

    public List<Dependency> merge(List<String> modules) throws Exception {
        List<MavenProject> allProjects = this.session.getAllProjects();
        List<Dependency> list = new ArrayList<Dependency>();
        for (String module : modules) {
            MavenProject project = MavenUtils.find(allProjects, module); // 搜索要从哪个项目中复制依赖
            list.addAll(project.getModel().getDependencies());
        }

        // 搜索需要合并源码的项目集合
        List<String> copySource = ObjectUtils.coalesce(this.copySource, new ArrayList<String>());
        List<MavenProject> projects = CopyDependencyUtils.find(allProjects, copySource);
        List<Dependency> dependencies = CopyDependencyUtils.deepReplace(list, projects);
        CopyDependencyUtils.dealScope(dependencies);
        return dependencies;
    }

    private void paste(List<Dependency> dependencies, String pasteModule) throws IOException, MojoExecutionException {
        List<MavenProject> allProjects = this.session.getAllProjects();

        // 将依赖添加到哪个项目中
        MavenProject dest = MavenUtils.find(allProjects, pasteModule);
        Model newModel = dest.getModel().clone();

        // 将内部模块中的依赖合并到目标项目中，对依赖进行排序，去重
        newModel.getDependencies().addAll(dependencies);
        List<Dependency> dependencyList = newModel.getDependencies();
        CopyDependencyUtils.removeDuplicate(dependencyList, this.comparator);

        // 生成pom
        MavenXpp3Writer writer = new MavenXpp3Writer();
        StringWriter buffer = new StringWriter(1024 * 50);
        writer.write(buffer, newModel);
        String pom = buffer.getBuffer().toString();

        // 写入pom文件
        File target = new File(dest.getBuild().getDirectory());
        File pomfile = new File(target, ".copy-dependency-pom.xml");
        getLog().info("Generating " + pomfile.getAbsolutePath() + " ..");
        FileUtils.createFile(pomfile);
        boolean b = FileUtils.write(pomfile, newModel.getModelEncoding(), false, pom);
        if (!b) {
            getLog().error("Generating " + pomfile + " fail!");
        }

        // 更新pom
        dest.setPomFile(pomfile);
        dest.setOriginalModel(newModel);
        dest.setModel(newModel);
    }
}
