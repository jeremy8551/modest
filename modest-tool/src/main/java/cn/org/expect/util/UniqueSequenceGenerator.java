package cn.org.expect.util;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class UniqueSequenceGenerator {

    /** 编号规则，需要使用 {} 或 {0} 表示编号 */
    private final String message;

    /** 编号计数器 */
    private final AtomicLong count;

    /**
     * 初始化序号生成器
     *
     * @param value 初始值
     */
    public UniqueSequenceGenerator(String message, long value) {
        if (message == null || (!message.contains("{}") && !message.contains("{0}"))) {
            throw new IllegalArgumentException(message);
        }

        this.message = message;
        this.count = new AtomicLong(value);
    }

    /**
     * 生成唯一序号
     *
     * @return 序号
     */
    public long next() {
        return this.count.getAndIncrement();
    }

    /**
     * 生成唯一序号
     *
     * @return 序号
     */
    public String nextString() {
        String value = this.message;
        value = StringUtils.replaceAll(value, "{timestamp}", Dates.format17());
        value = StringUtils.replaceAll(value, "{date}", Dates.format08(new Date()));
        return StringUtils.replacePlaceholder(value, this.next());
    }
}
