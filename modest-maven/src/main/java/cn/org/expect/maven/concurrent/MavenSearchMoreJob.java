package cn.org.expect.maven.concurrent;

import java.util.List;

import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.impl.SimpleMavenSearchResult;
import cn.org.expect.maven.search.MavenSearch;
import cn.org.expect.maven.repository.MavenRepositoryDatabase;
import cn.org.expect.util.StringUtils;

public class MavenSearchMoreJob extends MavenSearchPatternJob {

    public MavenSearchMoreJob(String pattern) {
        super(pattern);
    }

    public int execute() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("{} search more: {}", this.getName(), this.pattern);
        }

        MavenSearch search = this.getSearch();
        MavenRepositoryDatabase database = search.getDatabase();
        MavenSearchResult result = database.select(this.pattern);
        if (result != null && result.getFoundNumber() > result.size()) { // 还有未加载的数据
            int start = result.getStart();
            int foundNumber = result.getFoundNumber();
            List<MavenArtifact> list = result.getList();

            MavenSearchResult next = this.getRemoteRepository().query(StringUtils.trimBlank(StringUtils.replaceAll(this.pattern, ".", "%2E")), start);
            if (next != null) {
                list.addAll(next.getList());
                SimpleMavenSearchResult newResult = new SimpleMavenSearchResult(list, next.getStart(), foundNumber);
                database.insert(this.pattern, newResult); // 保存到数据库
                search.getContext().setSearchResult(newResult); // 保存查询记录
                search.showSearchResult();
            }
        }
        return 0;
    }
}
