package cn.org.expect.maven.repository.central;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.concurrent.EasyJobReaderImpl;
import cn.org.expect.concurrent.EasyJobService;
import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.concurrent.MavenJob;
import cn.org.expect.maven.repository.impl.SimpleArtifactSearchResult;
import cn.org.expect.maven.repository.AbstractArtifactRepository;
import cn.org.expect.maven.repository.ArtifactOperation;
import cn.org.expect.maven.repository.ArtifactRepositoryDatabaseEngine;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.repository.ArtifactSearchResultType;
import cn.org.expect.maven.repository.HttpClient;
import cn.org.expect.util.StringUtils;

/**
 * 中央仓库
 */
@EasyBean(value = "query.use.central", priority = Integer.MAX_VALUE)
public class CentralMavenRepository extends AbstractArtifactRepository {

    /** 分析工具 */
    private final CentralMavenRepositoryAnalysis analysis;

    /** 行数 */
    protected int rows = 200;

    public CentralMavenRepository(EasyContext ioc, Class<? extends ArtifactRepositoryDatabaseEngine> cls) {
        super(ioc, cls);
        this.analysis = new CentralMavenRepositoryAnalysis();
    }

    public CentralMavenRepository(EasyContext ioc) {
        this(ioc, CentralMavenRepositoryDatabaseEngine.class);
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
        String url = "https://search.maven.org/solrsearch/select?q=" + escape(pattern) + "&rows=" + this.rows + "&wt=json&start=" + (start - 1); // 构建请求 URL
        String responseBody = this.sendRequest(url);
        if (responseBody == null) {
            return null;
        } else {
            return this.analysis.parsePatternResult(responseBody).sortByPattern();
        }
    }

    public ArtifactSearchResult query(String groupId, String artifactId) throws Exception {
        this.terminate = false;

        String url = "https://search.maven.org/solrsearch/select?q=g:" + escape(groupId) + "+AND+a:" + escape(artifactId) + "&core=gav&rows=" + this.rows + "&wt=json"; // 构建请求 URL
        String responseBody = this.sendRequest(url);
        if (responseBody == null) {
            return null;
        }

        ArtifactSearchResult result = this.analysis.parseExtraResult(responseBody);

        // 使用线程池，并发查询工件所有版本
        ThreadSource threadSource = this.getEasyContext().getBean(ThreadSource.class);
        EasyJobService service = threadSource.getJobService(7);
        List<CentralMavenJob> jobList = new ArrayList<>();
        int start = result.size(); // 起始位置
        while (result.getFoundNumber() > start) { // 拆分任务
            jobList.add(new CentralMavenJob(url, start, this.analysis, result.getList()));
            start += this.rows + 1;
        }
        service.execute(new EasyJobReaderImpl(jobList)); // 提交到线程池，等待执行完毕

        // 搜索结果
        return new SimpleArtifactSearchResult(CentralMavenRepository.class.getName(), ArtifactSearchResultType.ALL, result.sortByTime().getList(), start, result.getFoundNumber(), System.currentTimeMillis(), false);
    }

    public static class CentralMavenJob extends MavenJob {

        private final String url;

        private final int start;

        private final CentralMavenRepositoryAnalysis analysis;

        private final List<Artifact> list;

        private final HttpClient client = new HttpClient();

        public CentralMavenJob(String url, int start, CentralMavenRepositoryAnalysis analysis, List<Artifact> list) {
            super("");
            this.url = url;
            this.start = start;
            this.analysis = analysis;
            this.list = list;
        }

        public int execute() throws Exception {
            String responseBody = this.client.sendRequest(this.url + "&start=" + this.start);
            if (responseBody == null) {
                return -1;
            } else {
                ArtifactSearchResult next = this.analysis.parseExtraResult(responseBody);
                synchronized (this.list) {
                    this.list.addAll(next.getList());
                }
                return 0;
            }
        }

        public void terminate() {
            super.terminate();
            this.client.terminate();
        }
    }
}
