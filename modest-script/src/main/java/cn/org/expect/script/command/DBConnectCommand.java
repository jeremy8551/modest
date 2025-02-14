package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import javax.sql.DataSource;

import cn.org.expect.database.JdbcDao;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptInputStream;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.internal.ScriptDataSource;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 建立数据库连接命令 <br>
 * 关闭数据库连接命令 <br>
 * <p>
 * db connect to name; <br>
 * db connect reset; <br>
 */
public class DBConnectCommand extends AbstractTraceCommand implements UniversalScriptInputStream, NohupCommandSupported {

    /** 数据库编目名 */
    private String name;

    public DBConnectCommand(UniversalCommandCompiler compiler, String command, String name) {
        super(compiler, command);
        this.name = name;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.name)) {
            this.name = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "db connect to", this.name);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (StringUtils.isBlank(this.name)) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message004", this.command));
            return UniversalScriptCommand.COMMAND_ERROR;
        }

        ScriptDataSource dataSource = ScriptDataSource.get(context);
        JdbcDao dao = dataSource.getDao();
        boolean print = session.isEchoEnable() || forceStdout;

        // 关闭当前数据库连接
        if ("reset".equalsIgnoreCase(this.name)) {
            if (print) {
                stdout.println("db connect reset");
            }

            dao.commit();
            dao.close();
            return 0;
        }

        // 先关闭数据库连接，再创建新的连接
        if (dao.isConnected()) {
            dao.commit();
            dao.close();
        }

        // 找到对应的数据库连接池
        DataSource pool = dataSource.getPool(this.name);
        dao.setConnection(pool.getConnection(), true);
        dataSource.setCatalog(this.name); // 切换当前数据库编目名
        context.addLocalVariable(UniversalScriptVariable.VARNAME_CATALOG, this.name); // 保存当前数据库编目名

        if (print) {
            stdout.println("db connect to " + this.name);
        }

        if (dao.getDialect() != null) {
            context.getEngine().getChecker().setDatabaseKeywords(dao.getDialect().getKeyword(dao.getConnection()));
        }
        return 0;
    }

    public boolean enableNohup() {
        return true;
    }
}
