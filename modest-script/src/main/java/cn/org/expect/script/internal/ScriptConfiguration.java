package cn.org.expect.script.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import cn.org.expect.collection.CaseSensitivSet;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.script.UniversalScriptConfiguration;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

@EasyBean
public class ScriptConfiguration implements UniversalScriptConfiguration {

    /**
     * 配置信息
     */
    private final Properties config;

    /**
     * 初始化
     */
    public ScriptConfiguration() throws IOException {
        InputStream in = UniversalScriptEngine.class.getResourceAsStream("ScriptEngine.properties");
        this.config = new Properties();
        this.config.load(in);
    }

    /**
     * 返回脚本引擎默认命令的语句
     *
     * @return 默认命令
     */
    public String getDefaultCommand() {
        return Ensure.notBlank(this.getProperty("universal.script.command.default"));
    }

    public String getMimeTypes() {
        return this.getProperty("universal.script.mimetypes");
    }

    public String getExtensions() {
        return this.getProperty("universal.script.extensions");
    }

    public String getNames() {
        return this.getProperty("universal.script.names");
    }

    public String getCompiler() {
        return this.getProperty("universal.script.compiler");
    }

    public String getSessionFactory() {
        return this.getProperty("universal.script.session");
    }

    public String getConverter() {
        return this.getProperty("universal.script.converter");
    }

    public String getChecker() {
        return this.getProperty("universal.script.checker");
    }

    public String getEngineName() {
        return this.getProperty("universal.script.engine");
    }

    public String getEngineVersion() {
        return this.getProperty("universal.script.engine.version");
    }

    public String getLanguageName() {
        return this.getProperty("universal.script.language");
    }

    public String getLanguageVersion() {
        return this.getProperty("universal.script.language.version");
    }

    public Set<String> getKeywords() {
        String keywords = this.getProperty("universal.script.keywords");
        String[] array = StringUtils.removeBlank(StringUtils.split(keywords, ','));
        Set<String> set = new CaseSensitivSet();
        Collections.addAll(set, array);
        return set;
    }

    public String getProperty(String name) {
        // 优先从系统属性中读取参数
        String value = System.getProperty(name);
        if (StringUtils.isNotBlank(value)) { // 属性不能是空
            return value;
        }

        // 加载配置文件中的属性值
        return this.config.getProperty(name);
    }
}
