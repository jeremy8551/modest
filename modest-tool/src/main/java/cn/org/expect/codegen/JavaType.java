package cn.org.expect.codegen;

public enum JavaType {

    STRING("String"),

    INTEGER("Integer"),

    LONG("Long"),

    BIG_DECIMAL("BigDecimal"),

    BOOLEAN("Boolean"),

    LOCAL_DATE("LocalDate"),

    LOCAL_TIME("LocalTime"),

    LOCAL_DATE_TIME("LocalDateTime"),

    BYTE_ARRAY("byte[]"),

    OBJECT("Object");

    private final String name;

    JavaType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
