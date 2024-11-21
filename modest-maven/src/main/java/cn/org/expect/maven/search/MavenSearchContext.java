package cn.org.expect.maven.search;

import cn.org.expect.maven.repository.MavenSearchResult;

public interface MavenSearchContext {

    /**
     * 返回最后一次模糊查询的文本
     *
     * @return 文本信息
     */
    String getSearchText();

    /**
     * 设置最后一次模糊查询的文本
     *
     * @param searchPattern 文本信息
     */
    void setSearchText(String searchPattern);

    /**
     * 保存搜索结果
     *
     * @param result 搜索结果
     */
    void setSearchResult(MavenSearchResult result);

    /**
     * 返回上一次保存的搜索结果
     *
     * @return 搜索结果
     */
    MavenSearchResult getSearchResult();
}
