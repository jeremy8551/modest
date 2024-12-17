package cn.org.expect.maven.repository.clazz;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.repository.impl.SimpleArtifactSearchResult;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.repository.ArtifactSearchResultType;
import cn.org.expect.maven.repository.central.CentralMavenRepositoryAnalysis;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 用于解析模糊查询返回的 Json 字符串
 */
public class SearchInClassRepositoryAnalysis {
    protected final static Log log = LogFactory.getLog(SearchInClassRepositoryAnalysis.class);

    public ArtifactSearchResult parse(String responseBody) {
        try {
            return this.parse0(responseBody);
        } catch (Exception e) {
            log.error("responseBody: {}", responseBody);
            throw e;
        }
    }

    private SimpleArtifactSearchResult parse0(String responseBody) {
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject response = jsonObject.getJSONObject("response");
        int numFound = response.getInt("numFound"); // 总记录数
        int start = response.getInt("start"); // 起始位置，从0开始
        JSONArray docs = response.getJSONArray("docs");

        if (log.isDebugEnabled()) {
            log.debug("send Response, find: {}, return {}, response: {}", numFound, docs.length(), responseBody);
        }

        List<Artifact> list = new ArrayList<>(docs.length());
        for (int i = 0; i < docs.length(); i++) {
            JSONObject json = docs.getJSONObject(i);
            Artifact item = CentralMavenRepositoryAnalysis.parseExtraResult(json);
            list.add(item);
        }
        return new SimpleArtifactSearchResult(SearchClassInRepository.class.getName(), ArtifactSearchResultType.LIMIT_PAGE, list, start + list.size() + 1, numFound, System.currentTimeMillis(), numFound > list.size());
    }
}
