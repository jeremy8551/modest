package cn.org.expect.maven.pom.impl;

import cn.org.expect.maven.pom.Issue;
import cn.org.expect.util.StringUtils;

public class SimpleIssue implements Issue {

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
