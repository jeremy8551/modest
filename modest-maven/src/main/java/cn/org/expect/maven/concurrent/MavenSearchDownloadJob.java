package cn.org.expect.maven.concurrent;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenSearchRepaintJob;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.search.MavenSearch;
import cn.org.expect.maven.search.MavenSearchUtils;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.NetUtils;
import cn.org.expect.util.StringUtils;

public class MavenSearchDownloadJob extends MavenSearchJob {

    private final MavenArtifact artifact;

    public MavenSearchDownloadJob(MavenArtifact artifact) {
        super();
        this.artifact = Ensure.notNull(artifact);
    }

    public MavenArtifact getArtifact() {
        return artifact;
    }

    public int execute() throws Exception {
        MavenSearch search = this.getSearch();

        List<String> list = new ArrayList<>();
        list.add(search.getRepository().getAddress());
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

            List<String> files = MavenSearchUtils.fetchFileList(parentUrl);
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

            if (!this.terminate) {
                File remote = new File(parent, "_remote.repositories");

                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00")); // CST 是中国标准时间 (GMT+08:00)
                String formattedDate = sdf.format(new Date());

                StringBuilder buf = new StringBuilder("#NOTE: ").append(search.getSettings().getName()).append(" Plugin for intellij Idea").append(FileUtils.lineSeparator);
                buf.append("#").append(formattedDate).append(FileUtils.lineSeparator);
                for (String name : files) {
                    if (name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".pom")) {
                        buf.append(name).append(">").append(search.getSettings().getRepositoryId()).append("=").append(FileUtils.lineSeparator);
                    }
                }
                FileUtils.write(remote, CharsetName.UTF_8, false, buf.toString());
            }

            search.execute(new MavenSearchRepaintJob());
        }

        return 0;
    }
}
