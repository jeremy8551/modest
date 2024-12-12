package cn.org.expect.maven.concurrent;

import java.util.List;

import cn.org.expect.maven.impl.SimpleArtifactSearchResult;
import cn.org.expect.maven.repository.ArtifactRepositoryDatabase;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.search.ArtifactSearch;
import cn.org.expect.maven.MavenMessage;

public class ArtifactSearchMoreJob extends ArtifactSearchPatternJob {

    public ArtifactSearchMoreJob() {
        super("", false);
        this.description = MavenMessage.get("maven.search.job.search.more.description");
    }

    public int execute() throws Exception {
        ArtifactSearch search = this.getSearch();
        String pattern = search.getContext().getSearchText();

        if (log.isDebugEnabled()) {
            log.debug("{} search more: {}", this.getName(), pattern);
        }

        ArtifactRepositoryDatabase database = search.getDatabase();
        ArtifactSearchResult result = database.select(pattern);
        if (result != null && result.isHasMore()) { // 还有未加载的数据
            int start = result.getStart();
            int foundNumber = result.getFoundNumber();
            List<Artifact> list = result.getList();

            ArtifactSearchResult next = this.getRemoteRepository().query(pattern, start);
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

                SimpleArtifactSearchResult newResult = new SimpleArtifactSearchResult(result.getType(), list, next.getStart(), Math.max(list.size(), foundNumber), System.currentTimeMillis(), hasMore);
                database.insert(pattern, newResult); // 保存到数据库
                search.getContext().setSearchResult(newResult); // 保存查询记录
                search.asyncDisplay();
            }
        }
        return 0;
    }
}
