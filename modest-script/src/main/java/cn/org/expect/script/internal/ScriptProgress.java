package cn.org.expect.script.internal;

import cn.org.expect.printer.Printer;
import cn.org.expect.printer.Progress;

/**
 * 脚本引擎进度输出类
 *
 * @author jeremy8551@gmail.com
 */
public class ScriptProgress extends Progress {

    public ScriptProgress(Printer out, String message, int total) {
        super(out, message, total);
    }

    public ScriptProgress(String taskId, Printer out, String message, int total) {
        super(taskId, out, message, total);
    }

    public String toString(boolean global) {
        StringBuilder buf = new StringBuilder(200);
        buf.append("declare ");
        if (global) {
            buf.append("global ");
        }
        buf.append("progress ");
        buf.append("use ");
        buf.append(this.out.getClass().getName());
        buf.append(" print ");
        buf.append('"');
        buf.append(this.message);
        buf.append('"');
        buf.append(" total ");
        buf.append(this.total);
        buf.append(" times");
        return buf.toString();
    }
}
