package cn.org.expect.script.internal;

import cn.org.expect.os.OSFtpCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptProgram;

public class FtpList implements UniversalScriptProgram {

    public final static String key = "FtpList";

    public static FtpList get(UniversalScriptContext context, boolean... array) {
        boolean global = array.length != 0 && array[0];
        FtpList obj = context.getProgram(key, global);
        if (obj == null) {
            obj = new FtpList();
            context.addProgram(key, obj, global);
        }
        return obj;
    }

    /** FTP 客户端接口 */
    private OSFtpCommand ftp;

    /**
     * 初始化
     */
    public FtpList() {
    }

    /**
     * 添加 FTP 客户端到脚本引擎上下文中
     *
     * @param ftp FTP客户端
     */
    public void add(OSFtpCommand ftp) {
        if (this.ftp != null) {
            this.ftp.close();
        }
        this.ftp = ftp;
    }

    /**
     * 返回 FTP 客户端
     *
     * @return FTP客户端
     */
    public OSFtpCommand getFTPClient() {
        return ftp;
    }

    public ScriptProgramClone deepClone() {
        FtpList obj = new FtpList();
        obj.ftp = this.ftp;
        return new ScriptProgramClone(key, obj);
    }

    public void close() {
        if (this.ftp != null) {
            this.ftp.close();
            this.ftp = null;
        }
    }

}
