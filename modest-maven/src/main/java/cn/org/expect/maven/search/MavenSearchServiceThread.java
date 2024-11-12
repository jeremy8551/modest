package cn.org.expect.maven.search;

import java.util.List;

import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.impl.SimpleMavenSearchResult;
import cn.org.expect.maven.search.db.MavenSearchDatabase;
import cn.org.expect.util.StringUtils;

/**
 * 立即执行模糊查询与精确查询
 */
public class MavenSearchServiceThread extends AbstractSearchThread<Object> {

    public MavenSearchServiceThread() {
        super();
    }

    /**
     * 执行 more 按钮对应的模糊查询
     *
     * @param search  Maven工具
     * @param pattern 字符串
     */
    public void searchMore(MavenSearch search, String pattern) {
        if (StringUtils.isNotBlank(pattern)) {
            String message = MavenSearchMessage.get("maven.search.pattern.text", StringUtils.escapeLineSeparator(pattern));
            search.setStatusbarText(MavenSearchAdvertiser.RUNNING, message);

            try {
                this.queue.put(new SearchElementMore(search, pattern));
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * 执行精确查询
     *
     * @param search     搜索接口
     * @param groupId    域名
     * @param artifactId 工件名
     */
    public void searchExtra(MavenSearch search, String groupId, String artifactId) {
        if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(artifactId)) {
            String message = MavenSearchMessage.get("maven.search.extra.text", groupId, artifactId);
            search.setStatusbarText(MavenSearchAdvertiser.RUNNING, message);

            try {
                this.queue.put(new SearchElementExtra(search, groupId, artifactId));
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    public void run() {
        String name = MavenSearchServiceThread.class.getSimpleName();
        log.info(MavenSearchMessage.get("maven.search.thread.start", name));
        while (this.notTerminate) {
            try {
                Object object = this.queue.take();

                // 精确查询
                if (object instanceof SearchElementExtra) {
                    SearchElementExtra element = (SearchElementExtra) object;
                    String groupId = element.getGroupId();
                    String artifactId = element.getArtifactId();
                    MavenSearch search = element.getSearch();

                    if (log.isDebugEnabled()) {
                        log.debug("{} search groupId: {}, artifactId: {} ..", name, groupId, artifactId);
                    }

                    this.searching = element;
                    if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(artifactId)) {
                        MavenSearchResult result;
                        try {
                            result = this.searchExtra(search.getDatabase(), groupId, artifactId);
                        } finally {
                            this.searching = null;
                        }

                        if (result != null) {
                            search.repaintSearchResult();
                        }
                    }
                    continue;
                }

                // more 按钮的模糊查询操作
                if (object instanceof SearchElementMore) {
                    SearchElementMore element = (SearchElementMore) object;
                    MavenSearch search = element.getSearch();
                    String pattern = element.getPattern();

                    if (log.isDebugEnabled()) {
                        log.debug("{} search more: {}", name, pattern);
                    }

                    MavenSearchDatabase database = search.getDatabase();
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
                            search.getContext().setSearchResult(newResult); // 保存查询记录
                            search.repaintMoreSearchResult(newResult); // 重新渲染
                        }
                    }
                    continue;
                }
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    private MavenSearchResult searchExtra(MavenSearchDatabase database, String groupId, String artifactId) {
        if (StringUtils.isBlank(groupId) || StringUtils.isBlank(artifactId)) {
            return null;
        }

        groupId = StringUtils.trimBlank(groupId);
        artifactId = StringUtils.trimBlank(artifactId);

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
            if (object instanceof SearchElementExtra) {
                SearchElementExtra element = (SearchElementExtra) object;
                if (groupId.equals(element.getGroupId()) && artifactId.equals(element.getArtifactId())) {
                    return true;
                }
            }
        }

        SearchElementExtra element = this.searching;
        return element != null && groupId.equals(element.getGroupId()) && artifactId.equals(element.getArtifactId());
    }
}
