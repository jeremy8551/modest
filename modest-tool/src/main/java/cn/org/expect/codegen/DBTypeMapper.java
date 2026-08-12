package cn.org.expect.codegen;

import java.util.HashMap;
import java.util.Map;

public class DBTypeMapper {
    private static final Map<String, JavaType> TYPE_MAP = new HashMap<String, JavaType>();

    static {
        // 字符
        TYPE_MAP.put("CHAR", JavaType.STRING);
        TYPE_MAP.put("VARCHAR", JavaType.STRING);
        TYPE_MAP.put("VARCHAR2", JavaType.STRING);
        TYPE_MAP.put("TEXT", JavaType.STRING);
        TYPE_MAP.put("CLOB", JavaType.STRING);

        // 整数
        TYPE_MAP.put("INTEGER", JavaType.INTEGER);
        TYPE_MAP.put("INT", JavaType.INTEGER);
        TYPE_MAP.put("SMALLINT", JavaType.INTEGER);

        // 大整数
        TYPE_MAP.put("BIGINT", JavaType.LONG);
        TYPE_MAP.put("LONG", JavaType.LONG);

        // 小数
        TYPE_MAP.put("NUMBER", JavaType.BIG_DECIMAL);
        TYPE_MAP.put("DECIMAL", JavaType.BIG_DECIMAL);
        TYPE_MAP.put("NUMERIC", JavaType.BIG_DECIMAL);

        // 时间
        TYPE_MAP.put("DATE", JavaType.LOCAL_DATE);
        TYPE_MAP.put("TIME", JavaType.LOCAL_TIME);
        TYPE_MAP.put("TIMESTAMP", JavaType.LOCAL_DATE_TIME);

        // 二进制
        TYPE_MAP.put("BLOB", JavaType.BYTE_ARRAY);
        TYPE_MAP.put("BINARY", JavaType.BYTE_ARRAY);
    }

    /**
     * 根据数据库类型映射Java类型
     *
     * @param dbType 数据库类型, 例如: char(10)
     * @return Java类型
     */
    public static String mapping(String dbType) {
        String baseType = DBTypeParser.getBaseType(dbType);
        JavaType javaType = TYPE_MAP.get(baseType);
        return javaType == null ? JavaType.OBJECT.getName() : javaType.getName();
    }
}
