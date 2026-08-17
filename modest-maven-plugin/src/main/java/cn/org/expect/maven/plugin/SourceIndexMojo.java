package cn.org.expect.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
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

    @Parameter
    private List<String> addSourceIndex;

    @Parameter
    private List<String> addResourceIndex;

    @Override
    public void execute() throws MojoExecutionException {
        String outputDirectory = project.getBuild().getOutputDirectory();
        String projectBuildDirectory = project.getBuild().getDirectory();
        String groupId = project.getGroupId();
        String artifactId = project.getArtifactId();
        String module = groupId + ":" + artifactId;
        StringBuilder buf = new StringBuilder();

        String sourceFlag = "source";
        String resourceFlag = "resource";

        // Java 源码目录
        for (String source : project.getCompileSourceRoots()) {
            if (FileUtils.isDirectory(source)) {
                buf.append(module).append(",").append(sourceFlag).append(",").append(source).append("\n");
            }
        }

        // Resource 资源目录
        for (Resource resource : project.getResources()) {
            String resourcePath = resource.getDirectory();
            if (FileUtils.isDirectory(resourcePath)) {
                buf.append(module).append(",").append(resourceFlag).append(",").append(resourcePath).append("\n");
            }
        }

        for (String testSource : project.getTestCompileSourceRoots()) {
            if (FileUtils.isDirectory(testSource)) {
                buf.append(module).append(",").append("testSource").append(",").append(testSource).append("\n");
            }
        }

        for (Resource testResource : project.getTestResources()) {
            String resourcePath = testResource.getDirectory();
            if (FileUtils.isDirectory(resourcePath)) {
                buf.append(module).append(",").append("testResource").append(",").append(resourcePath).append("\n");
            }
        }

        buf.append(module).append(",").append(sourceFlag).append(",").append(FileUtils.joinPath(projectBuildDirectory, "generated-sources/annotations")).append("\n");
        buf.append(module).append(",").append(sourceFlag).append(",").append(FileUtils.joinPath(projectBuildDirectory, Settings.getProjectName(), "codegen/java")).append("\n");

        if (this.addSourceIndex != null) {
            for (String source : this.addSourceIndex) {
                buf.append(module).append(",").append(sourceFlag).append(",").append(source).append("\n");
            }
        }

        if (this.addResourceIndex != null) {
            for (String resource : this.addResourceIndex) {
                buf.append(module).append(",").append(resourceFlag).append(",").append(resource).append("\n");
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
