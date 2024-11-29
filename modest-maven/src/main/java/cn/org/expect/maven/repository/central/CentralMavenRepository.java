package cn.org.expect.maven.repository.central;

import java.util.List;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenArtifactOperation;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.AbstractMavenRepository;
import cn.org.expect.maven.repository.impl.SimpleMavenSearchResult;
import cn.org.expect.util.StringUtils;

/**
 * 中央仓库
 */
@EasyBean("central")
public class CentralMavenRepository extends AbstractMavenRepository {
    protected final static Log log = LogFactory.getLog(CentralMavenRepository.class);

    protected PatternSearchResultAnalysis pattern;

    protected ExtraSearchResultAnalysis extra;

    public CentralMavenRepository(EasyContext ioc) {
        super(ioc);
        this.pattern = new PatternSearchResultAnalysis();
        this.extra = new ExtraSearchResultAnalysis();
    }

    public MavenArtifactOperation getSupported() {
        return new MavenArtifactOperation() {

            public boolean supportOpenInCentralRepository() {
                return true;
            }

            public boolean supportDownload() {
                return true;
            }

            public boolean supportDelete() {
                return true;
            }

            public boolean supportOpenInFileSystem() {
                return true;
            }
        };
    }

    public String getAddress() {
        return "https://repo1.maven.org/maven2/";
    }

    public MavenSearchResult query(String pattern, int start) {
        this.terminate = false;
        String url = "https://search.maven.org/solrsearch/select?q=" + StringUtils.trimBlank(StringUtils.replaceAll(pattern, ".", "%2E")) + "&rows=200&wt=json&start=" + (start - 1); // 构建请求 URL
        String responseBody = this.sendRequest(url);

        if (StringUtils.isBlank(responseBody) || this.terminate) {
            return null;
        }

        MavenSearchResult result = this.pattern.parse(responseBody);
        result.getList().sort(PATTERN_RESULT_COMPARATOR.reversed());
        return result;
    }

    public MavenSearchResult query(String groupId, String artifactId) {
        this.terminate = false;
        String url = "https://search.maven.org/solrsearch/select?q=g:" + groupId + "+AND+a:" + artifactId + "&core=gav&rows=200&wt=json"; // 构建请求 URL
        String responseBody = this.sendRequest(url);
        if (this.terminate) {
            return null;
        }

        MavenSearchResult result = this.extra.parse(responseBody);
        List<MavenArtifact> list = result.getList();

        int start = result.size(); // 起始位置
        if (result.getFoundNumber() > start) {
            do {
                if (this.terminate) {
                    break;
                }

                responseBody = this.sendRequest(url + "&start=" + start);

                if (this.terminate) {
                    break;
                }

                MavenSearchResult next = this.extra.parse(responseBody);
                list.addAll(next.getList());
                start = next.getStart();
            } while (result.getFoundNumber() > start);
            list.sort(EXTRA_RESULT_COMPARATOR);
            return new SimpleMavenSearchResult(list, start, result.getFoundNumber(), System.currentTimeMillis());
        } else {
            list.sort(EXTRA_RESULT_COMPARATOR);
            return result;
        }
    }
}
