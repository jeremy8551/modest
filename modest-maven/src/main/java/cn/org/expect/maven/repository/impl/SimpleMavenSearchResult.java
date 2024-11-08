package cn.org.expect.maven.repository.impl;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.util.Ensure;

public class SimpleMavenSearchResult implements MavenSearchResult {

    private final List<MavenArtifact> list;

    /** 下次开始读取记录的位置，从1开始 */
    private final int start;

    /** 总记录数 */
    private final int foundNumber;

    public SimpleMavenSearchResult() {
        this(new ArrayList<>(0), 0, 0);
    }

    public SimpleMavenSearchResult(List<MavenArtifact> list, int start, int foundNumber) {
        this.list = Ensure.notNull(list);
        this.start = start;
        this.foundNumber = foundNumber;
    }

    public List<MavenArtifact> getList() {
        return this.list;
    }

    public int getStart() {
        return start;
    }

    public int getFoundNumber() {
        return foundNumber;
    }

    public int size() {
        return this.list.size();
    }
}
