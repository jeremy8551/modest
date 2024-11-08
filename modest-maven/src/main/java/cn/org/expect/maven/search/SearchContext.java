package cn.org.expect.maven.search;

import cn.org.expect.maven.intellij.idea.navigation.SearchNavigationResultSet;
import cn.org.expect.maven.repository.MavenSearchResult;

public interface SearchContext {
    /**
     * 返回最后一次模糊查询的文本
     *
     * @return 文本信息
     */
    String getSearchPattern();

    /**
     * 设置最后一次模糊查询的文本
     *
     * @param searchPattern 文本信息
     */
    void setSearchPattern(String searchPattern);

    long getInputIntervalTime();

    void setInputIntervalTime(long continueInputIntervalTime);

    void setPatternSearchResult(MavenSearchResult result);

    /**
     * 返回上一次查询结果
     *
     * @return 查询结果
     */
    MavenSearchResult getPatternSearchResult();

    SearchNavigationResultSet getNavigationResultSet();

    void setNavigationResultSet(SearchNavigationResultSet navigationResultSet);
}
