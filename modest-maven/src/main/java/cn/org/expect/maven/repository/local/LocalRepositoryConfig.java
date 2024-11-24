package cn.org.expect.maven.repository.local;

import java.io.File;

public interface LocalRepositoryConfig {

    /**
     * 返回本地仓库的目录
     *
     * @return 目录
     */
    File getRepository();

    /**
     * 设置本地仓库目录
     *
     * @param dir 目录
     */
    void setRepository(File dir);
}
