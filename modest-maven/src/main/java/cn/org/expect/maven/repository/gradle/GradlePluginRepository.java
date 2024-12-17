package cn.org.expect.maven.repository.gradle;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.concurrent.EasyJob;
import cn.org.expect.concurrent.EasyJobReaderImpl;
import cn.org.expect.concurrent.EasyJobService;
import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.concurrent.MavenJob;
import cn.org.expect.maven.impl.SimpleArtifact;
import cn.org.expect.maven.repository.impl.SimpleArtifactSearchResult;
import cn.org.expect.maven.repository.AbstractArtifactRepository;
import cn.org.expect.maven.repository.ArtifactOperation;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.repository.ArtifactSearchResultType;
import cn.org.expect.maven.repository.HttpClient;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;

/**
 * gradle插件仓库
 */
@EasyBean(value = "query.use.gradle", priority = 1)
public class GradlePluginRepository extends AbstractArtifactRepository {

    protected GradlePluginRepositoryAnalysis analysis;

    public GradlePluginRepository(EasyContext ioc) {
        super(ioc, GradlePluginRepositoryDatabaseEngine.class);
        this.analysis = new GradlePluginRepositoryAnalysis();
    }

    public ArtifactOperation getSupported() {
        return new ArtifactOperation() {

            public boolean supportOpenInCentralRepository() {
                return false;
            }

            public boolean supportDownload() {
                return false;
            }

            public boolean supportDelete() {
                return false;
            }

            public boolean supportOpenInFileSystem() {
                return false;
            }

            public boolean supportCopyMavenDependency() {
                return false;
            }

            public boolean supportOpenProjectURL() {
                return false;
            }

            public boolean supportOpenIssueURL() {
                return false;
            }

            public boolean supportOpenPomFile() {
                return false;
            }
        };
    }

    public String getAddress() {
        return "https://plugins.gradle.org/";
    }

    public String escape(String str) {
        StringBuilder buf = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '!') {
                buf.append("%21");
            } else if (c == '`') {
                buf.append("%60");
            } else if (c == ';') {
                buf.append("%3B");
            } else if (c == ':') {
                buf.append("%3A");
            } else if (c == '\'') {
                buf.append("%27");
            } else if (c == '|') {
                buf.append("%7C");
            } else if (c == '\\') {
                buf.append("%5C");
            } else if (c == ',') {
                buf.append("%2C");
            } else if (c == '/') {
                buf.append("%2F");
            } else if (c == '?') {
                buf.append("%3F");
            } else if (c == '#') {
                buf.append("%23");
            } else if (c == '%') {
                buf.append("%25");
            } else if (c == '+') {
                buf.append("%2B");
            } else if (c == '@') {
                buf.append("%40");
            } else if (c == '$') {
                buf.append("%24");
            } else if (c == '^') {
                buf.append("%5E");
            } else if (c == '&') {
                buf.append("%26");
            } else if (c == '(') {
                buf.append("%28");
            } else if (c == ')') {
                buf.append("%29");
            } else if (c == '=') {
                buf.append("%3D");
            } else if (c == '[') {
                buf.append("%5B");
            } else if (c == ']') {
                buf.append("%5D");
            } else if (c == '{') {
                buf.append("%7B");
            } else if (c == '}') {
                buf.append("%7D");
            } else if (c == ' ') {
                buf.append('+');
            } else if (StringUtils.isLetter(c) || StringUtils.isNumber(c) || StringUtils.isSymbol(c)) { // - " < > .
                buf.append(c);
            }
        }
        return buf.toString();
    }

    public ArtifactSearchResult query(String pattern, int start) throws Exception {
        this.terminate = false;
        String responseBody = this.sendRequest("https://plugins.gradle.org/search?term=" + this.escape(pattern) + "&page=" + (start - 1));
        if (responseBody == null) {
            return null;
        } else {
            return this.analysis.parsePatternResult(responseBody, start);
        }
    }

    public ArtifactSearchResult query(String groupId, String artifactId) throws Exception {
        this.terminate = false;
        String responseBody = this.sendRequest("https://plugins.gradle.org/m2/" + artifactId.replace('.', '/') + "/" + artifactId + ".gradle.plugin/");
        if (responseBody == null) {
            return null;
        }

        List<SimpleArtifact> list = this.analysis.parseExtraResult(groupId, artifactId, responseBody);
        if (list == null) {
            return null;
        }

        if (log.isDebugEnabled()) {
            log.debug("{} list: {}", this.getClass().getSimpleName(), list.size());
        }

        ThreadSource threadSource = this.getEasyContext().getBean(ThreadSource.class);
        EasyJobService service = threadSource.getJobService(7);

        List<EasyJob> jobList = new ArrayList<>();
        List<Artifact> result = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            SimpleArtifact artifact = list.get(i);
            result.add(artifact);
            jobList.add(new GradleQueryJob(artifact));
        }
        service.execute(new EasyJobReaderImpl(jobList));

        // 搜索结果
        return new SimpleArtifactSearchResult(GradlePluginRepository.class.getName(), ArtifactSearchResultType.NO_TOTAL, result, list.size() + 1, list.size(), System.currentTimeMillis(), true);
    }

    static class GradleQueryJob extends MavenJob {

        private final SimpleArtifact artifact;

        private final HttpClient client = new HttpClient();

        public GradleQueryJob(SimpleArtifact artifact) {
            super("");
            this.artifact = artifact;
        }

        public int execute() throws Exception {
            String responseBody = this.client.sendRequest("https://plugins.gradle.org/plugin/" + this.artifact.getArtifactId() + "/" + this.artifact.getVersion());
            if (responseBody == null) {
                return -1;
            }

            Matcher compile = StringUtils.compile(responseBody, "Created ([^\\.]+)\\.");
            if (compile != null) {
                String date = StringUtils.trimBlank(compile.group(1));
                this.artifact.setTimestamp(Dates.parse(date));
            }
            return 0;
        }

        public void terminate() {
            super.terminate();
            this.client.terminate();
        }
    }
}
