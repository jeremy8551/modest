package cn.org.expect.maven.concurrent;

import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.search.ArtifactSearch;
import cn.org.expect.util.StringUtils;

public class ArtifactSearchExtraJob extends ArtifactSearchJob {

    private final String groupId;

    private final String artifactId;

    public ArtifactSearchExtraJob(String groupId, String artifactId) {
        super("maven.search.job.search.extra.description", groupId + ":" + artifactId);
        this.groupId = StringUtils.trimBlank(groupId);
        this.artifactId = StringUtils.trimBlank(artifactId);
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public int execute() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("{} search groupId: {}, artifactId: {} ..", this.getName(), this.groupId, this.artifactId);
        }

        ArtifactSearch search = this.getSearch();
        ArtifactSearchResult result = search.getDatabase().select(this.groupId, this.artifactId);
        if (result != null && !result.isExpire(search.getSettings().getExpireTimeMillis())) {
            search.asyncDisplay();
            return 0;
        }

        result = this.getRemoteRepository().query(this.groupId, this.artifactId);
        if (result != null) {
            search.getDatabase().insert(this.groupId, this.artifactId, result);
            search.asyncDisplay();
        }
        return 0;
    }
}
