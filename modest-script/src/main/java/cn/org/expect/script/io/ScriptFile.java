package cn.org.expect.script.io;

import java.io.File;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

/**
 * 文件信息
 *
 * @author jeremy8551@gmail.com
 */
public class ScriptFile extends File {
    private final static long serialVersionUID = 1L;

    /**
     * 替换文件路径中的变量
     *
     * @param session  用户会话信息
     * @param context  脚本引擎上下文信息
     * @param filepath 路径信息（如果是文件名，则默认使用当前目录作为父目录）
     */
    public ScriptFile(UniversalScriptSession session, UniversalScriptContext context, String filepath) {
        super(replaceFilepath(session, context, filepath));
    }

    /**
     * 替换文件路径中的变量
     *
     * @param session  用户会话信息
     * @param context  脚本引擎上下文信息
     * @param filepath 路径信息（如果是文件名，则默认使用当前目录作为父目录）
     * @return 文件路径
     */
    protected static String replaceFilepath(UniversalScriptSession session, UniversalScriptContext context, String filepath) {
        String path = replaceFilepath(session, context, filepath, true);
        String parent = FileUtils.getParent(path);
        return StringUtils.trimBlank(parent == null ? FileUtils.joinPath(session.getDirectory(), path) : path);
    }

    public static String replaceFilepath(UniversalScriptSession session, UniversalScriptContext context, String filepath, boolean local) {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String path = analysis.replaceShellVariable(session, context, filepath, true, true);
        path = analysis.unQuotation(path);
        return FileUtils.replaceFolderSeparator(path, local);
    }
}
