package cn.org.expect.maven.pom;

import java.util.List;

public interface Developer {
    
    String getId();

    String getName();

    String getEmail();

    String getTimezone();

    String getOrganization();

    String getOrganizationUrl();

    List<String> getRoles();
}
