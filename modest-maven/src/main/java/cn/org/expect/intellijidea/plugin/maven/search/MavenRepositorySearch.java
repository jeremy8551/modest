package cn.org.expect.intellijidea.plugin.maven.search;

import java.util.List;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.MavenFinder;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderIcon;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderMessage;
import cn.org.expect.intellijidea.plugin.maven.MavenSearchResult;
import cn.org.expect.intellijidea.plugin.maven.db.MavenArtifactDatabase;
import cn.org.expect.intellijidea.plugin.maven.impl.SimpleMavenSearchResult;
import cn.org.expect.util.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 立即执行模糊查询与精确查询
 */
public class MavenRepositorySearch extends AbstractMavenRepositorySearch<Object> {

    public MavenRepositorySearch() {
        super();
    }

    /**
     * 执行 more 按钮对应的模糊查询
     *
     * @param mavenFinder Maven工具
     * @param pattern     字符串
     */
    public void searchMore(MavenFinder mavenFinder, String pattern) {
        if (StringUtils.isNotBlank(pattern)) {
            String message = MavenFinderMessage.SEARCHING_PATTERN.fill(StringUtils.escapeLineSeparator(pattern));
            mavenFinder.setAdvertiser(message, MavenFinderIcon.BOTTOM_WAITING);

            try {
                this.queue.put(new MoreElement(mavenFinder, pattern));
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * 执行精确查询
     *
     * @param mavenFinder 搜索对象
     * @param groupId     域名
     * @param artifactId  工件名
     */
    public void searchExtra(MavenFinder mavenFinder, String groupId, String artifactId) {
        if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(artifactId)) {
            String message = MavenFinderMessage.SEARCHING_EXTRA.fill(groupId, artifactId);
            mavenFinder.setAdvertiser(message, MavenFinderIcon.BOTTOM_WAITING);

            try {
                this.queue.put(new ExtraElement(mavenFinder, groupId, artifactId));
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    public void run() {
        log.warn("start MavenFinder Search Extra Thread ..");
        while (this.notTerminate) {
            try {
                Object object = this.queue.take();

                // 精确查询
                if (object instanceof ExtraElement) {
                    ExtraElement element = (ExtraElement) object;
                    String groupId = element.getGroupId();
                    String artifactId = element.getArtifactId();
                    MavenFinder mavenFinder = element.getMavenFinder();

                    this.searching = element;
                    if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(artifactId)) {
                        MavenSearchResult result;
                        try {
                            result = this.searchExtra(mavenFinder.getDatabase(), groupId, artifactId);
                        } finally {
                            this.searching = null;
                        }

                        if (result != null) {
                            mavenFinder.repaint();
                        }
                    }
                    continue;
                }

                // more 按钮的模糊查询操作
                if (object instanceof MoreElement) {
                    MoreElement element = (MoreElement) object;
                    MavenFinder mavenFinder = element.getMavenFinder();
                    String pattern = element.getPattern();
                    MavenArtifactDatabase database = mavenFinder.getDatabase();

                    MavenSearchResult result = database.select(pattern);
                    if (result != null && result.getFoundNumber() > result.size()) { // 还有未加载的数据
                        int start = result.getStart();
                        int foundNumber = result.getFoundNumber();
                        List<MavenArtifact> list = result.getList();

                        MavenSearchResult next = this.getRepository().query(StringUtils.trimBlank(StringUtils.replaceAll(pattern, ".", "%2E")), start);
                        if (next != null) {
                            list.addAll(next.getList());
                            SimpleMavenSearchResult newResult = new SimpleMavenSearchResult(list, next.getStart(), foundNumber);
                            database.insert(pattern, newResult); // 保存到数据库
                            mavenFinder.getContext().setPatternSearchResult(newResult); // 保存查询记录
                            mavenFinder.repaint(newResult); // 重新渲染
                        }
                    }
                    continue;
                }
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    private MavenSearchResult searchExtra(@NotNull MavenArtifactDatabase database, String groupId, String artifactId) {
        if (StringUtils.isBlank(groupId) || StringUtils.isBlank(artifactId)) {
            return null;
        }

        groupId = StringUtils.trimBlank(groupId);
        artifactId = StringUtils.trimBlank(artifactId);

        log.warn("search groupId: " + groupId + ", artifactId: " + artifactId);
        MavenSearchResult result = database.select(groupId, artifactId);
        if (result != null) {
            return result;
        }

        try {
            result = this.getRepository().query(groupId, artifactId);
            if (result != null) {
                database.insert(groupId, artifactId, result);
                return result;
            } else {
                return null;
            }
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * 判断当前是否正在查询某个 Maven 工件
     *
     * @param groupId    域名
     * @param artifactId 工件名
     * @return 返回true表示正在查询
     */
    public boolean isSearching(String groupId, String artifactId) {
        for (Object object : this.queue) {
            if (object instanceof ExtraElement) {
                ExtraElement element = (ExtraElement) object;
                if (groupId.equals(element.getGroupId()) && artifactId.equals(element.getArtifactId())) {
                    return true;
                }
            }
        }

        ExtraElement element = this.searching;
        return element != null && groupId.equals(element.getGroupId()) && artifactId.equals(element.getArtifactId());
    }
}
