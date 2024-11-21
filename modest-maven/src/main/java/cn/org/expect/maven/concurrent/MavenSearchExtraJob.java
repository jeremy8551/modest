package cn.org.expect.maven.concurrent;

import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.search.MavenSearch;
import cn.org.expect.util.StringUtils;

public class MavenSearchExtraJob extends MavenSearchJob {

    private final String groupId;

    private final String artifactId;

    public MavenSearchExtraJob(String groupId, String artifactId) {
        super();
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

        MavenSearch search = this.getSearch();
        MavenSearchResult result = search.getDatabase().select(this.groupId, this.artifactId);
        if (result != null && !result.isExpire(search.getSettings().getExpireTimeMillis())) {
            search.showSearchResult();
            return 0;
        }

        result = this.getRemoteRepository().query(groupId, artifactId);
        if (result != null) {
            search.getDatabase().insert(groupId, artifactId, result);
            search.showSearchResult();
        }
        return 0;
    }
}
