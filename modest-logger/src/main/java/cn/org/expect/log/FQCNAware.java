package cn.org.expect.log;

import cn.org.expect.util.StackTraceUtils;

/**
 * Fully Qualified Class Name（全限定类名）用于跟踪日志的来源
 */
public interface FQCNAware {

    /**
     * 用于定位输出日志的代码位置信息的标识符 <br>
     * 在日志模块中，使用 {@linkplain  StackTraceUtils#get(String)} 输出打印日志的类、方法、行号，这个方法的字符串参数就是 FQCN <br>
     * 需要根据 FQCN 来返回输出日志的类、方法、行号
     *
     * @param fqcn 字符串（在最前面使用 ^ 表示从堆栈的底部开始向上查找，默认是从堆栈的顶部开始向下查找）
     */
    void setFQCN(String fqcn);
}
