package cn.org.expect.maven.pom.impl;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.maven.pom.Developer;
import cn.org.expect.maven.pom.License;
import cn.org.expect.maven.pom.Pom;
import cn.org.expect.util.StringUtils;

public class SimplePom implements Pom {

    /** 父POM的坐标 */
    private final SimpleParent parent;

    /** 打包类型 */
    private String packaging;

    /** 说明 */
    private String description;

    /** 项目地址 */
    private String url;

    /** 源代码管理工具的地址 */
    private final SimpleScm scm;

    /** 开源许可证 */
    private final List<License> licenses;

    /** 问题管理系统 */
    private final SimpleIssue issue;

    /** 开发人员 */
    private final List<Developer> developers;

    public SimplePom() {
        this.parent = new SimpleParent();
        this.scm = new SimpleScm();
        this.licenses = new ArrayList<>();
        this.issue = new SimpleIssue();
        this.developers = new ArrayList<>();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = StringUtils.trimBlank(url);
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = StringUtils.trimBlank(packaging);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trimBlank(description);
    }

    public SimpleParent getParent() {
        return parent;
    }

    public SimpleScm getScm() {
        return scm;
    }

    public SimpleIssue getIssue() {
        return issue;
    }

    public List<Developer> getDevelopers() {
        return developers;
    }

    public List<License> getLicenses() {
        return licenses;
    }

    public String getProjectUrl() {
        if (StringUtils.isNotBlank(this.getUrl())) {
            return this.getUrl();
        }

        if (StringUtils.isNotBlank(this.getScm().getUrl())) {
            return StringUtils.replaceVariable(this.getScm().getUrl(), "project.scm.tag", this.getScm().getTag());
        }

        int begin;
        String connection = this.getScm().getConnection();
        if (connection != null && (begin = connection.indexOf("http://")) != -1) {
            return connection.substring(begin);
        }

        String developerConnection = this.getScm().getDeveloperConnection();
        if (developerConnection != null && (begin = developerConnection.indexOf("https://")) != -1) {
            return developerConnection.substring(begin);
        }
        return null;
    }
}
