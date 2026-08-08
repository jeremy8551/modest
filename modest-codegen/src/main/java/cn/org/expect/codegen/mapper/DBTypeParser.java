package cn.org.expect.codegen.mapper;

public class DBTypeParser {

    public static String getBaseType(String type) {
        if (type == null) {
            return null;
        }

        int index = type.indexOf('(');
        if (index > 0) {
            return type.substring(0, index).trim().toUpperCase();
        }

        return type.trim().toUpperCase();
    }
}
