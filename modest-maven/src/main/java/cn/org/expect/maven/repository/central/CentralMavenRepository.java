package cn.org.expect.maven.repository.central;

import java.util.List;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.AbstractArtifactRepository;
import cn.org.expect.maven.repository.Artifact;
import cn.org.expect.maven.repository.ArtifactOperation;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.repository.impl.ArtifactSearchResultType;
import cn.org.expect.maven.repository.impl.SimpleArtifactSearchResult;
import cn.org.expect.util.StringUtils;

/**
 * 中央仓库
 */
@EasyBean(value = "central", priority = Integer.MAX_VALUE)
public class CentralMavenRepository extends AbstractArtifactRepository {

    protected PatternSearchResultAnalysis pattern;

    protected ExtraSearchResultAnalysis extra;

    public CentralMavenRepository(EasyContext ioc) {
        super(ioc);
        this.pattern = new PatternSearchResultAnalysis();
        this.extra = new ExtraSearchResultAnalysis();
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
        return "https://repo1.maven.org/maven2/";
    }

    public ArtifactSearchResult query(String pattern, int start) throws Exception {
        this.terminate = false;
        String url = "https://search.maven.org/solrsearch/select?q=" + this.escape(pattern) + "&rows=200&wt=json&start=" + (start - 1); // 构建请求 URL
        String responseBody = this.sendRequest(url);
        if (StringUtils.isBlank(responseBody) || this.terminate) {
            return null;
        }

        ArtifactSearchResult result = this.pattern.parse(responseBody);
        result.sortByPattern();
        return result;
    }

    public ArtifactSearchResult query(String groupId, String artifactId) throws Exception {
        this.terminate = false;
        String url = "https://search.maven.org/solrsearch/select?q=g:" + groupId + "+AND+a:" + artifactId + "&core=gav&rows=200&wt=json"; // 构建请求 URL
        String responseBody = this.sendRequest(url);
        if (this.terminate) {
            return null;
        }

        ArtifactSearchResult result = this.extra.parse(responseBody);
        List<Artifact> list = result.getList();

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

                ArtifactSearchResult next = this.extra.parse(responseBody);
                list.addAll(next.getList());
                start = next.getStart();
            } while (result.getFoundNumber() > start);
            result.sortByTimeDesc();
            return new SimpleArtifactSearchResult(ArtifactSearchResultType.LIMIT_PAGE, list, start, result.getFoundNumber(), System.currentTimeMillis(), false);
        } else {
            result.sortByTimeDesc();
            return result;
        }
    }

    public String escape(String str) {
        StringBuilder buf = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (StringUtils.inArray(c, '%', '^', '[', ']', '{', '}', '|', '`', '_')) {
                continue;
            } else if (c == '.') {
                buf.append("%2E");
            } else if (c == '\'') {
                buf.append("%27");
            } else if (c == '+') {
                buf.append("%2B");
            } else if (c == '&') {
                buf.append("%26");
            } else if (c == ' ') {
                buf.append("%20");
            } else if (StringUtils.isLetter(c) || StringUtils.isNumber(c)) {
                buf.append(c);
            } else if (StringUtils.inArray(c, '(', ')', '~', '!', '@', '#', '$', '*', '=', ';', ':', ',', '<', '>', '.', '/', '?', '"', '-')) {
                buf.append(c);
            }
        }
        return buf.toString();
    }
}