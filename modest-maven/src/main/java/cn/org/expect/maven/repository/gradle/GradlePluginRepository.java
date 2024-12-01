package cn.org.expect.maven.repository.gradle;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.AbstractArtifactRepository;
import cn.org.expect.maven.repository.Artifact;
import cn.org.expect.maven.repository.ArtifactOperation;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.repository.central.PatternSearchResultAnalysis;
import cn.org.expect.maven.repository.impl.MavenArtifactImpl;
import cn.org.expect.maven.repository.impl.SimpleMavenSearchResult;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;

/**
 * gradle插件仓库
 */
@EasyBean(value = "gradle", priority = 1)
public class GradlePluginRepository extends AbstractArtifactRepository {

    protected PatternSearchResultAnalysis pattern;

    protected GradlePluginResultAnalysis analysis;

    public GradlePluginRepository(EasyContext ioc) {
        super(ioc);
        this.pattern = new PatternSearchResultAnalysis();
        this.analysis = new GradlePluginResultAnalysis();
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
        };
    }

    public String getAddress() {
        return "https://plugins.gradle.org/";
    }

    public String escape(String str) {
        str = StringUtils.replaceAll(str, "%", "%25");
        str = StringUtils.replaceAll(str, "+", "%2B");
        str = StringUtils.replaceAll(str, "|", "%21");
        str = StringUtils.replaceAll(str, "@", "%40");
        str = StringUtils.replaceAll(str, "$", "%24");
        str = StringUtils.replaceAll(str, "^", "%5E");
        str = StringUtils.replaceAll(str, "&", "%26");
        str = StringUtils.replaceAll(str, "(", "%28");
        str = StringUtils.replaceAll(str, ")", "%29");
        str = StringUtils.replaceAll(str, "=", "%3D");
        str = StringUtils.replaceAll(str, "[", "%5B");
        str = StringUtils.replaceAll(str, "]", "%5D");
        str = StringUtils.replaceAll(str, "{", "%7B");
        str = StringUtils.replaceAll(str, "}", "%7D");
        str = str.replace(' ', '+');
        try {
            return URLEncoder.encode(str, CharsetName.UTF_8);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
            return str;
        }
    }

    public ArtifactSearchResult query(String pattern, int start) throws Exception {
        this.terminate = false;
        String responseBody = this.sendRequest("https://plugins.gradle.org/search?term=" + escape(pattern) + "&page=" + (start - 1));
        if (StringUtils.isBlank(responseBody) || this.terminate) {
            return null;
        } else {
            return this.analysis.parsePatternResult(responseBody);
        }
    }

    public ArtifactSearchResult query(String groupId, String artifactId) throws Exception {
        this.terminate = false;
        String responseBody = this.sendRequest("https://plugins.gradle.org/m2/" + artifactId.replace('.', '/') + "/" + artifactId + ".gradle.plugin/");
        if (this.terminate || StringUtils.isBlank(responseBody)) {
            return null;
        }

        List<MavenArtifactImpl> list = this.analysis.parseExtraResult(groupId, artifactId, responseBody);
        if (list == null) {
            return null;
        }

        List<Artifact> result = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            MavenArtifactImpl artifact = list.get(i);
            result.add(artifact);

            String html = this.sendRequest("https://plugins.gradle.org/plugin/" + artifactId + "/" + artifact.getVersion());
            Matcher compile = StringUtils.compile(html, "Created ([^\\.]+)\\.");
            if (compile != null) {
                String date = StringUtils.trimBlank(compile.group(1));
                artifact.setTimestamp(Dates.parse(date));
            }
        }
        return new SimpleMavenSearchResult(result, list.size() + 1, list.size(), System.currentTimeMillis());
    }
}
