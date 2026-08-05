package cn.org.expect.codegen.render;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.org.expect.codegen.config.GeneratorConfig;
import cn.org.expect.codegen.model.ColumnDesign;
import cn.org.expect.codegen.model.TableDesign;
import cn.org.expect.codegen.util.NamingUtils;

/**
 * 持久化层代码渲染器
 */
public class CodeRenderer {

    private static final Pattern TYPE_PATTERN = Pattern.compile("^([A-Z]+)");
    private static final Pattern VIRTUAL_FIELD_PATTERN = Pattern.compile("(?i)^\\s*([A-Za-z0-9_]+)\\.([A-Za-z0-9_]+)\\s+as\\s+([A-Za-z0-9_]+)\\s*$");
    private final GeneratorConfig config;

    public CodeRenderer(GeneratorConfig config) {
        this.config = config;
    }

    public String renderDdl(List<TableDesign> tables) {
        StringBuilder sql = new StringBuilder();
        for (TableDesign table : tables) {
            sql.append("-- 创建").append(table.description()).append("\n").append("-- DROP TABLE ").append(table.name()).append(";\n").append("CREATE TABLE ").append(table.name()).append(" (\n");
            List<String> definitions = new ArrayList<String>();
            for (ColumnDesign column : table.columns()) {
                StringBuilder definition = new StringBuilder("    ").append(column.name()).append(" ").append(this.sqlType(column.sqlType()));
                if (column.defaultValue().length() > 0) {
                    definition.append(" DEFAULT ").append(column.defaultValue());
                }
                if (column.notNull()) {
                    definition.append(" NOT NULL");
                }
                definitions.add(definition.toString());
            }
            List<String> primaryKeys = new ArrayList<String>();
            for (ColumnDesign column : table.columns()) {
                if (column.primaryKey()) {
                    primaryKeys.add(column.name());
                }
            }
            if (!primaryKeys.isEmpty()) {
                definitions.add("    PRIMARY KEY (" + this.join(", ", primaryKeys) + ")");
            }
            sql.append(this.join(",\n", definitions)).append("\n);\n\n").append("COMMENT ON TABLE ").append(table.name()).append(" IS '").append(this.sqlLiteral(table.description())).append("';\n");
            for (ColumnDesign column : table.columns()) {
                sql.append("COMMENT ON COLUMN ").append(table.name()).append(".").append(column.name()).append(" IS '").append(this.sqlLiteral(column.description())).append("';\n");
            }
            List<String> uniqueColumns = new ArrayList<String>();
            for (ColumnDesign column : table.columns()) {
                if (column.uniqueIndex()) {
                    uniqueColumns.add(column.name());
                }
            }
            if (!uniqueColumns.isEmpty()) {
                sql.append("CREATE UNIQUE INDEX ").append(table.name()).append("_uk_01 ON ").append(table.name()).append(" (").append(this.join(", ", uniqueColumns)).append(");\n");
            }
            sql.append("\n");
        }
        return sql.toString();
    }

