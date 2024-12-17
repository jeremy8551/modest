package cn.org.expect.maven.pom.impl;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.maven.pom.Developer;
import cn.org.expect.util.StringUtils;

public class SimpleDeveloper implements Developer {

    private String id;
    private String name;
    private String email;
    private String timezone;
    private String organization;
    private String organizationUrl;
    private final List<String> roles;

    public SimpleDeveloper() {
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
}
