package cn.org.expect.maven.repository.local;

import java.io.File;
import java.util.ArrayList;

import cn.org.expect.maven.repository.ArtifactRepository;
import cn.org.expect.maven.Artifact;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

/**
 * 本地仓库
 */
public interface LocalRepository extends ArtifactRepository {

    /**
     * 返回本地仓库配置信息
     *
     * @return 本地仓库配置信息
     */
    LocalRepositorySettings getSettings();

    /**
     * 返回工件在本地仓库中的目录
     *
     * @param artifact 工件信息
     * @return 工件文件
     */
    default File getParent(Artifact artifact) {
        File repository = this.getSettings().getRepository();
        if (repository != null) {
            ArrayList<String> list = new ArrayList<>(10);
            list.add(repository.getAbsolutePath());
            StringUtils.split(artifact.getGroupId(), '.', list);
            list.add(artifact.getArtifactId());
            list.add(artifact.getVersion());
            String filepath = FileUtils.joinPath(list.toArray(new String[list.size()]));
            return new File(filepath);
        }
        throw new LocalRepositoryNotFoundException("Local Repository not setup!");
    }

    /**
     * 返回本地仓库中的工件文件
     *
     * @param artifact 工件信息
     * @param ext      文件扩展名
     * @return 文件（只是路径，文件是否存在需要单独判断）
     */
    default File getFile(Artifact artifact, String ext) {
        File parent = this.getParent(artifact);
        String filename = artifact.getArtifactId() + "-" + artifact.getVersion() + "." + ext;
        return new File(parent, filename);
    }

    /**
     * 判断工件是否存在
     *
     * @param artifact 工件信息
     * @return 返回true表示工件存在 false表示不存在
     */
    default boolean exists(Artifact artifact) {
        File repository = this.getSettings().getRepository();
        if (repository != null && repository.exists() && repository.isDirectory()) {
            File parent = this.getParent(artifact);
            String filename = artifact.getArtifactId() + "-" + artifact.getVersion() + ".";
            File[] files = parent.listFiles(file -> //
                    file.exists() && file.isFile() && file.length() > 0 //
                            && file.getName().startsWith(filename) //
                            && StringUtils.inArrayIgnoreCase(file.getName().substring(filename.length()), Artifact.EXTENSION_TYPES) //
            );
            return files != null && files.length > 0;
        }
        return false;
    }

    /**
     * 返回工件在本地仓库中的 jar 文件
     *
     * @param artifact 工件信息
     * @return 返回 jar 文件
     */
    default File getJarfile(Artifact artifact) {
        File repository = this.getSettings().getRepository();
        if (repository != null && repository.exists() && repository.isDirectory()) {
            File parent = this.getParent(artifact);
            String filename = artifact.getArtifactId() + "-" + artifact.getVersion() + ".";
            File[] files = parent.listFiles(file -> file.exists() && file.isFile() && file.getName().startsWith(filename) && file.getName().substring(filename.length()).equalsIgnoreCase("jar"));
            if (files != null && files.length == 1) {
                return files[0];
            }
        }
        return null;
    }
}
