package cn.org.expect.util;

public class Terminator implements Terminate {

    /** 终止状态：true表示终止，false表示未终止 */
    protected volatile boolean terminate = false;

    public void terminate() throws Exception {
        this.terminate = true;
    }

    public boolean isTerminate() {
        return this.terminate;
    }
}
