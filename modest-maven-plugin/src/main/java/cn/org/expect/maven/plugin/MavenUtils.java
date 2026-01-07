package cn.org.expect.maven.plugin;

import java.io.File;
import java.util.List;

import cn.org.expect.util.FileUtils;
import org.apache.maven.project.MavenProject;

public class MavenUtils {

    /**
     * 返回 Maven 生成代码的目录
     *
     * @param project Maven项目信息
     * @return Maven 生成代码的目录
     */
    public static String getGeneratedSources(MavenProject project) {
        String sourceDir = FileUtils.joinPath(project.getBuild().getDirectory(), "generated-sources", project.getArtifactId());
        FileUtils.assertCreateDirectory(sourceDir);
        project.addCompileSourceRoot(sourceDir);
        FileUtils.clearDirectory(new File(sourceDir));
        return sourceDir;
    }

    /**
     * 查找项目
     *
     * @param list 项目集合
     * @param name 项目名
     * @return 项目信息
     */
    public static MavenProject find(List<MavenProject> list, String name) {
        for (MavenProject project : list) {
            if (project.getName().equals(name)) {
                return project;
            }
        }
        throw new IllegalArgumentException(name);
    }
}
