package cn.org.expect.maven.pom;

public interface Scm {
    
    String getConnection();

    String getDeveloperConnection();

    String getUrl();

    String getTag();
}
