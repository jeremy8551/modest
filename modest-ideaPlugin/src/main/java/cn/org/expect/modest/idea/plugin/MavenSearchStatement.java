package cn.org.expect.modest.idea.plugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.org.expect.modest.idea.plugin.maven.MavenFinderQueryByCentral;
import cn.org.expect.util.StringUtils;
import com.intellij.openapi.diagnostic.Logger;

public class MavenSearchStatement {
    private static final Logger log = Logger.getInstance(MavenSearchStatement.class);

    public final static MavenSearchStatement INSTANCE = new MavenSearchStatement();

    protected final Map<String, MavenFinderResult> map;

    protected final Map<String, MavenFinderResult> map1;

    protected volatile MavenFinderResult last;

    protected final MavenFinderQuery query;

    protected MavenSearchStatement() {
        this.map = new ConcurrentHashMap<String, MavenFinderResult>();
        this.map1 = new ConcurrentHashMap<String, MavenFinderResult>();
        this.query = new MavenFinderQueryByCentral();
    }

    public synchronized MavenFinderResult query(String pattern) {
        String patternFinal = MavenFinderPattern.parse(pattern);
        if (StringUtils.isBlank(patternFinal)) {
            return null;
        }

        log.warn("search Pattern: " + patternFinal);
        MavenFinderResult result = this.map.get(patternFinal);
        if (result == null) {
            List<MavenFinderItem> list = null;
            try {
                list = this.query.execute(StringUtils.trimBlank(StringUtils.replaceAll(pattern, ".", "%2E")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (list != null && !list.isEmpty()) {
                result = new MavenFinderResult(patternFinal, list);
                this.map.put(result.getPattern(), result);
            }
        }

        if (result == null) {
            log.warn("search Pattern: " + patternFinal + ", result is null!");
        } else {
            this.last = result;
            log.warn("search Pattern: " + patternFinal + ", Size: " + result.getItems().size() + ", List: " + StringUtils.toString(result.getItems()));
        }

        return result;
    }

    public synchronized MavenFinderResult query(String groupId, String artifactId) {
        if (StringUtils.isBlank(groupId) || StringUtils.isBlank(artifactId)) {
            return null;
        }

        log.warn("search groupId: " + groupId + ", artifactId: " + artifactId);
        String key = this.toExtraSearchKey(groupId, artifactId);
        MavenFinderResult result = this.map1.get(key);
        if (result == null) {
            List<MavenFinderItem> list = null;
            try {
                list = this.query.execute(StringUtils.trimBlank(groupId), StringUtils.trimBlank(artifactId));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (list != null && !list.isEmpty()) {
                result = new MavenFinderResult(key, list);
                this.map1.put(result.getPattern(), result);
            }
        }

        if (result == null) {
            log.warn("search groupId: " + groupId + ", artifactId: " + artifactId + ", result is null!");
        } else {
            log.warn("search groupId: " + groupId + ", artifactId: " + artifactId + ", Size: " + result.getItems().size() + ", List: " + StringUtils.toString(result.getItems()));
        }

        return result;
    }

    /**
     * 返回上一次查询结果
     *
     * @return
     */
    public MavenFinderResult last() {
        return this.last;
    }

    public MavenFinderResult getResult(String pattern) {
        String patternFinal = MavenFinderPattern.parse(pattern);
        return this.map.get(patternFinal);
    }

    public MavenFinderResult getResult(String groupId, String artifactId) {
        String key = toExtraSearchKey(groupId, artifactId);
        return this.map1.get(key);
    }

    protected String toExtraSearchKey(String groupId, String artifactId) {
        return groupId + ":" + artifactId;
    }
}