    public String renderEntity(TableDesign table) {
        String entityName = this.entityName(table);
        Set<String> imports = new LinkedHashSet<String>();
        imports.add("com.baomidou.mybatisplus.annotation.FieldFill");
        imports.add("com.baomidou.mybatisplus.annotation.IdType");
        imports.add("com.baomidou.mybatisplus.annotation.TableField");
        imports.add("com.baomidou.mybatisplus.annotation.TableId");
        imports.add("com.baomidou.mybatisplus.annotation.TableName");
        imports.add("com.baomidou.mybatisplus.annotation.Version");
        imports.add("io.swagger.v3.oas.annotations.media.Schema");
        imports.add("lombok.AllArgsConstructor");
        imports.add("lombok.Builder");
        imports.add("lombok.Data");
        imports.add("lombok.EqualsAndHashCode");
        imports.add("lombok.NoArgsConstructor");
        imports.add("lombok.ToString");
        imports.addAll(this.config.annotationImports());
        String superClass = this.simpleType(this.config.entitySuperClass(), imports);
        for (ColumnDesign column : table.columns()) {
            this.registerJavaType(this.javaType(table, column), imports);
        }
        StringBuilder source = new StringBuilder("package ").append(this.config.basePackage()).append(".entity;\n\n");
        this.appendImports(source, imports);
        source.append("\n/**\n * ").append(table.description()).append("\n */\n").append("@Builder\n@Data\n@EqualsAndHashCode(callSuper = true)\n@ToString(callSuper = true)\n").append("@NoArgsConstructor\n@AllArgsConstructor\n@Schema(description = \"").append(this.javaLiteral(table.sheetName())).append("\")\n@TableName(\"").append(this.javaLiteral(table.name())).append("\")\npublic class ").append(entityName);
        if (superClass.length() > 0) {
            source.append(" extends ").append(superClass);
        }
        source.append(" {\n\n");
        for (ColumnDesign column : table.columns()) {
            this.appendField(source, table, column);
            this.appendVirtualField(source, column);
        }
        return source.append("}\n").toString();
    }

    public String renderEnum(TableDesign table, ColumnDesign column) {
        String enumName = this.enumName(table, column);
        StringBuilder source = new StringBuilder("package ").append(this.config.basePackage()).append(".enums;\n\n").append("import io.swagger.v3.oas.annotations.media.Schema;\n").append("import lombok.Getter;\n\n/**\n * ").append(column.description()).append("\n */\n").append("@Schema(description = \"").append(this.javaLiteral(column.description())).append("\")\n").append("@Getter\npublic enum ").append(enumName).append(" implements Enum {\n\n");
        for (String item : column.dictionary().split("/")) {
            String[] pair = item.trim().split("-", 2);
            if (pair.length != 2 || pair[0].trim().length() == 0) {
                throw new IllegalArgumentException(table.name() + "." + column.name() + " 数据字典格式错误: " + item);
            }
            source.append("    ").append(NamingUtils.enumConstant(pair[0])).append("(\"").append(this.javaLiteral(pair[1])).append("\"), //\n");
        }
        return source.append("    ;\n\n    /** 枚举说明 */\n    private final String desc;\n\n").append("    ").append(enumName).append("(String desc) {\n        this.desc = desc;\n    }\n\n").append("    @Override\n    public String toString() {\n").append("        return this.name() + \"-\" + this.desc + \"-\" + this.ordinal();\n    }\n}\n").toString();
    }

    public String renderMapper(TableDesign table) {
        return this.renderAccessInterface(table, "mapper", "Mapper", this.config.mapperSuperInterface());
    }

    public String renderDao(TableDesign table) {
        return this.renderAccessInterface(table, "dao", "Dao", this.config.daoSuperInterface());
    }

