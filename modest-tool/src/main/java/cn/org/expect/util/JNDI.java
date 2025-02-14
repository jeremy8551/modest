package cn.org.expect.util;

import javax.naming.Context;
import javax.naming.InitialContext;

public class JNDI {

    /**
     * 查询 JNDI 资源
     *
     * @param <E>      资源类型
     * @param jndiName 资源定位符
     * @return 资源对象
     */
    @SuppressWarnings("unchecked")
    public static <E> E lookup(String jndiName) {
        try {
            Context context;
            if (StringUtils.startsWith(jndiName, "java:", 0, true, true)) {
                context = new InitialContext();
            } else {
                context = (Context) new InitialContext().lookup("java:comp/env");
            }
            return (E) context.lookup(jndiName);
        } catch (Throwable e) {
            throw new RuntimeException(jndiName, e);
        }
    }
}
