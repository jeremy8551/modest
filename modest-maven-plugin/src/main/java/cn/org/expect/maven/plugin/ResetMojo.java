package cn.org.expect.maven.plugin;

import java.io.File;
import java.util.List;

import cn.org.expect.util.FileUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * 删除工程中的所有（包含测试）java源文件与资源目录下的文件
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023-10-01
 */
@Mojo(name = "reset", defaultPhase = LifecyclePhase.CLEAN)
public class ResetMojo extends AbstractMojo {

    /**
     * 项目信息
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    public void execute() throws MojoExecutionException {
        boolean fail = false;
        File source = new File(this.project.getBuild().getSourceDirectory());
        if (FileUtils.isDirectory(source)) {
            boolean value = FileUtils.clearDirectory(source);
            getLog().info("Clear sourceDirectory " + source.getAbsolutePath() + " " + (value ? "[success]" : "[fail]"));
            if (!value) {
                fail = true;
            }
        }

        File testSources = new File(this.project.getBuild().getTestSourceDirectory());
        if (FileUtils.isDirectory(testSources)) {
            boolean value = FileUtils.clearDirectory(testSources);
            getLog().info("Clear testSourceDirectory " + testSources.getAbsolutePath() + " " + (value ? "[success]" : "[fail]"));
            if (!value) {
                fail = true;
            }
        }

        List<Resource> resources = this.project.getResources();
        if (resources != null) {
            for (Resource resource : resources) {
                File resourcefile = new File(resource.getDirectory());
                if (FileUtils.isDirectory(resourcefile)) {
                    boolean value = FileUtils.clearDirectory(resourcefile);
                    getLog().info("Clear resourceDirectory " + resourcefile.getAbsolutePath() + " " + (value ? "[success]" : "[fail]"));
                    if (!value) {
                        fail = true;
                    }
                }
            }
        }

        List<Resource> testResources = this.project.getTestResources();
        if (testResources != null) {
            for (Resource resource : testResources) {
                File testResource = new File(resource.getDirectory());
                if (FileUtils.isDirectory(testResource)) {
                    boolean value = FileUtils.clearDirectory(testResource);
                    getLog().info("Clear testResourceDirectory " + testResource.getAbsolutePath() + " " + (value ? "[success]" : "[fail]"));
                    if (!value) {
                        fail = true;
                    }
                }
            }
        }

        if (fail) {
            throw new MojoExecutionException("reset fail!");
        }
    }
}