    public String renderMapperXml(TableDesign table) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n" + "        \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n\n" + "<mapper namespace=\"" + this.config.basePackage() + ".dao." + this.entityName(table) + "Dao\">\n</mapper>\n";
    }

    public String entityName(TableDesign table) {
        String name = table.name().startsWith(this.config.tablePrefix()) ? table.name().substring(this.config.tablePrefix().length()) : table.name();
        return NamingUtils.upperCamel(name) + "Entity";
    }

    public String enumName(TableDesign table, ColumnDesign column) {
        String name = table.name().startsWith(this.config.tablePrefix()) ? table.name().substring(this.config.tablePrefix().length()) : table.name();
        return NamingUtils.upperCamel(name) + NamingUtils.upperCamel(column.name()) + "Enum";
    }

    private String renderAccessInterface(TableDesign table, String packageName, String suffix, String configuredSuperType) {
        String entityName = this.entityName(table);
        Set<String> imports = new LinkedHashSet<String>();
        imports.add(this.config.basePackage() + ".entity." + entityName);
        imports.add("org.apache.ibatis.annotations.Mapper");
        String superType = this.resolveSuperType(configuredSuperType, entityName, imports);
        StringBuilder source = new StringBuilder("package ").append(this.config.basePackage()).append(".").append(packageName).append(";\n\n");
        this.appendImports(source, imports);
        source.append("\n/**\n * ").append(table.description()).append("数据访问接口\n */\n@Mapper\npublic interface ").append(entityName).append(suffix);
        if (superType.length() > 0) {
            source.append(" extends ").append(superType);
        }
        return source.append(" {\n}\n").toString();
    }

    private String resolveSuperType(String configuredType, String entityName, Set<String> imports) {
        if (configuredType.trim().length() == 0) {
            return "";
        }
        String resolved = configuredType.replace("${entity}", entityName);
        Matcher matcher = Pattern.compile("([a-z][A-Za-z0-9_.]+\\.)?([A-Z][A-Za-z0-9_]*)(<.*>)?").matcher(resolved);
        if (!matcher.matches()) {
            return resolved;
        }
        String packagePrefix = matcher.group(1);
        if (packagePrefix != null) {
            imports.add(packagePrefix.substring(0, packagePrefix.length() - 1) + "." + matcher.group(2));
        }
        return matcher.group(2) + (matcher.group(3) == null ? "" : matcher.group(3));
    }

    private void appendField(StringBuilder source, TableDesign table, ColumnDesign column) {
        source.append("    /** ").append(column.description()).append(" */\n");
        if (column.primaryKey()) {
            String idType = column.remark().contains("自增") ? ", type = IdType.AUTO" : column.remark().contains("雪花算法") ? ", type = IdType.ASSIGN_ID" : "";
            source.append("    @TableId(value = \"").append(column.name()).append("\"").append(idType).append(")\n");
        } else {
            String fill = column.remark().contains("自动填充") ? ", fill = FieldFill." + (column.description().contains("修改") ? "UPDATE" : "INSERT") : "";
            source.append("    @TableField(value = \"").append(column.name()).append("\"").append(fill).append(")\n");
        }
        if (column.remark().contains("乐观锁")) {
            source.append("    @Version\n");
        }
        source.append("    @Schema(description = \"").append(this.javaLiteral(column.description())).append("\")\n");
        String fillEnum = this.fillEnum(column);
        if (fillEnum.length() > 0) {
            source.append("    @").append(this.config.basePackage()).append(".annotation.AutoFill(").append(this.config.basePackage()).append(".enums.FillEnum.").append(fillEnum).append(")\n");
        }
        for (String annotation : this.config.fieldAnnotations(column.name())) {
            source.append("    @").append(this.annotationText(annotation, table, column)).append("\n");
        }
        source.append("    private ").append(this.simpleJavaType(this.javaType(table, column))).append(" ").append(NamingUtils.lowerCamel(column.name())).append(";\n\n");
    }

    private void appendVirtualField(StringBuilder source, ColumnDesign column) {
        if (column.virtualField().trim().length() == 0) {
            return;
        }
        Matcher matcher = VIRTUAL_FIELD_PATTERN.matcher(column.virtualField());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("虚拟字段格式错误: " + column.virtualField());
        }
        String virtualName = matcher.group(3);
        source.append("    /** ").append(column.description()).append("虚拟值 */\n").append("    @TableField(value = \"").append(virtualName).append("\", exist = false)\n").append("    @").append(this.config.basePackage()).append(".annotation.RelateField(table = \"").append(matcher.group(1)).append("\", on = \"").append(column.name()).append("\", field = \"").append(matcher.group(2)).append("\")\n").append("    @Schema(description = \"").append(this.javaLiteral(column.description())).append("值\")\n").append("    private String ").append(NamingUtils.lowerCamel(virtualName)).append(";\n\n");
    }

    private String javaType(TableDesign table, ColumnDesign column) {
        if (column.dictionary().trim().length() > 0) {
            return this.config.basePackage() + ".enums." + this.enumName(table, column);
        }
        String baseType = this.baseSqlType(column.sqlType());
        if ("BIGINT".equals(baseType) || "LONG".equals(baseType)) {
            return "Long";
        }
        if ("INT".equals(baseType) || "INTEGER".equals(baseType) || "SMALLINT".equals(baseType)) {
            return "Integer";
        }
        if ("DECIMAL".equals(baseType) || "NUMERIC".equals(baseType) || "NUMBER".equals(baseType)) {
            return "java.math.BigDecimal";
        }
        if ("DATE".equals(baseType)) {
            return "java.util.Date";
        }
        if ("TIME".equals(baseType)) {
            return "java.sql.Time";
        }
        if ("DATETIME".equals(baseType) || "TIMESTAMP".equals(baseType)) {
            return "java.sql.Timestamp";
        }
        if ("BOOLEAN".equals(baseType) || "BOOL".equals(baseType)) {
            return "Boolean";
        }
        if ("BLOB".equals(baseType) || "BYTEA".equals(baseType) || "BINARY".equals(baseType) || "VARBINARY".equals(baseType)) {
            return "byte[]";
        }
        return "String";
    }

    private String fillEnum(ColumnDesign column) {
        if (!column.remark().contains("自动填充")) {
            return "";
        }
        String description = column.description();
        if (description.contains("用户编号") || description.contains("用户ID")) {
            return "USER_ID";
        }
        if (description.contains("用户名称") || description.contains("用户名")) {
            return "USER_NAME";
        }
        if (description.contains("机构编号") || description.contains("机构号")) {
            return "ORGAN_NO";
        }
        if (description.contains("机构名称")) {
            return "ORGAN_NAME";
        }
        if (description.contains("角色编号")) {
            return "ROLE_LIST";
        }
        return "";
    }

    private String sqlType(String excelType) {
        String baseType = this.baseSqlType(excelType);
        String mappedType = this.config.sqlTypeMappings().get(baseType);
        return mappedType == null ? excelType : mappedType + excelType.substring(baseType.length());
    }

    private String baseSqlType(String type) {
        Matcher matcher = TYPE_PATTERN.matcher(type.trim().toUpperCase(Locale.ENGLISH));
        return matcher.find() ? matcher.group(1) : type.trim().toUpperCase(Locale.ENGLISH);
    }

    private void registerJavaType(String type, Set<String> imports) {
        if (type.contains(".")) {
            imports.add(type);
        }
    }

    private String simpleType(String type, Set<String> imports) {
        if (type.trim().length() == 0) {
            return "";
        }
        if (type.contains(".")) {
            imports.add(type);
            return type.substring(type.lastIndexOf('.') + 1);
        }
        return type;
    }

    private String simpleJavaType(String type) {
        return type.contains(".") ? type.substring(type.lastIndexOf('.') + 1) : type;
    }

    private String annotationText(String annotation, TableDesign table, ColumnDesign column) {
        String resolved = annotation.replace("${tableName}", table.name()).replace("${columnName}", column.name()).replace("${fieldName}", NamingUtils.lowerCamel(column.name())).replace("${description}", this.javaLiteral(column.description()));
        int argumentsIndex = resolved.indexOf('(');
        String type = argumentsIndex < 0 ? resolved : resolved.substring(0, argumentsIndex);
        return type.substring(type.lastIndexOf('.') + 1) + (argumentsIndex < 0 ? "" : resolved.substring(argumentsIndex));
    }

    private String javaLiteral(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String sqlLiteral(String value) {
        return value.replace("'", "''");
    }

    /**
     * 按字典顺序追加导入声明
     *
     * @param source  Java 源码
     * @param imports 导入类名
     */
    private void appendImports(StringBuilder source, Set<String> imports) {
        List<String> sortedImports = new ArrayList<String>(imports);
        java.util.Collections.sort(sortedImports);
        for (String item : sortedImports) {
            source.append("import ").append(item).append(";\n");
        }
    }

    /**
     * 拼接字符串列表
     *
     * @param separator 分隔符
     * @param values    字符串列表
     * @return 拼接结果
     */
    private String join(String separator, List<String> values) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                result.append(separator);
            }
            result.append(values.get(i));
        }
        return result.toString();
    }
}
