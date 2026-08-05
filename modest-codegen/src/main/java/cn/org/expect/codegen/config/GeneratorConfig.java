package cn.org.expect.codegen.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 代码生成配置
 */
public final class GeneratorConfig {

    private final Properties properties;

    private GeneratorConfig(Properties properties) {
        this.properties = properties;
    }

    /**
     * 创建默认配置
     *
     * @return 默认配置
     */
    public static GeneratorConfig defaults() {
        return new GeneratorConfig(new Properties());
    }

    /**
     * 从属性文件加载配置
     *
     * @param file 属性文件
     * @return 生成配置
     * @throws IOException 读取配置失败时抛出
     */
    public static GeneratorConfig load(File file) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(file);
        try {
            properties.load(inputStream);
        } finally {
            inputStream.close();
        }
        return new GeneratorConfig(properties);
    }

    public String basePackage() {
        return this.value("basePackage", "cn.org.expect.nas");
    }

    public String tablePrefix() {
        return this.value("tablePrefix", "nas_");
    }

    public String daoSuperInterface() {
        return this.value("daoSuperInterface", "cn.org.expect.nas.dao.BaseDao<${entity}>");
    }

    public String mapperSuperInterface() {
        return this.value("mapperSuperInterface", "com.baomidou.mybatisplus.core.mapper.BaseMapper<${entity}>");
    }

    public String entitySuperClass() {
        return this.value("entitySuperClass", "cn.org.expect.nas.entity.AbstractTemplateEntity");
    }

    public List<String> annotationImports() {
        return this.list("entityAnnotationImports", ",");
    }

    public List<String> fieldAnnotations(String columnName) {
        List<String> annotations = new ArrayList<String>(this.list("entityFieldAnnotations", "\\|"));
        annotations.addAll(this.list("entityFieldAnnotations." + columnName, "\\|"));
        return annotations;
    }

    public Map<String, String> sqlTypeMappings() {
        Map<String, String> mappings = new LinkedHashMap<String, String>();
        mappings.put("LONG", "BIGINT");
        mappings.put("DATETIME", "TIMESTAMP");
        mappings.put("BLOB", "BYTEA");
        mappings.put("CLOB", "TEXT");
        for (String name : this.properties.stringPropertyNames()) {
            if (name.startsWith("sqlTypeMappings.")) {
                mappings.put(name.substring("sqlTypeMappings.".length()).toUpperCase(),
                        this.properties.getProperty(name).trim());
            }
        }
        return mappings;
    }

    private String value(String name, String defaultValue) {
        return this.properties.getProperty(name, defaultValue).trim();
    }

    private List<String> list(String name, String separator) {
        String value = this.value(name, "");
        if (value.length() == 0) {
            return new ArrayList<String>();
        }
        List<String> values = new ArrayList<String>();
        for (String item : Arrays.asList(value.split(separator))) {
            String trimmed = item.trim();
            if (trimmed.length() > 0) {
                values.add(trimmed);
            }
        }
        return values;
    }
}
