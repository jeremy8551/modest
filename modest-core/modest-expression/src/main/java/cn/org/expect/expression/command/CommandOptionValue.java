package cn.org.expect.expression.command;

public class CommandOptionValue {

    /** 选项名（无-） */
    private String name;

    /** 选项值 */
    private String value;

    /** true 表示长选项 */
    private boolean islong;

    /**
     * 初始化
     *
     * @param name   选项名（无-）
     * @param value  选项值
     * @param islong 是否是长选项
     */
    public CommandOptionValue(String name, String value, boolean islong) {
        this.name = name;
        this.value = value;
        this.islong = islong;
    }

    /**
     * 返回选项名
     *
     * @return 选项名
     */
    public String getName() {
        return name;
    }

    /**
     * 返回选项值
     *
     * @return 选项值
     */
    public String getValue() {
        return value;
    }

    /**
     * 判断命令是否支持长选项
     *
     * @return 返回 true 表示长选项
     */
    public boolean islong() {
        return islong;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(this.name.length() + 3 + this.value.length());
        buf.append('-');
        if (this.islong) {
            buf.append('-');
        }
        buf.append(this.name);
        buf.append(' ');
        if (this.value.indexOf(' ') != -1) {
            buf.append('\"');
            buf.append(this.value);
            buf.append('\"');
        } else {
            buf.append(this.value);
        }
        return buf.toString();
    }

}
