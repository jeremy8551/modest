package cn.org.expect.maven.repository.central;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.concurrent.EasyJobReaderImpl;
import cn.org.expect.concurrent.EasyJobService;
import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.intellij.idea.plugin.maven.concurrent.EDTJob;
import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenSearchPluginJob;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.AbstractArtifactRepository;
import cn.org.expect.maven.repository.Artifact;
import cn.org.expect.maven.repository.ArtifactOperation;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.repository.HttpClient;
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

    public static String escape(String str) {
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

    public ArtifactSearchResult query(String pattern, int start) throws Exception {
        this.terminate = false;
        String url = "https://search.maven.org/solrsearch/select?q=" + escape(pattern) + "&rows=200&wt=json&start=" + (start - 1); // 构建请求 URL
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

        int rows = 200;
        String url = "https://search.maven.org/solrsearch/select?q=g:" + escape(groupId) + "+AND+a:" + escape(artifactId) + "&core=gav&rows=" + rows + "&wt=json"; // 构建请求 URL
        String responseBody = this.sendRequest(url);
        if (StringUtils.isBlank(responseBody) || this.terminate) {
            return null;
        }

        ArtifactSearchResult result = this.extra.parse(responseBody);
        List<Artifact> list = result.getList();

        ThreadSource threadSource = this.getEasyContext().getBean(ThreadSource.class);
        EasyJobService service = threadSource.getJobService(7);

        List<CentralMavenJob> jobList = new ArrayList<>();
        int start = result.size(); // 起始位置
        while (result.getFoundNumber() > start) {
            jobList.add(new CentralMavenJob(url, start, this.extra, list));
            start += rows + 1;
        }
        service.execute(new EasyJobReaderImpl(jobList));

        result.sortByTimeDesc();
        return new SimpleArtifactSearchResult(ArtifactSearchResultType.LIMIT_PAGE, list, start, result.getFoundNumber(), System.currentTimeMillis(), false);
    }

    static class CentralMavenJob extends MavenSearchPluginJob implements EDTJob {

        private final String url;

        private final int start;

        private final ExtraSearchResultAnalysis extra;

        private final List<Artifact> list;

        private final HttpClient client = new HttpClient();

        public CentralMavenJob(String url, int start, ExtraSearchResultAnalysis extra, List<Artifact> list) {
            super();
            this.url = url;
            this.start = start;
            this.extra = extra;
            this.list = list;
        }

        public int execute() throws Exception {
            String responseBody = this.client.sendRequest(this.url + "&start=" + this.start);
            if (StringUtils.isBlank(responseBody) || this.terminate) {
                return -1;
            } else {
                ArtifactSearchResult next = this.extra.parse(responseBody);
                list.addAll(next.getList());
                return 0;
            }
        }

        public void terminate() {
            super.terminate();
            this.client.terminate();
        }

        public boolean isTerminate() {
            return super.isTerminate();
        }
    }
}
