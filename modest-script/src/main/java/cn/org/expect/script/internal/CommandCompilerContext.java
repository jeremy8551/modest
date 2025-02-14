package cn.org.expect.script.internal;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Ensure;

/**
 * 脚本引擎命令的工厂配置信息
 *
 * @author jeremy8551@gmail.com
 */
public class CommandCompilerContext {

    /** 命令编译器 */
    private final UniversalCommandCompiler compiler;

    /**
     * 初始化
     *
     * @param compiler 命令编译器
     */
    public CommandCompilerContext(UniversalCommandCompiler compiler) {
        this.compiler = Ensure.notNull(compiler);
    }

    /**
     * 返回脚本命令对应的编译器
     *
     * @return 编译器
     */
    public UniversalCommandCompiler getCompiler() {
        return compiler;
    }

    /**
     * 判断命令编译器类是否与类信息参数 cls 相等
     *
     * @param type 类信息
     * @return 返回true表示编译命令与参数相等
     */
    public boolean equalsClass(Class<?> type) {
        return ClassUtils.equals(this.compiler.getClass(), type);
    }

    public boolean equals(Object obj) {
        if (obj instanceof CommandCompilerContext) { // 判断类型是否相等
            CommandCompilerContext context = (CommandCompilerContext) obj;
            UniversalCommandCompiler compiler = context.getCompiler();
            return compiler != null && compiler.getClass().equals(this.compiler.getClass());
        }
        return false;
    }
}
