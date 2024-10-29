package cn.org.expect.intellijidea.plugin.maven.search;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import org.json.JSONObject;

/**
 * MavenArtifact 工厂
 */
public interface MavenArtifactFactory {

    /**
     * 将 Json 字符串解析为 Maven 工件
     *
     * @param json 字符串
     * @return Maven 工件
     */
    MavenArtifact build(JSONObject json);
}
