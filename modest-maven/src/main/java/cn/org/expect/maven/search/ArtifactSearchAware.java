package cn.org.expect.maven.search;

public interface ArtifactSearchAware {

    /**
     * 设置搜索接口
     *
     * @param search 搜索接口
     */
    void setSearch(ArtifactSearch search);
}
