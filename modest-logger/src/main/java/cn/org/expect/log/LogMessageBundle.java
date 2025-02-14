package cn.org.expect.log;

import cn.org.expect.message.ResourceMessageBundle;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

public class LogMessageBundle {

    /** 日志上下文信息 */
    protected LogContext context;

    /** 国际化资源 */
    protected ResourceMessageBundle resourceBundle;

    public LogMessageBundle(LogContext context) {
        this.context = Ensure.notNull(context);
        this.resourceBundle = Ensure.notNull(context.getResourceBundle());
    }

    /**
     * 判断是否是国际化信息
     *
     * @param key 资源编号
     * @return 返回true表示是国际化信息，false表示不是
     */
    public boolean isResourceBundle(String key) {
        return this.resourceBundle.contains(key);
    }

    /**
     * 返回国际化资源信息
     *
     * @param key  资源编号
     * @param args 参数数组
     * @return 国际化信息
     */
    public String getResourceBundle(String key, Object... args) {
        String value = this.resourceBundle.get(key);
        return StringUtils.replacePlaceHolder(value, args);
    }
}
