package cn.org.expect.util;

import cn.org.expect.Modest;

/**
 * 日志工具类
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/11/24
 */
public class StackTraceUtils {

    /** 属性值设置为true，表示打印日志堆栈 */
    public final static String PROPERTY_LOG_STACKTRACE = Modest.class.getPackage().getName() + ".logStackTrace";

    /** 是否打印日志跟踪信息 */
    public static volatile boolean print = Boolean.parseBoolean(System.getProperty(PROPERTY_LOG_STACKTRACE));

    /**
     * 返回堆栈信息，从 util.cn.org.expect.StackTraceUtils.get:28 开始向上的所有调用步骤  <br>
     * 用于定位输出日志的代码位置 <br>
     * 这个方法只能是单独一个类，不能写在其他日志系统中
     *
     * @param fqcn 全限定类名
     * @return 堆栈信息
     */
    public static StackTraceElement get(String fqcn) {
        StackTraceElement[] array = new Throwable().getStackTrace();

        if (print) {
            System.out.println("search FQCN " + fqcn + " in StackTrace:");
            for (StackTraceElement e : array) {
                System.out.println("    " + e.getClassName() + "." + e.getMethodName() + ":" + e.getLineNumber());
            }
        }

        if (fqcn.length() > 0 && fqcn.charAt(0) == '^') {
            fqcn = fqcn.substring(1);
            for (int i = array.length - 1; i >= 0; i--) {
                StackTraceElement trace = array[i];
                if (fqcn.equals(trace.getClassName())) {
                    int next = i + 1;
                    if (next < array.length) {
                        return array[next];
                    } else {
                        return trace;
                    }
                }
            }
        } else {
            for (int i = 0; i < array.length; i++) {
                StackTraceElement trace = array[i];
                if (fqcn.equals(trace.getClassName())) {
                    int next = i + 1;

                    if (next < array.length) {
                        StackTraceElement element = array[next];
                        if (fqcn.equals(element.getClassName())) {
                            continue;
                        }
                        return element;
                    } else {
                        return trace;
                    }
                }
            }
        }

        return new StackTraceElement("?", "?", "?", -1);
    }

}
