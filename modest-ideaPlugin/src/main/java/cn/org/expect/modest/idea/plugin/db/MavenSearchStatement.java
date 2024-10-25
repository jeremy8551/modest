package cn.org.expect.modest.idea.plugin.db;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.modest.idea.plugin.MavenFinderPattern;
import cn.org.expect.modest.idea.plugin.maven.MavenFinderQueryByCentral;
import cn.org.expect.modest.idea.plugin.navigation.MavenArtifact;
import cn.org.expect.util.StringUtils;
import com.intellij.openapi.diagnostic.Logger;

public class MavenSearchStatement {
    private static final Logger log = Logger.getInstance(MavenSearchStatement.class);

    /** 单例模式 */
    public final static MavenSearchStatement INSTANCE = new MavenSearchStatement();

    /** 最近一次模糊搜索结果 */
    protected volatile MavenFinderResult last;

    /** 远程调用组件 */
    protected final MavenFinderQuery query;

    /** 正在精确查找的工件 */
    protected volatile String groupId;

    /** 正在精确查找的工件 */
    protected volatile String artifactId;

    protected MavenSearchStatement() {
        this.query = new MavenFinderQueryByCentral();
        this.groupId = "";
        this.artifactId = "";
    }

    public synchronized MavenFinderResult query(String pattern) {
        String patternFinal = MavenFinderPattern.parse(pattern);
        if (StringUtils.isBlank(patternFinal)) {
            return null;
        }

        log.warn("search Pattern: " + patternFinal);
        MavenFinderResult result = MavenFinderDB.INSTANCE.select(patternFinal);
        if (result == null) {
            List<MavenArtifact> list = null;
            try {
                if (MavenFinderPattern.isExtraSearch(patternFinal)) {
                    String[] array = StringUtils.split(patternFinal, ':');
                    List<MavenArtifact> some = this.query.execute(array[0], array[1]);
                    if (some.size() > 1) {
                        MavenFinderDB.INSTANCE.insert(array[0], array[1], some);
                        MavenArtifact last = some.get(some.size() - 1);
                        list = new ArrayList<>();
                        list.add(last);
                    } else {
                        list = some;
                    }
                } else {
                    list = this.query.execute(StringUtils.trimBlank(StringUtils.replaceAll(patternFinal, ".", "%2E")));
                }
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
            }

            if (list != null) {
                result = MavenFinderDB.INSTANCE.insert(patternFinal, list);
            }
        } else {
            List<MavenArtifact> artifacts = result.getArtifacts();
            for (MavenArtifact artifact : artifacts) {
                artifact.setFold(true);
            }
        }

        if (result == null) {
            log.warn("search Pattern: " + patternFinal + ", result is null!");
        } else {
            this.last = result;
            log.warn("search Pattern: " + patternFinal + ", Size: " + result.getArtifacts().size() + ", List: " + StringUtils.toString(result.getArtifacts()));
        }

        return result;
    }

    public synchronized MavenFinderResult query(String groupId, String artifactId) {
        if (StringUtils.isBlank(groupId) || StringUtils.isBlank(artifactId)) {
            return null;
        }

        groupId = StringUtils.trimBlank(groupId);
        artifactId = StringUtils.trimBlank(artifactId);

        log.warn("search groupId: " + groupId + ", artifactId: " + artifactId);
        MavenFinderResult result = MavenFinderDB.INSTANCE.select(groupId, artifactId);
        if (result == null) {
            try {
                this.groupId = groupId;
                this.artifactId = artifactId;

                List<MavenArtifact> list = null;
                try {
                    list = this.query.execute(groupId, artifactId);
                } catch (Exception e) {
                    log.error(e.getLocalizedMessage(), e);
                }

                if (list != null) {
                    result = MavenFinderDB.INSTANCE.insert(groupId, artifactId, list);
                }
            } finally {
                this.groupId = "";
                this.artifactId = "";
            }
        }

        if (result == null) {
            log.warn("search groupId: " + groupId + ", artifactId: " + artifactId + ", result is null!");
        } else {
            log.warn("search groupId: " + groupId + ", artifactId: " + artifactId + ", Size: " + result.getArtifacts().size() + ", List: " + StringUtils.toString(result.getArtifacts()));
        }

        return result;
    }

    /**
     * 判断当前是否正在查询某个 Maven 工件
     *
     * @param groupId    域名
     * @param artifactId 工件名
     * @return 返回true表示正在查询
     */
    public boolean isExtraQuerying(String groupId, String artifactId) {
        return this.groupId.equals(groupId) && this.artifactId.equals(artifactId);
    }

    /**
     * 返回上一次查询结果
     *
     * @return
     */
    public MavenFinderResult last() {
        return this.last;
    }
}
