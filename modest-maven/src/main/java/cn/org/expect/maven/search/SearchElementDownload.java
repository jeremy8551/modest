package cn.org.expect.maven.search;

import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.util.Ensure;

public class SearchElementDownload implements SearchElement {

    private final MavenSearch search;

    private final MavenArtifact artifact;

    public SearchElementDownload(MavenSearch search, MavenArtifact artifact) {
        this.search = Ensure.notNull(search);
        this.artifact = Ensure.notNull(artifact);
    }

    public MavenSearch getSearch() {
        return search;
    }

    public MavenArtifact getArtifact() {
        return artifact;
    }
}
