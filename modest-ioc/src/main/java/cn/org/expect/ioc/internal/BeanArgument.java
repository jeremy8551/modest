package cn.org.expect.ioc.internal;

/**
 * 组件构造方法的参数
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/26
 */
public class BeanArgument {

    /** 组件名 */
    private String name;

    /** 参数数组 */
    private Object[] args;

    /**
     * 初始化
     *
     * @param name 组件名
     * @param args 参数数组
     */
    public BeanArgument(String name, Object[] args) {
        this.name = name;
        this.args = args;
    }

    /**
     * 初始化
     *
     * @param args 参数数组
     */
    public BeanArgument(Object[] args) {
        if (args.length == 0) {
            this.args = args;
            return;
        }

        Object first = args[0];
        if (first instanceof String) {
            this.name = (String) first;
            this.args = new Object[args.length - 1];
            System.arraycopy(args, 1, this.args, 0, this.args.length);
            return;
        }

        this.args = args;
    }

    /**
     * 组件名
     *
     * @return 字符串
     */
    public String getName() {
        return this.name;
    }

    /**
     * 其他参数
     *
     * @return 参数数组
     */
    public Object[] getArgs() {
        return this.args;
    }

    /**
     * 查询参数值
     *
     * @param i 位置信息，从0开始
     * @return 参数值
     */
    public Object get(int i) {
        return this.args[i];
    }

    /**
     * 参数个数
     *
     * @return 个数
     */
    public int size() {
        return this.args.length;
    }
}
