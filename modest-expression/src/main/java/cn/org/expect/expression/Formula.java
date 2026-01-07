package cn.org.expect.expression;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cn.org.expect.expression.operation.Operator;
import cn.org.expect.expression.parameter.Parameter;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.CharTable;

/**
 * 运算公式
 *
 * @author jeremy8551@gmail.com
 * @createtime 2014-05-27 14:22:12
 */
public class Formula {
    private final static Log log = LogFactory.getLog(Formula.class);

    /** 参数集合 */
    private final List<Parameter> datas;

    /** 操作集合 */
    private final List<Operator> operations;

    /** 循环次数 */
    private int loop;

    /**
     * 初始化
     *
     * @param datas      参数
     * @param operations 运算操作符
     */
    public Formula(List<Parameter> datas, List<Operator> operations) {
        this.datas = datas;
        this.operations = operations;
        this.loop = 0;
    }

    /**
     * 返回公式中参数个数
     *
     * @return 参数个数
     */
    public int getParameterSize() {
        return this.datas.size();
    }

    /**
     * 返回公式中的参数集合
     *
     * @return 参数集合
     */
    public List<Parameter> getParameters() {
        return Collections.unmodifiableList(this.datas);
    }

    /**
     * 返回公式中的操作符集合
     *
     * @return 操作集合
     */
    public List<Operator> getOperators() {
        return Collections.unmodifiableList(this.operations);
    }

    /**
     * 执行算术运算
     *
     * @return 运算结果
     */
    public Parameter execute() {
        if (this.datas.isEmpty()) {
            return null;
        }

        if (this.datas.size() == 1) {
            if (this.operations.isEmpty()) {
                Parameter parameter = this.datas.get(0);
                parameter.execute();
                return parameter;
            } else {
                throw new ExpressionException("expression.stdout.message037");
            }
        }

        // 按从高到低顺序排序运算符优先级
        int[] array = new int[this.operations.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = this.operations.get(i).getPriority();
        }
        Arrays.sort(array);

        // 按运算符优先级执行运算
        int last = -1;
        for (int priority : array) {
            if (priority == last) { // 不能重复执行
                continue;
            } else {
                last = priority;
            }

            for (int index = 0; index < this.operations.size(); index++) {
                int next = index + 1;
                if (next >= this.datas.size()) {
                    throw new ExpressionException("expression.stdout.message037");
                }

                // 如果等于运算符优先级，执行运算并替换原公式中的参数
                Operator operation = this.operations.get(index); // 运算符
                if (operation.getPriority() == priority) {
                    Parameter left = this.datas.get(index);
                    Parameter right = this.datas.get(next);
                    Parameter data = this.execute(left, operation, right);

                    this.datas.set(next, data); // 替换参数
                    this.datas.remove(index); // 删除无效参数
                    this.operations.remove(index);
                    index--;
                }
            }

            if (this.datas.size() == 1) {
                if (this.operations.isEmpty()) {
                    return this.datas.get(0);
                } else {
                    throw new ExpressionException("expression.stdout.message037");
                }
            }
        }

        // 预防无限循环
        if (++this.loop >= 100) {
            throw new ExpressionException("expression.stdout.message005", this.toString(this.datas, this.operations));
        } else {
            return this.execute();
        }
    }

    /**
     * 执行运算
     *
     * @param left      左侧参数
     * @param operation 运算操作符
     * @param right     右侧参数
     * @return 参数
     */
    public Parameter execute(Parameter left, Operator operation, Parameter right) {
        if (operation == null) {
            if (left == null && right == null) {
                return null;
            } else if (left != null) {
                left.execute();
                return left;
            } else {
                right.execute();
                return right;
            }
        } else {
            if (left == null || right == null) {
                throw new ExpressionException("expression.stdout.message038", left, right);
            }

            left.execute();
            right.execute();

            if (log.isTraceEnabled()) {
                log.trace("{} data1={}, data2={}", operation, left, right);
            }

            Parameter parameter = operation.execute(left, right);
            if (parameter == null) {
                throw new ExpressionException("expression.stdout.message039");
            }

            if (log.isTraceEnabled()) {
                log.trace("expression.stdout.message040", left, operation, right, parameter);
            }

            parameter.execute();
            int type = parameter.getType();
            Object value = parameter.value();

            left.setType(type);
            left.setValue(value);

            right.setType(type);
            right.setValue(value);
            return parameter;
        }
    }

    /**
     * 测试使用
     *
     * @param datas      参数
     * @param operations 运算操作符
     * @return 字符串
     */
    protected String toString(List<Parameter> datas, List<Operator> operations) {
        CharTable table = new CharTable();
        table.addTitle("Type");
        table.addTitle("Class");
        table.addTitle("");

        Iterator<Parameter> it = datas.iterator();
        Iterator<Operator> it1 = operations.iterator();
        while (it.hasNext()) {
            Parameter data = it.next();

            table.addCell("Data");
            table.addCell(data.getClass().getName());
            table.addCell(data);

            if (it1.hasNext()) {
                Operator operator = it1.next();

                table.addCell("Operator");
                table.addCell(operator.getClass().getName());
                table.addCell(operator);
            }
        }

        while (it1.hasNext()) {
            Operator operator = it1.next();

            table.addCell("Operator");
            table.addCell(operator.getClass().getName());
            table.addCell(operator);
        }

        return table.toString(CharTable.Style.DB2);
    }

    public String toStandardString() {
        return this.toString(this.datas, this.operations);
    }
}
