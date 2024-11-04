package cn.org.expect.intellijidea.plugin.maven.impl;

import java.util.List;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.search.JsonResult;

public class JsonResultImpl implements JsonResult {

    private List<MavenArtifact> list;

    private int numFound;

    private int start;

    public JsonResultImpl(int numFound, int start, List<MavenArtifact> list) {
        this.list = list;
        this.numFound = numFound;
        this.start = start;
    }

    @Override
    public List<MavenArtifact> getList() {
        return list;
    }

    @Override
    public int getNumFound() {
        return numFound;
    }

    @Override
    public int getStart() {
        return start;
    }
}
