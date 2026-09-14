package cn.org.expect.script.command;

import java.io.File;
import java.util.List;

import cn.org.expect.codegen.DatabaseDesignParser;
import cn.org.expect.codegen.TableDesign;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.io.PathExpression;

/**
 * 读取数据库设计工作簿并将表设计保存到脚本局部变量
 */
public class CodegenCommand extends AbstractTraceCommand {

    /** 工作簿路径表达式 */
    private final String workbookFilepath;

    /**
     * 创建 codegen 命令
     *
     * @param compiler         脚本命令编译器
     * @param command          原始脚本命令
     * @param workbookFilepath 工作簿路径表达式
     */
    public CodegenCommand(UniversalCommandCompiler compiler, String command, String workbookFilepath) {
        super(compiler, command);
        this.workbookFilepath = workbookFilepath;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        File workbookFile = PathExpression.toFile(session, context, this.workbookFilepath);
        DatabaseDesignParser parser = new DatabaseDesignParser();
        List<TableDesign> tables = parser.execute(workbookFile);
        if (tables.isEmpty()) {
            throw new IllegalArgumentException("工作簿中未找到包含必要列名的数据库表设计");
        }

        context.addLocalVariable("tableDesignList", tables);
        session.setValue(tables);
        return 0;
    }
}
