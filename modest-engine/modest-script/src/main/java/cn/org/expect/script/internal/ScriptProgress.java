package cn.org.expect.script.internal;

import cn.org.expect.printer.Printer;
import cn.org.expect.printer.Progress;

/**
 * 脚本引擎进度输出类
 *
 * @author jeremy8551@qq.com
 */
public class ScriptProgress extends Progress {

    public ScriptProgress(Printer out, String message, int total) {
        super(out, message, total);
    }

    public ScriptProgress(String taskId, Printer out, String message, int total) {
        super(taskId, out, message, total);
    }

}
