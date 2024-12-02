package cn.org.expect.maven.repository.impl;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.maven.repository.Artifact;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.util.Ensure;

public class SimpleArtifactSearchResult implements ArtifactSearchResult {

    private final ArtifactSearchResultType type;

    private final List<Artifact> list;

    /** 下次开始读取记录的位置，从1开始 */
    private final int start;

    /** 总记录数 */
    private final int foundNumber;

    /** 查询时间 */
    private final long queryTime;

    /** true表示还有未读数据，false表示已全部读取 */
    private volatile boolean hasMore;

    public SimpleArtifactSearchResult(ArtifactSearchResultType type) {
        this(type, new ArrayList<>(0), 0, 0, System.currentTimeMillis(), false);
    }

    public SimpleArtifactSearchResult(ArtifactSearchResultType type, List<Artifact> list, int start, int foundNumber, long queryTime, boolean hasMore) {
        this.type = Ensure.notNull(type);
        this.list = Ensure.notNull(list);
        this.start = start;
        this.foundNumber = foundNumber;
        this.queryTime = queryTime;
        this.hasMore = hasMore;
    }

    public ArtifactSearchResultType getType() {
        return type;
    }

    public List<Artifact> getList() {
        return this.list;
    }

    public int getStart() {
        return start;
    }

    public int getFoundNumber() {
        return foundNumber;
    }

    public long getQueryTime() {
        return queryTime;
    }

    public int size() {
        return this.list.size();
    }

    public boolean hasMore() {
        return hasMore;
    }

    public void setMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
