package cn.org.expect.intellijidea.plugin.maven.search;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifactSet;
import cn.org.expect.intellijidea.plugin.maven.MavenFinder;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderIcon;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderMessage;
import cn.org.expect.intellijidea.plugin.maven.db.MavenArtifactDatabase;
import cn.org.expect.util.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * （按groupId 与 artifactId）精确查询使用的线程
 */
public class MavenRepositorySearchExtra extends MavenRepositorySearch<MavenRepositorySearchExtra.QueueElement> {

    public MavenRepositorySearchExtra() {
        super();
    }

    /**
     * 多线程执行精确搜索
     *
     * @param mavenFinder 搜索对象
     * @param groupId     域名
     * @param artifactId  工件名
     */
    public void search(MavenFinder mavenFinder, String groupId, String artifactId) {
        if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(artifactId)) {
            String message = MavenFinderMessage.SEARCHING_EXTRA.fill(groupId, artifactId);
            mavenFinder.setAdvertiser(message, MavenFinderIcon.BOTTOM_WAITING);

            try {
                this.queue.put(new QueueElement(mavenFinder, groupId, artifactId));
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    public void run() {
        log.warn("start MavenFinder Search Extra Thread ..");
        while (this.notTerminate) {
            try {
                QueueElement element = this.queue.take();
                String groupId = element.groupId;
                String artifactId = element.artifactId;
                MavenFinder mavenFinder = element.mavenFinder;

                if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(artifactId)) {
                    MavenArtifactSet result = this.query(mavenFinder.getDatabase(), groupId, artifactId);
                    if (result != null && this.queue.isEmpty()) { // 如果没有其他任务，则重新渲染UI
                        mavenFinder.repaint();
                    }
                }
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    public synchronized MavenArtifactSet query(@NotNull MavenArtifactDatabase database, String groupId, String artifactId) {
        if (StringUtils.isBlank(groupId) || StringUtils.isBlank(artifactId)) {
            return null;
        }

        groupId = StringUtils.trimBlank(groupId);
        artifactId = StringUtils.trimBlank(artifactId);

        log.warn("search groupId: " + groupId + ", artifactId: " + artifactId);
        MavenArtifactSet result = database.select(groupId, artifactId);
        if (result == null) {
            try {
                MavenArtifactSet list = this.getRepository().query(groupId, artifactId);
                result = database.insert(groupId, artifactId, list);
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }

        if (result == null) {
            log.warn("search groupId: " + groupId + ", artifactId: " + artifactId + ", result is null!");
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
        for (QueueElement element : this.queue) {
            if (groupId.equals(element.groupId) && artifactId.equals(element.artifactId)) {
                return true;
            }
        }
        return false;
    }

    protected static class QueueElement {
        MavenFinder mavenFinder;
        String groupId;
        String artifactId;

        public QueueElement(MavenFinder mavenFinder, String groupId, String artifactId) {
            this.mavenFinder = mavenFinder;
            this.groupId = groupId;
            this.artifactId = artifactId;
        }
    }
}
