package cn.org.expect.maven.repository.impl;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.maven.repository.Artifact;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.util.Ensure;

public class SimpleMavenSearchResult implements ArtifactSearchResult {

    private final List<Artifact> list;

    /** 下次开始读取记录的位置，从1开始 */
    private final int start;

    /** 总记录数 */
    private final int foundNumber;

    /** 查询时间 */
    private final long queryTime;

    public SimpleMavenSearchResult() {
        this(new ArrayList<>(0), 0, 0, System.currentTimeMillis());
    }

    public SimpleMavenSearchResult(List<Artifact> list, int start, int foundNumber, long queryTime) {
        this.list = Ensure.notNull(list);
        this.start = start;
        this.foundNumber = foundNumber;
        this.queryTime = queryTime;
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
}
