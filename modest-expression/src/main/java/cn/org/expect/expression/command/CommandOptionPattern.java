package cn.org.expect.expression.command;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.expression.CommandExpression;
import cn.org.expect.expression.ExpressionException;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

public class CommandOptionPattern {

    /** 选项名 */
    private String name;

    /** true表示必须有值 */
    private boolean containsValue;

    /** true 表示长选项 */
    private boolean islong;

    /** 选项值的格式 */
    private String format;

    /**
     * 初始化
     *
     * @param str -t -t: -tvf: -tvf:date --filename
     */
    public CommandOptionPattern(CommandExpression expr, String str) {
        if (str == null || str.length() == 0 || str.charAt(0) != '-') {
            throw new IllegalArgumentException(str);
        }

        this.islong = str.charAt(1) == '-';
        List<String> list = new ArrayList<String>();
        StringUtils.split(str.substring(this.islong ? 2 : 1), ':', list);
        switch (list.size()) {
            case 1:
                this.name = list.get(0);
                this.containsValue = false;
                this.format = "";
                break;

            case 2:
                this.name = list.get(0);
                this.containsValue = true;
                this.format = expr.getAnalysis().unQuotation(list.get(1));
                break;

            default:
                throw new ExpressionException(ResourcesUtils.getMessage("expression.standard.output.msg070", expr.getPattern(), str));
        }
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
     * 判断选项是否有选项值
     *
     * @return 返回 true 表示选项有选项值
     */
    public boolean containsValue() {
        return containsValue;
    }

    /**
     * 判断是否是长选项
     *
     * @return 返回 true 表示是长选项
     */
    public boolean islong() {
        return islong;
    }

    /**
     * 返回选项值的格式
     *
     * @return 选项值格式
     */
    public String getFormat() {
        return format;
    }

    public String toString() {
        return "CommandOptionPattern [name=" + name + ", containsValue=" + containsValue + ", islong=" + islong + ", format=" + format + "]";
    }

}
