package cn.org.expect.maven.search;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.maven.intellij.idea.IdeaUtils;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.impl.SimpleMavenSearchResult;
import cn.org.expect.maven.search.db.MavenSearchDatabase;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.NetUtils;
import cn.org.expect.util.StringUtils;

/**
 * 立即执行模糊查询与精确查询
 */
public class MavenSearchServiceThread extends AbstractSearchThread<SearchElement> {

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

    /**
     * 下载工件
     *
     * @param search   搜索接口
     * @param artifact 工件
     */
    public void download(MavenSearch search, MavenArtifact artifact) {
        if (search != null && artifact != null) {
            String message = MavenSearchMessage.get("maven.search.download.url", artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion());
            search.setStatusbarText(MavenSearchAdvertiser.RUNNING, message);

            try {
                this.queue.put(new SearchElementDownload(search, artifact));
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    public void run() {
        log.info(MavenSearchMessage.get("maven.search.thread.start", this.getName()));
        while (!this.terminate) {
            try {
                SearchElement element = this.queue.take();
                int value;

                this.searching = element;
                try {
                    value = this.run(element);
                } finally {
                    this.searching = null;
                    this.terminate = false;
                }

                switch (value) {
                    case 0:
                        element.getSearch().repaintSearchResult();
                        break;

                    case 1:
                        element.getSearch().repaintMoreSearchResult();
                        break;
                }
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    protected int run(SearchElement object) throws Exception {
        // 精确查询
        if (object instanceof SearchElementExtra) {
            SearchElementExtra element = (SearchElementExtra) object;
            String groupId = element.getGroupId();
            String artifactId = element.getArtifactId();
            MavenSearch search = element.getSearch();

            if (log.isDebugEnabled()) {
                log.debug("{} search groupId: {}, artifactId: {} ..", this.getName(), groupId, artifactId);
            }

            if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(artifactId)) {
                MavenSearchResult result = this.searchExtra(search.getDatabase(), groupId, artifactId);
                if (result != null) {
                    return 0;
                }
            }
            return -1;
        }

        // 下载工件
        if (object instanceof SearchElementDownload) {
            SearchElementDownload element = (SearchElementDownload) object;
            MavenArtifact artifact = element.getArtifact();
            MavenSearch search = element.getSearch();

            List<String> list = new ArrayList<>();
            list.add(search.getRemoteRepository().getAddress());
            StringUtils.split(artifact.getGroupId(), '.', list);
            list.add(artifact.getArtifactId());
            list.add(artifact.getVersion());
            String parentUrl = NetUtils.joinUri(list.toArray(new String[0]));

            String filepath = search.getLocalRepository().getAddress();
            if (FileUtils.isDirectory(filepath)) {
                list.clear();
                list.add(filepath);
                StringUtils.split(artifact.getGroupId(), '.', list);
                list.add(artifact.getArtifactId());
                list.add(artifact.getVersion());
                File parent = new File(FileUtils.joinPath(list.toArray(new String[0])));
                FileUtils.createDirectory(parent);

                List<String> files = IdeaUtils.fetchFileList(parentUrl);
                for (String filename : files) {
                    if (this.terminate) {
                        break;
                    }

                    String httpUrl = NetUtils.joinUri(parentUrl, filename);
                    File downfile = new File(parent, filename);

                    if (log.isDebugEnabled()) {
                        log.debug("download {} to {} ..", httpUrl, downfile.getAbsolutePath());
                    }

                    // 创建目录，创建下载文件
                    if (FileUtils.createDirectory(parent) && FileUtils.createFile(downfile, true)) {
                        IO.write(new URL(httpUrl).openStream(), new FileOutputStream(downfile), this); // 开始下载文件
                    }

                    // 如果终止了下载文件，则需要将被终止的文件删除
                    if (this.isTerminate()) {
                        FileUtils.delete(downfile);
                    }
                }
                return 0;
            }
            return -1;
        }

        // more 按钮的模糊查询操作
        if (object instanceof SearchElementMore) {
            SearchElementMore element = (SearchElementMore) object;
            MavenSearch search = element.getSearch();
            String pattern = element.getPattern();

            if (log.isDebugEnabled()) {
                log.debug("{} search more: {}", this.getName(), pattern);
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
                    return 1;
                }
            }
            return -1;
        }

        throw new UnsupportedOperationException(object.getClass().getName());
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

        if (this.searching instanceof SearchElementExtra) {
            SearchElementExtra element = (SearchElementExtra) this.searching;
            return groupId.equals(element.getGroupId()) && artifactId.equals(element.getArtifactId());
        }

        return false;
    }

    /**
     * 判断当前是否正在下载工件
     *
     * @param artifact 工件
     * @return 返回true表示正在下载
     */
    public boolean isDownloading(MavenArtifact artifact) {
        for (Object object : this.queue) {
            if (object instanceof SearchElementDownload) {
                SearchElementDownload element = (SearchElementDownload) object;
                if (element.getArtifact().equals(artifact)) {
                    return true;
                }
            }
        }

        if (this.searching instanceof SearchElementDownload) {
            SearchElementDownload element = (SearchElementDownload) this.searching;
            return element.getArtifact().equals(artifact);
        }

        return false;
    }

    public void terminateDownloading() {
        if (this.searching instanceof SearchElementDownload) {
            this.terminate();
        }
    }
}
