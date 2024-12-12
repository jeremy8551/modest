package cn.org.expect.maven.repository.aliyun;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.repository.ArtifactSearchResultType;
import cn.org.expect.maven.impl.SimpleArtifact;
import cn.org.expect.maven.impl.SimpleArtifactSearchResult;
import cn.org.expect.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 用于解析模糊查询返回的 Json 字符串
 */
public class AliyunMavenRepositoryJsonAnalysis {
    protected final static Log log = LogFactory.getLog(AliyunMavenRepositoryJsonAnalysis.class);

    static Comparator<Artifact> PATTERN_RESULT_COMPARATOR = (o1, o2) -> {
        int gv = o1.getGroupId().compareTo(o2.getGroupId());
        if (gv != 0) {
            return gv;
        }

        int av = o1.getArtifactId().compareTo(o2.getArtifactId());
        if (av != 0) {
            return av;
        }

        return getOrder(o2.getType()) - getOrder(o1.getType());
    };

    static Comparator<Artifact> EXTRA_RESULT_COMPARATOR = (o1, o2) -> {
        int gv = o1.getGroupId().compareTo(o2.getGroupId());
        if (gv != 0) {
            return gv;
        }

        int av = o1.getArtifactId().compareTo(o2.getArtifactId());
        if (av != 0) {
            return av;
        }

        return o2.getVersion().compareTo(o1.getVersion());
    };

    private static int getOrder(String type) {
        if (StringUtils.inArrayIgnoreCase(type, "jar", "war", "ear", "ejb", "apk", "sar", "bundle", "rpm", "module")) {
            return 100;
        } else {
            return 1;
        }
    }

    public ArtifactSearchResult parsePattern(String responseBody) {
        List<Artifact> list = this.parseList(responseBody);
        list.sort(PATTERN_RESULT_COMPARATOR);
        List<Artifact> result = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            Artifact artifact = list.get(i);
            result.add(artifact);

            boolean find = false;
            for (int j = i + 1; j < list.size(); j++) {
                Artifact next = list.get(j);
                if (!artifact.equalsId(next)) { // 按名字判断是否相等
                    i = j - 1;
                    find = true;
                    break;
                }
            }
            if (!find) {
                break;
            }
        }

        return new SimpleArtifactSearchResult(ArtifactSearchResultType.ALL, result, result.size() + 1, result.size(), System.currentTimeMillis(), false);
    }

    public SimpleArtifactSearchResult parseExtra(String responseBody) {
        List<Artifact> list = this.parseList(responseBody);
        list.sort(EXTRA_RESULT_COMPARATOR);
        List<Artifact> result = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            Artifact artifact = list.get(i);
            result.add(artifact);

            boolean find = false;
            for (int j = i + 1; j < list.size(); j++) {
                Artifact next = list.get(j);
                if (!artifact.equalsVersion(next)) { // 按版本号判断是否相等
                    i = j - 1;
                    find = true;
                    break;
                }
            }
            if (!find) {
                break;
            }
        }

        return new SimpleArtifactSearchResult(ArtifactSearchResultType.ALL, result, result.size() + 1, result.size(), System.currentTimeMillis(), false);
    }

    protected List<Artifact> parseList(String responseBody) {
        JSONArray array;
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            array = jsonObject.getJSONArray("object");
        } catch (Exception e) {
            log.error("responseBody: {}", responseBody);
            return new ArrayList<>(0);
        }

        if (log.isDebugEnabled()) {
            log.debug("send Response, find: {}, response: {}", array.length(), responseBody);
        }

        List<Artifact> list = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            Artifact artifact = this.toArtifact(object);
            if (artifact.getGroupId().contains("#") //
                    || artifact.getGroupId().contains("%") //
                    || artifact.getVersion().startsWith("unknown") //
                    || StringUtils.isBlank(artifact.getGroupId()) //
                    || StringUtils.isBlank(artifact.getArtifactId()) //
            ) {
                continue;
            }
            list.add(artifact);
        }
        return list;
    }

    public Artifact toArtifact(JSONObject json) {
        String groupId = json.getString("groupId");
        String artifactId = json.getString("artifactId");
        String version = json.getString("version");
        String packaging = json.getString("packaging");
        return new SimpleArtifact(groupId, artifactId, version, packaging, null, -1);
    }
}
