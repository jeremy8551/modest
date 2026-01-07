package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import cn.org.expect.database.Jdbc;
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
import cn.org.expect.script.internal.ScriptDataSource;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.CharTable;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringComparator;
import cn.org.expect.util.StringUtils;

/**
 * 返回数据库信息 <br>
 * <p>
 * db get cfg for information
 */
public class DBGetCfgForCommand extends AbstractTraceCommand implements UniversalScriptInputStream {

    /** 数据库编目名 */
    private String information;

    public DBGetCfgForCommand(UniversalCommandCompiler compiler, String command, String information) {
        super(compiler, command);
        this.information = information;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.information)) {
            this.information = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "db get cfg for", this.information);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        boolean print = session.isEchoEnable() || forceStdout;
        if (print) {
            stdout.println(session.getAnalysis().replaceShellVariable(session, context, this.getScript(), true, true));
        }

        String str = StringUtils.trimBlank(session.getAnalysis().replaceShellVariable(session, context, this.information, true, true));
        String[] words = StringUtils.splitByBlank(str.toLowerCase());
        if (ArrayUtils.equals(words, new String[]{"field", "type"}, new StringComparator())) {
            this.printFieldInfo(session, context, stdout, print);
            return 0;
        }

        if (ArrayUtils.equals(words, new String[]{"table", "type"}, new StringComparator())) {
            this.printTableInfo(session, context, stdout, print);
            return 0;
        }

        if (ArrayUtils.equals(words, new String[]{"catalog"}, new StringComparator())) {
            this.printCatalogInfo(session, context, stdout, print);
            return 0;
        }

        if (ArrayUtils.equals(words, new String[]{"schema"}, new StringComparator())) {
            this.printSchemaInfo(session, context, stdout, print);
            return 0;
        }

        stderr.println(ResourcesUtils.getMessage("script.stderr.message013", this.command, str));
        return UniversalScriptCommand.COMMAND_ERROR;
    }

    private void printFieldInfo(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, boolean print) throws SQLException {
        CharTable ct = new CharTable();
        ct.addTitle("TYPE_NAME");
        ct.addTitle("DATA_TYPE");
        ct.addTitle("PRECISION");
        ct.addTitle("LITERAL_PREFIX");
        ct.addTitle("LITERAL_SUFFIX");
        ct.addTitle("CREATE_PARAMS");
        ct.addTitle("NULLABLE");
        ct.addTitle("SEARCHABLE");
        ct.addTitle("CASE_SENSITIVE");
        ct.addTitle("UNSIGNED_ATTRIBUTE");
        ct.addTitle("FIXED_PREC_SCALE");
        ct.addTitle("AUTO_INCREMENT");
        ct.addTitle("LOCAL_TYPE_NAME");
        ct.addTitle("MINIMUM_SCALE");
        ct.addTitle("MAXIMUM_SCALE");
        ct.addTitle("NUM_PREC_RADIX");
        ct.addTitle("SQL_DATA_TYPE");
        ct.addTitle("SQL_DATETIME_SUB");

        DatabaseMetaData metaData;
        ResultSet resultSet = null;
        JdbcDao dao = ScriptDataSource.get(context).getDao();
        try {
            metaData = dao.getConnection().getMetaData();
            resultSet = metaData.getTypeInfo();
            while (resultSet.next()) {
                ct.addCell(resultSet.getString("TYPE_NAME"));
                ct.addCell(Jdbc.getObject(resultSet, "DATA_TYPE"));
                ct.addCell(Jdbc.getObject(resultSet, "PRECISION"));
                ct.addCell(Jdbc.getObject(resultSet, "LITERAL_PREFIX"));
                ct.addCell(Jdbc.getObject(resultSet, "LITERAL_SUFFIX"));
                ct.addCell(Jdbc.getObject(resultSet, "CREATE_PARAMS"));
                ct.addCell(Jdbc.getObject(resultSet, "NULLABLE"));
                ct.addCell(Jdbc.getObject(resultSet, "SEARCHABLE"));
                ct.addCell(Jdbc.getObject(resultSet, "CASE_SENSITIVE"));
                ct.addCell(Jdbc.getObject(resultSet, "UNSIGNED_ATTRIBUTE"));
                ct.addCell(Jdbc.getObject(resultSet, "FIXED_PREC_SCALE"));
                ct.addCell(Jdbc.getObject(resultSet, "AUTO_INCREMENT"));
                ct.addCell(Jdbc.getObject(resultSet, "LOCAL_TYPE_NAME"));
                ct.addCell(Jdbc.getObject(resultSet, "MINIMUM_SCALE"));
                ct.addCell(Jdbc.getObject(resultSet, "MAXIMUM_SCALE"));
                ct.addCell(Jdbc.getObject(resultSet, "NUM_PREC_RADIX"));
                ct.addCell(Jdbc.getObject(resultSet, "SQL_DATA_TYPE"));
                ct.addCell(Jdbc.getObject(resultSet, "SQL_DATETIME_SUB"));
            }

            if (print) {
                stdout.println(ct.toString(CharTable.Style.DB2));
            }
        } finally {
            IO.closeQuietly(resultSet);
        }
    }

    private void printTableInfo(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, boolean print) throws SQLException {
        CharTable ct = new CharTable();
        ct.addTitle("TABLE_TYPE");

        DatabaseMetaData metaData;
        ResultSet resultSet = null;
        JdbcDao dao = ScriptDataSource.get(context).getDao();
        try {
            metaData = dao.getConnection().getMetaData();
            resultSet = metaData.getTableTypes();
            while (resultSet.next()) {
                ct.addCell(resultSet.getString("TABLE_TYPE"));
            }

            if (print) {
                stdout.println(ct.toString(CharTable.Style.DB2));
            }
        } finally {
            IO.closeQuietly(resultSet);
        }
    }

    private void printCatalogInfo(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, boolean print) throws SQLException {
        CharTable ct = new CharTable();
        ct.addTitle("TABLE_CAT");

        DatabaseMetaData metaData;
        ResultSet resultSet = null;
        JdbcDao dao = ScriptDataSource.get(context).getDao();
        try {
            metaData = dao.getConnection().getMetaData();
            resultSet = metaData.getCatalogs();
            while (resultSet.next()) {
                ct.addCell(resultSet.getString("TABLE_CAT"));
            }

            if (print) {
                stdout.println(ct.toString(CharTable.Style.DB2));
            }
        } finally {
            IO.closeQuietly(resultSet);
        }
    }

    private void printSchemaInfo(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, boolean print) throws SQLException {
        CharTable ct = new CharTable();
        ct.addTitle("TABLE_CATALOG");
        ct.addTitle("TABLE_SCHEM");

        DatabaseMetaData metaData;
        ResultSet resultSet = null;
        JdbcDao dao = ScriptDataSource.get(context).getDao();
        try {
            metaData = dao.getConnection().getMetaData();
            resultSet = metaData.getSchemas();
            while (resultSet.next()) {
                ct.addCell(resultSet.getString("TABLE_CATALOG"));
                ct.addCell(resultSet.getString("TABLE_SCHEM"));
            }

            if (print) {
                stdout.println(ct.toString(CharTable.Style.DB2));
            }
        } finally {
            IO.closeQuietly(resultSet);
        }
    }
}
