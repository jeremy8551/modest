package cn.org.expect.maven.pom;

import java.util.List;

public interface Pom {

    String getUrl();

    String getPackaging();

    String getDescription();

    Parent getParent();

    Scm getScm();

    Issue getIssue();

    List<Developer> getDevelopers();

    List<License> getLicenses();

    String getProjectUrl();
}
