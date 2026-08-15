package cn.org.expect.maven.plugin;

import java.io.File;
import java.io.IOException;

import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.SourceIndex;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * 保存当前 Maven 项目的源码目录和资源目录。
 */
@Mojo(name = "sourceIndex", defaultPhase = LifecyclePhase.PROCESS_SOURCES, threadSafe = true)
public class SourceIndexMojo extends AbstractMojo {

    /** 当前 Maven Project */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        String outputDirectory = project.getBuild().getOutputDirectory();
        String groupId = project.getGroupId();
        String artifactId = project.getArtifactId();
        String module = groupId + ":" + artifactId;
        StringBuilder buf = new StringBuilder();

        // Java 源码目录
        for (String path : project.getCompileSourceRoots()) {
            if (FileUtils.isDirectory(path)) {
                buf.append(module).append(",").append("source").append(",").append(path).append("\n");
            }
        }

        // Resource 资源目录
        for (Resource resource : project.getResources()) {
            String resourcePath = resource.getDirectory();
            if (FileUtils.isDirectory(resourcePath)) {
                buf.append(module).append(",").append("resource").append(",").append(resourcePath).append("\n");
            }
        }

        for (String path : project.getTestCompileSourceRoots()) {
            if (FileUtils.isDirectory(path)) {
                buf.append(module).append(",").append("testSource").append(",").append(path).append("\n");
            }
        }

        for (Resource resource : project.getTestResources()) {
            String resourcePath = resource.getDirectory();
            if (FileUtils.isDirectory(resourcePath)) {
                buf.append(module).append(",").append("testResource").append(",").append(resourcePath).append("\n");
            }
        }

        File outputFile = new File(outputDirectory, SourceIndex.INDEX_RESOURCE);
        try {
            FileUtils.write(outputFile, CharsetName.UTF_8, false, buf.toString());
        } catch (IOException e) {
            throw new MojoExecutionException(outputFile.getAbsolutePath(), e);
        }
    }
}
