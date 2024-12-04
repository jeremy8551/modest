package cn.org.expect.maven.repository.impl;

import java.io.File;
import java.io.FileNotFoundException;
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

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.Artifact;
import cn.org.expect.maven.repository.ArtifactDownloader;
import cn.org.expect.maven.repository.HttpClient;
import cn.org.expect.maven.search.ArtifactSearch;
import cn.org.expect.maven.search.ArtifactSearchSettings;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.NetUtils;
import cn.org.expect.util.StringUtils;

public abstract class AbstractArtifactDownloader extends HttpClient implements ArtifactDownloader {

    protected ArtifactSearch search;

    protected final EasyContext ioc;

    public AbstractArtifactDownloader(EasyContext ioc) {
        this.ioc = Ensure.notNull(ioc);
    }

    public void setSearch(ArtifactSearch search) {
        this.search = search;
    }

    public String getListAddress() {
        return "https://repo1.maven.org/maven2/";
    }

    public abstract String getAddress();

    public List<File> execute(Artifact artifact, File parent, boolean downloadSources, boolean downloadDocs, boolean downloadAnnotation) throws Exception {
        FileUtils.createDirectory(parent, true);
        String listFileUrl = this.getListAddress();
        String address = this.getAddress();

        String dirUrl = artifact.toURI(listFileUrl, artifact);
        String parentUrl = artifact.toURI(address, artifact);

        List<String> files = this.listFilename(dirUrl);
        for (String filename : files) {
            if (this.terminate) {
                break;
            }

            if (filename.endsWith(".asc") || filename.endsWith(".md5") || filename.endsWith(".sha1")) {
                continue;
            }

            String docFilename = artifact.getArtifactId() + "-" + artifact.getVersion() + "-javadoc.jar";
            if (!downloadDocs && filename.equals(docFilename)) {
                continue;
            }

            String sourceFilename = artifact.getArtifactId() + "-" + artifact.getVersion() + "-sources.jar";
            if (!downloadSources && filename.equals(sourceFilename)) {
                continue;
            }

            String httpUrl = NetUtils.joinUri(parentUrl, filename);
            File downfile = new File(parent, filename);

            if (log.isDebugEnabled()) {
                log.debug("download {} to {} ..", httpUrl, downfile.getAbsolutePath());
            }

            // 创建目录，创建下载文件
            if (FileUtils.createFile(downfile, true)) {
                try {
                    IO.write(new URL(httpUrl).openStream(), new FileOutputStream(downfile), this);
                } catch (FileNotFoundException e) {
                    log.error(e.getLocalizedMessage());
                    continue;
                }
            }

            // 如果终止了下载文件，则需要将被终止的文件删除
            if (this.isTerminate()) {
                FileUtils.delete(downfile);
            }
        }

        if (!this.isTerminate()) {
            File remote = new File(parent, "_remote.repositories");
            String id = ArrayUtils.lastElement(StringUtils.split(this.getClass().getAnnotation(EasyBean.class).value(), '.'));

            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00")); // CST 是中国标准时间 (GMT+08:00)
            String formattedDate = sdf.format(new Date());

            ArtifactSearchSettings settings = this.ioc.getBean(ArtifactSearchSettings.class);
            StringBuilder buf = new StringBuilder("#NOTE: ").append(settings.getName()).append(" Plugin for intellij Idea").append(FileUtils.lineSeparator);
            buf.append("#").append(formattedDate).append(FileUtils.lineSeparator);
            for (String filename : files) {
                buf.append(filename).append(">").append(id).append("=").append(FileUtils.lineSeparator);
            }
            FileUtils.write(remote, CharsetName.UTF_8, true, buf.toString());
        }

        if (this.search != null) {
            this.search.asyncDisplay();
        }
        return null;
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
