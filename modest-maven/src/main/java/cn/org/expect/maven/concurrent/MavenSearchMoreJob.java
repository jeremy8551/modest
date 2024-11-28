package cn.org.expect.maven.concurrent;

import java.util.List;

import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenRepositoryDatabase;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.impl.SimpleMavenSearchResult;
import cn.org.expect.maven.search.MavenSearch;

public class MavenSearchMoreJob extends MavenSearchPatternJob {

    public MavenSearchMoreJob() {
        super("", false);
    }

    public int execute() throws Exception {
        MavenSearch search = this.getSearch();
        String pattern = search.getContext().getSearchText();

        if (log.isDebugEnabled()) {
            log.debug("{} search more: {}", this.getName(), pattern);
        }

        MavenRepositoryDatabase database = search.getDatabase();
        MavenSearchResult result = database.select(pattern);
        if (result != null && result.getFoundNumber() > result.size()) { // 还有未加载的数据
            int start = result.getStart();
            int foundNumber = result.getFoundNumber();
            List<MavenArtifact> list = result.getList();

            MavenSearchResult next = this.getRemoteRepository().query(pattern, start);
            if (next != null) {
                list.addAll(next.getList());
                SimpleMavenSearchResult newResult = new SimpleMavenSearchResult(list, next.getStart(), foundNumber, System.currentTimeMillis());
                newResult.reset();
                database.insert(pattern, newResult); // 保存到数据库
                search.getContext().setSearchResult(newResult); // 保存查询记录
                search.asyncDisplay();
            }
        }
        return 0;
    }
}
