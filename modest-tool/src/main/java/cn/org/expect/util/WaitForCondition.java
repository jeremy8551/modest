package cn.org.expect.util;

/**
 * 等待条件
 */
public interface WaitForCondition {

    /**
     * 等待条件
     *
     * @return 返回true表示继续等待，false表示终止
     */
    boolean test();
}
