package cn.org.expect.expression;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.org.expect.expression.operation.AndOperator;
import cn.org.expect.expression.operation.DivOperator;
import cn.org.expect.expression.operation.EqualsOperator;
import cn.org.expect.expression.operation.GreaterEqualsOperator;
import cn.org.expect.expression.operation.GreaterOperator;
import cn.org.expect.expression.operation.InOperator;
import cn.org.expect.expression.operation.LessEqualsOperator;
import cn.org.expect.expression.operation.LessOperator;
import cn.org.expect.expression.operation.ModOperator;
import cn.org.expect.expression.operation.MupliOperator;
import cn.org.expect.expression.operation.NotEqualsOperator;
import cn.org.expect.expression.operation.NotInOperator;
import cn.org.expect.expression.operation.Operator;
import cn.org.expect.expression.operation.OrOperator;
import cn.org.expect.expression.operation.PlusOperator;
import cn.org.expect.expression.operation.SubOperator;
import cn.org.expect.expression.operation.TreeOperator;
import cn.org.expect.expression.parameter.ArrayParameter;
import cn.org.expect.expression.parameter.ComplexParameter;
import cn.org.expect.expression.parameter.DateUnitParameter;
import cn.org.expect.expression.parameter.ExpressionParameter;
import cn.org.expect.expression.parameter.Parameter;
import cn.org.expect.expression.parameter.TwoParameter;
import cn.org.expect.util.StringUtils;

/**
 * 表达式解析器
 *
 * @author jeremy8551@gmail.com
 * @createtime 2014-05-21 13:28:52
 */
public abstract class Parser {

    /**
     * 默认解析器
     */
    private static Parser defaultFormat;

    /**
     * 默认解析器
     *
     * @return 解析器
     */
    public static Parser getFormat() {
        if (defaultFormat == null) {
            defaultFormat = new Parser(new BaseAnalysis()) {

                public int parse(String array, int start, boolean isData, List<Parameter> datas, List<Operator> operation) throws ExpressionException {
                    throw new ExpressionException("expression.stdout.message024", String.valueOf(array), start + 1);
                }
            };
        }
        return defaultFormat;
    }

    /**
     * 默认解析器
     *
     * @param format 解析器
     */
    public static void setFormat(Parser format) {
        Parser.defaultFormat = format;
    }

    /** 分析器 */
    protected Analysis analysis;

    /**
     * 初始化
     */
    public Parser(Analysis analysis) {
        this.analysis = analysis;
    }

    /**
     * 解析表达式，返回运算公式
     *
     * @param str 表达式
     * @return 运算公式
     */
    public Formula parse(String str) {
        ArrayList<Operator> operations = new ArrayList<Operator>(10);
        ArrayList<Parameter> datas = new ArrayList<Parameter>(10);
        boolean isData = false; // true表示上一个是数值 false表示上一个是操作符
        for (int i = 0, length = str.length(); i < length; i++) {
            char c = str.charAt(i);
            int next = i + 1; // 下一个位置

            // 忽略空白字符
            if (Character.isWhitespace(c)) {
                continue;
            }

            // 解析加号
            if (c == '+') {
                if (isData) { // 表示加法符
                    PlusOperator oper = new PlusOperator();
                    operations.add(oper);
                    isData = false;
                }
                // 表示正整数符时，可以忽略向下执行
                continue;
            }

            // 减法 或 负数
            if (c == '-') {
                if (isData) {
                    SubOperator oper = new SubOperator();
                    operations.add(oper);
                    isData = false;
                } else {
                    if (next >= length || "0123456789".indexOf(str.charAt(next)) == -1) {
                        throw new ExpressionException("expression.stdout.message026", str, next);
                    }

                    int index = this.analysis.indexOfFloat(str, next);
                    String value = str.substring(i, index);
                    if (value.indexOf('.') == -1) {
                        datas.add(new ExpressionParameter(Parameter.LONG, Long.valueOf(value)));
                    } else {
                        datas.add(new ExpressionParameter(Parameter.DOUBLE, new Double(value)));
                    }
                    i = index - 1;
                    isData = true;
                }
                continue;
            }

            // 乘法
            if (c == '*') {
                if (!isData) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }

                MupliOperator oper = new MupliOperator();
                operations.add(oper);
                isData = false;
                continue;
            }

            // 除法
            if (c == '/') {
                if (!isData) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }

                DivOperator oper = new DivOperator();
                operations.add(oper);
                isData = false;
                continue;
            }

            // 取余
            if (c == '%') {
                if (!isData) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }

