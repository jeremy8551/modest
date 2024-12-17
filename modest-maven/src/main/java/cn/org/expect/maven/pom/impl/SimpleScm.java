package cn.org.expect.maven.pom.impl;

import cn.org.expect.maven.pom.Scm;
import cn.org.expect.util.StringUtils;

public class SimpleScm implements Scm {
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
