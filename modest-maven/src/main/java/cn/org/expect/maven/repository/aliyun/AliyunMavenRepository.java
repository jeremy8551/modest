package cn.org.expect.maven.repository.aliyun;

import java.net.UnknownHostException;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.AbstractArtifactRepository;
import cn.org.expect.maven.repository.ArtifactOperation;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.repository.central.CentralMavenRepository;

/**
 * 阿里云仓库
 */
@EasyBean(value = "query.use.aliyun", priority = 0)
public class AliyunMavenRepository extends AbstractArtifactRepository {

    protected AliyunMavenRepositoryJsonAnalysis parse;

    public AliyunMavenRepository(EasyContext ioc) {
        super(ioc, AliyunMavenRepositoryDatabaseEngine.class);
        this.parse = new AliyunMavenRepositoryJsonAnalysis();
    }

    public ArtifactOperation getSupported() {
        return new ArtifactOperation() {

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
        return "https://maven.aliyun.com/repository/public";
    }

    public ArtifactSearchResult query(String pattern, int start) throws UnknownHostException {
        this.terminate = false;
        String url = "https://developer.aliyun.com/artifact/aliyunMaven/searchArtifactByWords?repoId=all&queryTerm=" + CentralMavenRepository.escape(pattern) + "&_input_charset=utf-8";
        String responseBody = this.sendRequest(url);
        if (responseBody == null) {
            return null;
        }
        return this.parse.parsePattern(responseBody);
    }

    public ArtifactSearchResult query(String groupId, String artifactId) throws UnknownHostException {
        this.terminate = false;
        String url = "https://developer.aliyun.com/artifact/aliyunMaven/searchArtifactByGav?groupId=" + CentralMavenRepository.escape(groupId) + "&artifactId=" + CentralMavenRepository.escape(artifactId) + "&version=&repoId=all&_input_charset=utf-8";
        String responseBody = this.sendRequest(url);
        if (responseBody == null) {
            return null;
        }
        return this.parse.parseExtra(responseBody);
    }
}
