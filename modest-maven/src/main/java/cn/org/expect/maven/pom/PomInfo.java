package cn.org.expect.maven.pom;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.util.StringUtils;

public class PomInfo {

    /** 父POM的坐标 */
    private final Parent parent;

    /** 打包类型 */
    private String packaging;

    /** 说明 */
    private String description;

    /** 项目地址 */
    private String url;

    /** 源代码管理工具的地址 */
    private final Scm scm;

    /** 开源许可证 */
    private final List<License> licenses;

    /** 问题管理系统 */
    private final Issue issue;

    /** 开发人员 */
    private final List<Developer> developers;

    public PomInfo() {
        this.parent = new Parent();
        this.scm = new Scm();
        this.licenses = new ArrayList<>();
        this.issue = new Issue();
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

    public Parent getParent() {
        return parent;
    }

    public Scm getScm() {
        return scm;
    }

    public Issue getIssue() {
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

    public static class License {
        private String name;
        private String url;
        private String distribution;
        private String comments;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = StringUtils.trimBlank(name);
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = StringUtils.trimBlank(url);
        }

        public String getDistribution() {
            return distribution;
        }

        public void setDistribution(String distribution) {
            this.distribution = StringUtils.trimBlank(distribution);
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = StringUtils.trimBlank(comments);
        }
    }

    public static class Parent {
        private String artifactId;
        private String groupId;
        private String version;

        public String getArtifactId() {
            return artifactId;
        }

        public void setArtifactId(String artifactId) {
            this.artifactId = StringUtils.trimBlank(artifactId);
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = StringUtils.trimBlank(groupId);
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = StringUtils.trimBlank(version);
        }

        public boolean isInherit(String groupId) {
            if (StringUtils.isBlank(this.groupId) || StringUtils.isBlank(this.artifactId) || StringUtils.isBlank(this.version)) {
                return false;
            }

            if (this.groupId.startsWith(groupId) || groupId.startsWith(this.groupId)) {
                return true;
            }

            // 域名的前两位相等
            String[] a1 = StringUtils.split(this.groupId, '.');
            String[] a2 = StringUtils.split(groupId, '.');
            return a1.length >= 2 && a2.length >= 2 && a1[0].equals(a2[0]) && a1[1].equals(a2[1]);
        }
    }

    public static class Issue {
        private String system;
        private String url;

        public String getSystem() {
            return system;
        }

        public void setSystem(String system) {
            this.system = StringUtils.trimBlank(system);
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = StringUtils.trimBlank(url);
        }
    }

    public static class Scm {
        private String connection;
        private String developerConnection;
        private String url;
        private String tag;

        public String getConnection() {
            return connection;
        }

        public void setConnection(String connection) {
            this.connection = StringUtils.trimBlank(connection);
        }

        public String getDeveloperConnection() {
            return developerConnection;
        }

        public void setDeveloperConnection(String developerConnection) {
            this.developerConnection = StringUtils.trimBlank(developerConnection);
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = StringUtils.trimBlank(url);
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = StringUtils.trimBlank(tag);
        }
    }

    public static class Developer {
        private String id;
        private String name;
        private String email;
        private String timezone;
        private String organization;
        private String organizationUrl;
        private List<String> roles;

        public Developer() {
            this.roles = new ArrayList<>();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = StringUtils.trimBlank(id);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = StringUtils.trimBlank(name);
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = StringUtils.trimBlank(email);
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = StringUtils.trimBlank(timezone);
        }

        public String getOrganization() {
            return organization;
        }

        public void setOrganization(String organization) {
            this.organization = StringUtils.trimBlank(organization);
        }

        public String getOrganizationUrl() {
            return organizationUrl;
        }

        public void setOrganizationUrl(String organizationUrl) {
            this.organizationUrl = StringUtils.trimBlank(organizationUrl);
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}