                ModOperator oper = new ModOperator();
                operations.add(oper);
                isData = false;
                continue;
            }

            // 解析并且
            if (c == '&') {
                if (!isData || next >= length) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }
                if (str.charAt(next) != '&') {
                    throw new ExpressionException("expression.stdout.message027", str, next);
                }

                AndOperator oper = new AndOperator();
                operations.add(oper);
                i = next;
                isData = false;
                continue;
            }

            // 解析并且 and
            if ((c == 'a' || c == 'A') && this.analysis.indexOf(str, "and", i, 1, 1) == i) {
                if (!isData) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }

                AndOperator oper = new AndOperator();
                operations.add(oper);
                i = i + 2;
                isData = false;
                continue;
            }

            // 解析或
            if (c == '|') {
                if (!isData || next >= length) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }
                if (str.charAt(next) != '|') {
                    throw new ExpressionException("expression.stdout.message028", str, next);
                }

                OrOperator oper = new OrOperator();
                operations.add(oper);
                i = next;
                isData = false;
                continue;
            }

            // 解析或 or
            if ((c == 'o' || c == 'O') && this.analysis.indexOf(str, "or", i, 1, 1) == i) {
                if (!isData) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }

                OrOperator oper = new OrOperator();
                operations.add(oper);
                i = next;
                isData = false;
                continue;
            }

            // 解析大于 和 大于等于
            if (c == '>') {
                if (!isData || next >= length) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }

                if (str.charAt(next) == '=') {
                    operations.add(new GreaterEqualsOperator());
                    i = next;
                } else {
                    operations.add(new GreaterOperator());
                }
                isData = false;
                continue;
            }

            // 解析小于 和 小于等于
            if (c == '<') {
                if (!isData || next >= length) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }

                if (str.charAt(next) == '=') {
                    operations.add(new LessEqualsOperator());
                    i = next;
                } else {
                    operations.add(new LessOperator());
                }
                isData = false;
                continue;
            }

            // 解析等于
            if (c == '=') {
                if (!isData || next >= length) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }
                if (str.charAt(next) != '=') {
                    throw new ExpressionException("expression.stdout.message029", str, next);
                }

                operations.add(new EqualsOperator());
                i = next;
                isData = false;
                continue;
            }

            // 解析不等于
            if (c == '!' && next < length && str.charAt(next) == '=') { // 只解析不等于, 取反操作放在扩展方法中实现
                if (!isData) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }

                operations.add(new NotEqualsOperator());
                i = next;
                isData = false;
                continue;
            }

            // 解析括号
            if (c == '(') {
                int index = this.analysis.indexOfParenthes(str, i);
                if (index == -1) {
                    throw new ExpressionException("expression.stdout.message030", str, next);
                }

                // 如果上一个是 in 操作符
                else if (!isData && !operations.isEmpty() && (operations.get(operations.size() - 1) instanceof InOperator)) {
                    String content = StringUtils.trimBlank(str.substring(next, index));
                    List<String> list = new ArrayList<String>();
                    this.analysis.split(content, list, this.analysis.getSegment());
                    ArrayParameter parameter = new ArrayParameter();
                    for (String expression : list) {
                        Expression expr = new Expression(expression);
                        parameter.add(new ExpressionParameter(expr.getType(), expr.value()));
                    }
                    datas.add(parameter);
                } else { // 正常括号
                    String content = str.substring(next, index);
                    datas.add(new ComplexParameter(content));
                }

                i = index;
                isData = true;
                continue;
            }

            // 非法字符
            if (c == ')') {
                throw new ExpressionException("expression.stdout.message031", str, next);
            }

            // 字符常量
            if (c == '\'') {
                if (isData) {
                    throw new ExpressionException("expression.stdout.message032", str, next);
                }
                int index = this.analysis.indexOfQuotation(str, i);
                if (index == -1) {
                    throw new ExpressionException("expression.stdout.message025", str, next);
                }

                String content = this.unescapeString(true, str.substring(next, index));
                datas.add(new ExpressionParameter(Parameter.STRING, content));
                i = index;
                isData = true;
                continue;
            }

            // 字符变量
            if (c == '\"') {
                if (isData) {
                    throw new ExpressionException("expression.stdout.message032", str, next);
                }
                int index = this.analysis.indexOfDoubleQuotation(str, i);
                if (index == -1) {
                    throw new ExpressionException("expression.stdout.message025", str, next);
                }

                String content = this.unescapeString(false, str.substring(next, index));
                datas.add(new ExpressionParameter(Parameter.STRING, content));
                i = index;
                isData = true;
                continue;
            }

            // 十六进制数 0x1e4d 和八进制数 01234
            if (c == '0') {
                if (isData) {
                    throw new ExpressionException("expression.stdout.message032", str, next);
                }

                if (next < length) {
                    char nc = str.charAt(next);
                    if (nc == 'x' || nc == 'X') { // 十六进制
                        int start = next + 1;
                        int index = this.analysis.indexOfHex(str, start);
                        if (index == -1) {
                            throw new ExpressionException("expression.stdout.message026", str, next);
                        }

                        datas.add(new ExpressionParameter(Parameter.LONG, Long.parseLong(str.substring(start, index), 16)));
                        i = index - 1;
                        isData = true;
                        continue;
                    } else if ("0123456789".indexOf(nc) != -1) { // 八进制
                        int index = this.analysis.indexOfOctal(str, next);
                        if (index == -1) {
                            throw new ExpressionException("expression.stdout.message026", str, next);
                        }

                        datas.add(new ExpressionParameter(Parameter.LONG, Long.parseLong(str.substring(next, index), 8)));
                        i = index - 1;
                        isData = true;
                        continue;
                    }
                }

                int index = this.analysis.indexOfFloat(str, i);
                String content = str.substring(i, index);
                if (content.indexOf('.') == -1) {
                    datas.add(new ExpressionParameter(Parameter.LONG, Long.valueOf(content)));
                } else {
                    datas.add(new ExpressionParameter(Parameter.DOUBLE, new Double(content)));
                }
                i = index - 1;
                isData = true;
                continue;
            }

            // 数字
            if ("0123456789".indexOf(c) != -1) {
                if (isData) {
                    throw new ExpressionException("expression.stdout.message032", str, next);
                }

                int index = this.analysis.indexOfFloat(str, i);
                String content = str.substring(i, index);
                if (content.indexOf('.') == -1) {
                    datas.add(new ExpressionParameter(Parameter.LONG, Long.valueOf(content)));
                } else {
                    datas.add(new ExpressionParameter(Parameter.DOUBLE, new Double(content)));
                }
                i = index - 1;
                isData = true;
                continue;
            }

            // 空指针 null
            if ((c == 'n' || c == 'N') && this.analysis.indexOf(str, "null", i, 1, 1) == i) {
                if (isData) {
                    throw new ExpressionException("expression.stdout.message032", str, next);
                }

                datas.add(new ExpressionParameter(Parameter.NULL, null));
                i = i + "null".length() - 1;
                isData = true;
                continue;
            }

            // 解析布尔值 true
            if ((c == 't' || c == 'T') && this.analysis.indexOf(str, "true", i, 1, 1) == i) {
                if (isData) {
                    throw new ExpressionException("expression.stdout.message032", str, next);
                }

                datas.add(new ExpressionParameter(Parameter.BOOLEAN, Boolean.TRUE));
                i = i + "true".length() - 1;
                isData = true;
                continue;
            }

            // 解析布尔值 false
            if ((c == 'f' || c == 'F') && this.analysis.indexOf(str, "false", i, 1, 1) == i) {
                if (isData) {
                    throw new ExpressionException("expression.stdout.message032", str, next);
                }

                datas.add(new ExpressionParameter(Parameter.BOOLEAN, Boolean.FALSE));
                i = i + "false".length() - 1;
                isData = true;
                continue;
            }

            // 三目运算符 boolean ? value1 : value2
            if (c == '?') {
                if (datas.isEmpty()) { // 如果是三目操作则只能有一个操作符
                    throw new ExpressionException("expression.stdout.message033", str);
                }

                int index = this.analysis.indexOfSemicolon(str, i);
                if (index == -1) {
                    throw new ExpressionException("expression.stdout.message034", str);
                }

                ComplexParameter trueRun = new ComplexParameter(str.substring(next, index));
                ComplexParameter falseRun = new ComplexParameter(str.substring(index + 1));

                TwoParameter data = new TwoParameter();
                data.setTrueRun(trueRun);
                data.setFalseRun(falseRun);

                TreeOperator oper = new TreeOperator();
                operations.add(oper);
                datas.add(data);
                i = length;
                isData = true;
                continue;
            }

            // 在范围内 in
            if ((c == 'i' || c == 'I') && this.analysis.indexOf(str, "in", i, 0, 1) == i) {
                if (!isData || next >= length) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }

                InOperator oper = new InOperator();
                operations.add(oper);
                i = next;
                isData = false;
                continue;
            }

            // 不在范围内 not in
            if ((c == 'n' || c == 'N') && this.analysis.indexOf(str, "not", i, 1, 0) == i && this.analysis.startsWith(str, "in", i + 3, true)) {
                int index = str.indexOf("in", i + 3); // 查找 not in 语句中 in 关键字的位置
                if (index == -1) {
                    throw new ExpressionException("expression.stdout.message007", str, i + 4);
                }
                index += "in".length(); // in 关键字的结束位置
                if (!isData || index >= str.length() || !this.analysis.charAt(str, index, 1)) {
                    throw new ExpressionException("expression.stdout.message026", str, index);
                }

                NotInOperator oper = new NotInOperator();
                operations.add(oper);
                i = index - 1;
                isData = false;
                continue;
            }

            // 日 day
            if ((c == 'd' || c == 'D') && this.analysis.indexOf(str, "day", i, 2, 1) == i) {
                if (!isData) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }
                int index = datas.size() - 1;
                if (index < 0) {
                    throw new ExpressionException("expression.stdout.message006", str, next);
                }

                datas.set(index, new DateUnitParameter(datas.get(index), Calendar.DAY_OF_MONTH));
                i = i + "day".length() - 1;
                continue;
            }

            // 月份 month
            if ((c == 'm' || c == 'M') && this.analysis.indexOf(str, "month", i, 2, 1) == i) {
                if (!isData) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }
                int index = datas.size() - 1;
                if (index < 0) {
                    throw new ExpressionException("expression.stdout.message006", str, next);
                }

                datas.set(index, new DateUnitParameter(datas.get(index), Calendar.MONTH));
                i = i + "month".length() - 1;
                continue;
            }

            // 年份 year
            if ((c == 'y' || c == 'Y') && this.analysis.indexOf(str, "year", i, 2, 1) == i) {
                if (!isData) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }
                int index = datas.size() - 1;
                if (index < 0) {
                    throw new ExpressionException("expression.stdout.message006", str, next);
                }

                datas.set(index, new DateUnitParameter(datas.get(index), Calendar.YEAR));
                i = i + "year".length() - 1;
                continue;
            }

            // 小时 hour
            if ((c == 'h' || c == 'H') && this.analysis.indexOf(str, "hour", i, 2, 1) == i) {
                if (!isData) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }
                int index = datas.size() - 1;
                if (index < 0) {
                    throw new ExpressionException("expression.stdout.message006", str, next);
                }

                datas.set(index, new DateUnitParameter(datas.get(index), Calendar.HOUR));
                i = i + "hour".length() - 1;
                continue;
            }

            // 分钟 minute
            if ((c == 'm' || c == 'M') && this.analysis.indexOf(str, "minute", i, 2, 1) == i) {
                if (!isData) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }
                int index = datas.size() - 1;
                if (index < 0) {
                    throw new ExpressionException("expression.stdout.message006", str, next);
                }

                datas.set(index, new DateUnitParameter(datas.get(index), Calendar.MINUTE));
                i = i + "minute".length() - 1;
                continue;
            }

            // 秒 second
            if ((c == 's' || c == 'S') && this.analysis.indexOf(str, "second", i, 2, 1) == i) {
                if (!isData) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }
                int index = datas.size() - 1;
                if (index < 0) {
                    throw new ExpressionException("expression.stdout.message006", str, next);
                }

                datas.set(index, new DateUnitParameter(datas.get(index), Calendar.SECOND));
                i = i + "second".length() - 1;
                continue;
            }

            // 毫秒 millisecond
            if ((c == 'm' || c == 'M') && this.analysis.indexOf(str, "millis", i, 2, 1) == i) {
                if (!isData) {
                    throw new ExpressionException("expression.stdout.message026", str, next);
                }
                int index = datas.size() - 1;
                if (index < 0) {
                    throw new ExpressionException("expression.stdout.message006", str, next);
                }

                datas.set(index, new DateUnitParameter(datas.get(index), Calendar.MILLISECOND));
                i = i + "millis".length() - 1;
                continue;
            }

            // 扩展接口
            int oldDataSize = datas.size(), oldOperationSize = operations.size();
            int index = this.parse(str, i, isData, datas, operations);
            int c1 = datas.size() - oldDataSize;
            int c2 = operations.size() - oldOperationSize;
            if (c1 > c2) {
                isData = true;
            } else if (c1 < c2) {
                isData = false;
            }
            i = index;
        }

        // 生成运算公式
        return this.calc(datas, operations);
    }

    /**
     * 解析表达式
     *
     * @param str       表达式
     * @param index     当前字符数组所在位置（大于等于0且小于字符数组长度）
     * @param isData    true表示上一个是数值; false表示上一个是操作符
     * @param datas     表达式数值
     * @param operation 操作符对象
     * @return 记录解析符号位置 必须大于等0小于字符数组长度
     * @throws ExpressionException 表达式不合法
     */
    public abstract int parse(String str, int index, boolean isData, List<Parameter> datas, List<Operator> operation) throws ExpressionException;

    /**
     * 字符串转义
     *
     * @param singleQuote true表示单引号，false表示双引号
     * @param str         字符串
     * @return 字符串
     */
    public String unescapeString(boolean singleQuote, String str) {
        return this.analysis.unescapeString(str);
    }

    /**
     * 将数据代入公式执行运算并返回结果数值
     *
     * @param datas      参数集合
     * @param operations 操作集合
     * @return 计算公式
     */
    protected Formula calc(ArrayList<Parameter> datas, ArrayList<Operator> operations) {
        return new Formula(datas, operations);
    }
}
