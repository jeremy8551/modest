package cn.org.expect.maven.search;

import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.StringUtils;

public interface ArtifactOption {

    String getKey();

    String getName();

    default String getId() {
        return ArrayUtils.lastElement(StringUtils.split(this.getKey(), '.'));
    }

    static ArtifactOption getRepository(String id) {
        return new ArtifactOptionImpl("query.use." + id);
    }

    static ArtifactOption getDownloader(String id) {
        return new ArtifactOptionImpl("download.use." + id);
    }
}
