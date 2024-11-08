package cn.org.expect.maven.repository.local;

import java.io.File;

public interface LocalRepositoryConfig {

    /**
     * 返回本地仓库的目录
     *
     * @return 目录
     */
    File getRepository();
}
