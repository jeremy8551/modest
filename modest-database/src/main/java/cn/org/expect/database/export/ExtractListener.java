package cn.org.expect.database.export;

import java.util.ArrayList;
import java.util.List;

/**
 * 卸载数据功能的监听器
 *
 * @author jeremy8551@gmail.com
 * @createtime 2020-11-13
 */
public class ExtractListener {

    private final List<ExtractUserListener> list;

    private final ExtracterContext context;

    /**
     * 初始化
     *
     * @param context 上下文信息
     */
    public ExtractListener(ExtracterContext context) {
        this.context = context;
        this.list = new ArrayList<ExtractUserListener>();
    }

    /**
     * 添加用户自定义监听器
     *
     * @param listeners 用户自定义监听器集合
     */
    public void setListener(List<ExtractUserListener> listeners) {
        if (listeners != null) {
            this.list.clear();
            for (ExtractUserListener listener : listeners) {
                if (listener != null) {
                    this.list.add(listener);
                }
            }
        }
    }

    /**
     * 判断任务已准备就绪可以执行
     *
     * @return 返回 true 表示任务已准备就绪可以执行，false 表示任务还未准备就绪不能执行
     */
    public boolean ready() {
        for (ExtractUserListener listener : this.list) {
            if (!listener.ready(this.context)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 卸数任务运行前执行的逻辑
     */
    public void before() {
        for (ExtractUserListener listener : this.list) {
            listener.before(this.context);
        }
    }

    /**
     * 卸数任务运行发生错误时执行的逻辑
     *
     * @param e 异常信息
     */
    public void catchError(Throwable e) {
        if (this.list.isEmpty()) {
            throw new ExtractException("extract.stdout.message003", this.context.getName(), e);
        }

        for (ExtractUserListener listener : this.list) {
            listener.catchException(this.context, e);
        }
    }

    /**
     * 卸数任务运行完毕后执行的逻辑
     */
    public void after() {
        for (ExtractUserListener listener : this.list) {
            listener.after(this.context);
        }
    }

    /**
     * 退出卸数任务前执行的逻辑
     */
    public void close() {
        for (ExtractUserListener listener : this.list) {
            listener.quit(this.context);
        }
    }
}
