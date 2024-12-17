package cn.org.expect.maven.pom.impl;

import cn.org.expect.maven.pom.License;
import cn.org.expect.util.StringUtils;

public class SimpleLicense implements License {
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
