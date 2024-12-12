package cn.org.expect.maven.search;

import cn.org.expect.maven.repository.ArtifactSearchResult;

/**
 * 搜索接口的上下文信息
 */
public interface ArtifactSearchContext {

    /**
     * 返回最后一次执行模糊搜索的文本
     *
     * @return 文本信息
     */
    String getSearchText();

    /**
     * 设置最后一次执行模糊搜索的文本
     *
     * @param pattern 文本信息
     */
    void setSearchText(String pattern);

    /**
     * 保存搜索结果
     *
     * @param result 搜索结果
     */
    void setSearchResult(ArtifactSearchResult result);

    /**
     * 返回已保存的搜索结果
     *
     * @return 搜索结果
     */
    ArtifactSearchResult getSearchResult();
}
