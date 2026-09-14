package cn.org.expect.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 当前请求会话工具类
 */
public final class HttpSessionUtils {

    private HttpSessionUtils() {
    }

    /**
     * 获取当前请求已存在的用户 Session，不主动创建新 Session
     *
     * @return 当前用户 Session，不存在请求或 Session 时返回 null
     */
    public static HttpSession get() {
        HttpServletRequest request = HttpRequestUtils.get();
        return request == null ? null : request.getSession();
    }

    /**
     * 设置当前请求的用户 Session 中的指定属性
     *
     * @param name  属性名
     * @param value 属性值
     */
    public static void setAttribute(String name, Object value) {
        HttpSession session = get();
        if (session != null) {
            session.setAttribute(name, value);
        }
    }

    /**
     * 获取当前请求的用户 Session 中的指定属性
     *
     * @param name 属性名
     * @param <E>  属性类型
     * @return 指定属性，不存在时返回 null
     */
    @SuppressWarnings("unchecked")
    public static <E> E getAttribute(String name) {
        HttpSession session = get();
        if (session != null) {
            return (E) session.getAttribute(name);
        }
        return null;
    }

    /**
     * 移除当前请求的用户 Session 中的指定属性
     *
     * @param name 属性名
     */
    public static void removeAttribute(String name) {
        HttpSession session = get();
        if (session != null) {
            session.removeAttribute(name);
        }
    }

    /**
     * 判断当前请求是否存在用户 Session
     *
     * @return true表示存在用户 Session
     */
    public static boolean isNotNull() {
        return get() != null;
    }
}
