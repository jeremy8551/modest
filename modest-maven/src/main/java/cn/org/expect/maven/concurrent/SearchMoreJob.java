package cn.org.expect.maven.concurrent;

import java.util.List;

import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.MavenMessage;
import cn.org.expect.maven.repository.impl.SimpleArtifactSearchResult;
import cn.org.expect.maven.repository.ArtifactRepositoryDatabase;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.search.ArtifactSearch;

public class SearchMoreJob extends SearchPatternJob {

    private final String pattern;

    public SearchMoreJob(String pattern) {
        super(pattern);
        this.description = MavenMessage.get("maven.search.job.search.more.description");
        this.pattern = pattern;
    }

    public int execute() throws Exception {
        ArtifactSearch search = this.getSearch();

        if (log.isDebugEnabled()) {
            log.debug("{} search more: {}", this.getName(), this.pattern);
        }

        ArtifactRepositoryDatabase database = search.getDatabase();
        ArtifactSearchResult result = database.select(this.pattern);
        if (result != null && result.isHasMore()) { // 还有未加载的数据
            int start = result.getStart();
            int foundNumber = result.getFoundNumber();
            List<Artifact> list = result.getList();

            ArtifactSearchResult next = this.getRemoteRepository().query(this.pattern, start);
            if (next != null) {
                List<Artifact> nextList = next.getList();
                list.addAll(nextList);

                boolean hasMore = true;
                switch (result.getType()) {
                    case ALL:
                        hasMore = false;
                        break;
                    case LIMIT_PAGE:
                        hasMore = foundNumber > list.size();
                        break;
                    case NO_TOTAL:
                        hasMore = !nextList.isEmpty();
                        break;
                }

                SimpleArtifactSearchResult newResult = new SimpleArtifactSearchResult(result.getRepositoryName(), result.getType(), list, next.getStart(), Math.max(list.size(), foundNumber), System.currentTimeMillis(), hasMore);
                database.insert(this.pattern, newResult); // 保存到数据库
                search.saveSearchResult(newResult);
                search.asyncDisplay();
            }
        }
        return 0;
    }
}
