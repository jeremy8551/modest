package cn.org.expect.maven.repository.aliyun;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.maven.repository.impl.SimpleArtifactRepositoryDatabaseEngine;
import cn.org.expect.maven.search.ArtifactSearchSettings;

@EasyBean(value = "aliyun", singleton = true)
public class AliyunMavenRepositoryDatabaseEngine extends SimpleArtifactRepositoryDatabaseEngine {

    public AliyunMavenRepositoryDatabaseEngine(ArtifactSearchSettings settings) {
        super(settings, "ALIYUN_PATTERN_TABLE.json", "ALIYUN_ARTIFACT_TABLE.json");
    }
}