package cn.org.expect.intellijidea.plugin.maven.impl;

import java.util.List;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.MavenArtifactSet;
import cn.org.expect.util.Ensure;

public class MavenArtifactSetImpl implements MavenArtifactSet {

    private final List<MavenArtifact> list;

    /** 下次开始读取记录的位置，从1开始 */
    private int start;

    /** 总记录数 */
    private int totalRecord;

    public MavenArtifactSetImpl(List<MavenArtifact> list, int start, int totalRecord) {
        this.list = Ensure.notNull(list);
        this.start = start;
        this.totalRecord = totalRecord;
    }

    public List<MavenArtifact> getList() {
        return this.list;
    }

    public int getStart() {
        return start;
    }

    public int getFoundNumber() {
        return totalRecord;
    }

    public int size() {
        return this.list.size();
    }
}
