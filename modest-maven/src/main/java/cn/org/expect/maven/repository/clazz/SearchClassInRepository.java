package cn.org.expect.maven.repository.clazz;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.repository.central.CentralMavenRepository;

/**
 * 按类名搜索
 */
@EasyBean(value = "query.use.class", priority = -1)
public class SearchClassInRepository extends CentralMavenRepository {

    private final SearchInClassRepositoryAnalysis analysis;

    public SearchClassInRepository(EasyContext ioc) {
        super(ioc, SearchClassInRepositoryDatabaseEngine.class);
        this.analysis = new SearchInClassRepositoryAnalysis();
    }

    public String getAddress() {
        return "https://search.maven.org/solrsearch/";
    }

    public ArtifactSearchResult query(String className, int start) throws Exception {
        this.terminate = false;
        String url = "https://search.maven.org/solrsearch/select?q=fc:" + CentralMavenRepository.escape(className) + "&rows=200&wt=json&start=" + (start - 1); // 构建请求 URL
        String responseBody = this.sendRequest(url);
        if (responseBody == null) {
            return null;
        } else {
            return this.analysis.parse(responseBody).sortByGroup();
        }
    }

    public ArtifactSearchResult query(String groupId, String artifactId) throws Exception {
        throw new UnsupportedOperationException();
    }
}
