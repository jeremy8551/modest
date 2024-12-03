package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.org.expect.intellij.idea.plugin.maven.IdeaMavenUtils;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.settings.DownloadWay;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.Artifact;
import cn.org.expect.maven.repository.aliyun.AliyunMavenRepository;
import cn.org.expect.maven.repository.central.CentralMavenRepository;
import cn.org.expect.maven.repository.local.LocalMavenRepositorySettings;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.NetUtils;

public class MavenSearchDownloadJob extends MavenSearchPluginJob implements EDTJob {
    private final static Log log = LogFactory.getLog(MavenSearchDownloadJob.class);

    private final Artifact artifact;

    public MavenSearchDownloadJob(Artifact artifact) {
        super();
        this.artifact = Ensure.notNull(artifact);
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public int execute() throws Exception {
        MavenSearchPlugin plugin = this.getSearch();
        DownloadWay downloadWay = plugin.getSettings().getDownloadWay();
        String centralMavenUrl = plugin.getEasyContext().getBean(CentralMavenRepository.class).getAddress();
        String listUrl = centralMavenUrl;
        switch (downloadWay) {
            case MAVEN:
                if (IdeaMavenUtils.hasSetupMavenPlugin()) {
                    IdeaMavenUtils.download(plugin, this.artifact);
                } else {
                    this.download(plugin, this.artifact, listUrl, centralMavenUrl);
                }
                break;

            case CENTRAL:
                this.download(plugin, this.artifact, listUrl, centralMavenUrl);
                break;

            case ALIYUN:
                listUrl = "https://repo.huaweicloud.com/repository/maven/";
                this.download(plugin, this.artifact, listUrl, plugin.getEasyContext().getBean(AliyunMavenRepository.class).getAddress());
                break;

            case HUAWEI:
                listUrl = "https://repo.huaweicloud.com/repository/maven/";
                this.download(plugin, this.artifact, listUrl, "https://repo.huaweicloud.com/repository/maven/");
                break;

            case TENCENT:
                listUrl = "https://repo.huaweicloud.com/repository/maven/";
                this.download(plugin, this.artifact, listUrl, "http://mirrors.cloud.tencent.com/nexus/repository/maven-public/");
                break;
        }
        return 0;
    }

    /**
     * 从仓库下载
     *
     * @param plugin      搜索接口
     * @param artifact    下载工件
     * @param listFileUrl 地址，从这查询工件的资源列表
     * @param address     下载地址
     * @throws IOException 发生错误
     */
    private void download(MavenSearchPlugin plugin, Artifact artifact, String listFileUrl, String address) throws IOException {
        String dirUrl = artifact.toURI(listFileUrl, artifact);
        String parentUrl = artifact.toURI(address, artifact);

        //
        LocalMavenRepositorySettings settings = plugin.getLocalRepositorySettings();
        File parent = plugin.getLocalRepository().getParent(artifact);
        FileUtils.createDirectory(parent, true);

        List<String> files = this.listFilename(dirUrl);
        for (String filename : files) {
            if (terminate) {
                break;
            }

            if (filename.endsWith(".asc") || filename.endsWith(".md5") || filename.endsWith(".sha1")) {
                continue;
            }

            String docFilename = artifact.getArtifactId() + "-" + artifact.getVersion() + "-javadoc.jar";
            if (!settings.isDownloadDocsAutomatically() && (filename.endsWith(docFilename) || filename.startsWith(docFilename))) {
                continue;
            }

            String sourceFilename = artifact.getArtifactId() + "-" + artifact.getVersion() + "-sources.jar";
            if (!settings.isDownloadSourcesAutomatically() && (filename.endsWith(sourceFilename) || filename.startsWith(sourceFilename))) {
                continue;
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

        if (!this.isTerminate()) {
            File remote = new File(parent, "_remote.repositories");

            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00")); // CST 是中国标准时间 (GMT+08:00)
            String formattedDate = sdf.format(new Date());

            StringBuilder buf = new StringBuilder("#NOTE: ").append(plugin.getSettings().getName()).append(" Plugin for intellij Idea").append(FileUtils.lineSeparator);
            buf.append("#").append(formattedDate).append(FileUtils.lineSeparator);
            for (String name : files) {
                if (name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".pom")) {
                    buf.append(name).append(">").append(plugin.getSettings().getRepositoryId()).append("=").append(FileUtils.lineSeparator);
                }
            }
            FileUtils.write(remote, CharsetName.UTF_8, false, buf.toString());
        }

        plugin.asyncDisplay();
    }

    /**
     * 获取指定 URL 目录下的文件列表
     */
    public List<String> listFilename(String httpUrl) throws IOException {
        // 创建一个 URL 对象并打开连接
        URL url = new URL(httpUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // 读取服务器返回的 HTML 页面内容
        List<String> fileList = new ArrayList<>();
        try (InputStream inputStream = conn.getInputStream()) {
            String html = new String(inputStream.readAllBytes());
            Pattern pattern = Pattern.compile("href=\"([^\"]+)\""); // 使用正则表达式查找 HTML 中的文件链接
            Matcher matcher = pattern.matcher(html);
            while (matcher.find()) {
                String fileName = matcher.group(1);
                String ext = FileUtils.getFilenameExt(fileName);
                if (ext.length() > 1) { // 过滤需要的文件类型
                    fileList.add(fileName);
                }
            }
        }
        return fileList;
    }
}
