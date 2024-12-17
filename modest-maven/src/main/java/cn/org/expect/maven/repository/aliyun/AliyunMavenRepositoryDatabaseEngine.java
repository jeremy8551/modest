package cn.org.expect.maven.repository.aliyun;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.maven.repository.impl.SimpleArtifactRepositoryDatabaseEngine;
import cn.org.expect.maven.search.ArtifactSearchSettings;

@EasyBean(singleton = true)
public class AliyunMavenRepositoryDatabaseEngine extends SimpleArtifactRepositoryDatabaseEngine {

    public AliyunMavenRepositoryDatabaseEngine(ArtifactSearchSettings settings) {
        super(settings, "ALIYUN_PATTERN_TABLE.json", "ALIYUN_ARTIFACT_TABLE.json");
    }
}
