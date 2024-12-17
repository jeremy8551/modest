package cn.org.expect.maven.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.maven.Artifact;
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

    /** 搜索接口 */
    private ArtifactSearch search;

    /** 容器上下文信息 */
    private final EasyContext ioc;

    public AbstractArtifactDownloader(EasyContext ioc) {
        this.ioc = Ensure.notNull(ioc);
    }

    public void setSearch(ArtifactSearch search) {
        this.search = search;
    }

    /**
     * 返回搜索接口
     *
     * @return 搜索接口
     */
    public ArtifactSearch getSearch() {
        return search;
    }

    /**
     * 查询工件目录的仓库地址
     *
     * @return 仓库地址
     */
    public String getListAddress() {
        return "https://repo1.maven.org/maven2/";
    }

    /**
     * 返回下载工件的仓库地址
     *
     * @return 仓库地址
     */
    public abstract String getAddress();

    public void execute(Artifact artifact, File parent, boolean downloadSources, boolean downloadDocs, boolean downloadAnnotation) throws Exception {
        FileUtils.createDirectory(parent, true);
        String parentUrl = artifact.toURI(this.getAddress());

        // 获取下载文件名的集合
        List<String> downloads = new ArrayList<>();
        List<String> files = this.listFilename(this.getListAddress(), artifact);
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

            String url = NetUtils.joinUri(parentUrl, filename);
            File downfile = new File(parent, filename);

            if (log.isDebugEnabled()) {
                log.debug("download {} to {} ..", url, downfile.getAbsolutePath());
            }

            // 创建目录，创建下载文件
            if (FileUtils.createFile(downfile, true)) {
                try {
                    IO.write(new URL(url).openStream(), new FileOutputStream(downfile), this);
                } catch (Throwable e) {
                    FileUtils.delete(downfile);
                    log.error("download {} fail!", e.getLocalizedMessage());
                    continue;
                }
            }

            // 如果终止了下载文件，则需要将被终止的文件删除
            if (this.isTerminate()) {
                FileUtils.delete(downfile);
            } else {
                downloads.add(filename);
            }
        }

        if (!this.isTerminate()) {
            File remote = new File(parent, "_remote.repositories");
            String id = ArrayUtils.lastElement(StringUtils.split(this.getClass().getAnnotation(EasyBean.class).value(), '.'));

            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00")); // CST 是中国标准时间 (GMT+08:00)
            String dateStr = sdf.format(new Date());

            ArtifactSearchSettings settings = this.ioc.getBean(ArtifactSearchSettings.class);
            StringBuilder buf = new StringBuilder("# NOTE: ").append(settings.getName()).append(" Plugin for intellij Idea").append(FileUtils.lineSeparator);
            buf.append("# ").append(dateStr).append(FileUtils.lineSeparator);
            for (String filename : downloads) {
                buf.append(filename).append(">").append(id).append("=").append(FileUtils.lineSeparator);
            }
            FileUtils.write(remote, CharsetName.UTF_8, true, buf.toString());
        }

        if (this.search != null) {
            this.search.asyncDisplay();
        }
    }

    /**
     * 获取指定 URL 目录下的文件列表
     */
    public List<String> listFilename(String httpUrl, Artifact artifact) {
        try {
            List<String> result = new ArrayList<>();
            String html = this.sendURL(artifact.toURI(httpUrl));
            Pattern pattern = Pattern.compile("href=\"([^\"]+)\""); // 使用正则表达式查找 HTML 中的文件链接
            Matcher matcher = pattern.matcher(html);
            while (matcher.find()) {
                String filename = matcher.group(1);
                String ext = FileUtils.getFilenameExt(filename);
                if (ext.length() > 1) { // 过滤需要的文件类型
                    result.add(filename);
                }
            }
            return result.isEmpty() ? artifact.getFilenames() : result;
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage());
            return artifact.getFilenames();
        }
    }
}
