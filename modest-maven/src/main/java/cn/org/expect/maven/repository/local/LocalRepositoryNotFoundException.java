package cn.org.expect.maven.repository.local;

/**
 * 未设置本地仓库
 */
public class LocalRepositoryNotFoundException extends IllegalArgumentException {

    public LocalRepositoryNotFoundException(String s) {
        super(s);
    }
}
