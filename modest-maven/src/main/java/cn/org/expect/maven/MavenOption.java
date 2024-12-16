package cn.org.expect.maven;

/**
 * 选项信息接口
 */
public interface MavenOption {

    /**
     * 选项值
     *
     * @return 选项值
     */
    String value();

    /**
     * 选项说明
     *
     * @return 选项说明
     */
    String getDisplayName();
}
