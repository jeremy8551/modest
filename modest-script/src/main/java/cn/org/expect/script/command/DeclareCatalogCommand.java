package cn.org.expect.script.command;

import java.util.Properties;
import java.util.Set;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.util.CollectionUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 建立数据库编目 <br>
 * <br>
 * 使用参数建立数据库编目： declare global name catalog configuration use driver com.ibm.xxx.driver url db2:url://xxxx:xx username login password login; <br>
 * 使用文件建立数据库编目： declare global name catalog configuration use file filepath; <br>
 */
public class DeclareCatalogCommand extends AbstractGlobalCommand {

    /** 数据库编目名 */
    protected String name;

    /** 数据库编目信息，属性名可以是 driver url username password admin adminpw */
    protected Properties catalog;

    public DeclareCatalogCommand(UniversalCommandCompiler compiler, String command, String name, Properties catalog, boolean global) {
        super(compiler, command);
        this.name = name;
        this.catalog = catalog;
        this.setGlobal(global);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String name = analysis.trim(analysis.replaceShellVariable(session, context, this.name, true, false), 0, 0);

        Set<String> keys = CollectionUtils.stringPropertyNames(this.catalog);
        for (String key : keys) {
            String value = analysis.unQuotation(analysis.replaceShellVariable(session, context, this.catalog.getProperty(key), true, true));
            this.catalog.setProperty(key, value);
        }

        boolean print = session.isEchoEnable() || forceStdout;
        if (this.catalog.containsKey(DeclareCatalogCommandCompiler.file)) {
            String filepath = FileUtils.replaceFolderSeparator(this.catalog.getProperty(DeclareCatalogCommandCompiler.file));
            if (!FileUtils.exists(filepath)) {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message035", this.command, filepath));
                return UniversalScriptCommand.COMMAND_ERROR;
            }

            if (this.isGlobal()) {
                Object old = context.addGlobalCatalog(name, filepath);
                if (old != null && print) {
                    stdout.println(ResourcesUtils.getMessage("script.stdout.message032", filepath, name, StringUtils.toString(old)));
                }
            } else {
                Properties old = context.addLocalCatalog(name, filepath);
                if (old != null && print) {
                    stdout.println(ResourcesUtils.getMessage("script.stdout.message032", filepath, name, StringUtils.toString(old)));
                }
            }
        } else {
            if (this.isGlobal()) {
                Object old = context.addGlobalCatalog(name, this.catalog);
                if (old != null && print) {
                    stdout.println(ResourcesUtils.getMessage("script.stdout.message032", this.catalog, name, StringUtils.toString(old)));
                }
            } else {
                Properties old = context.addLocalCatalog(name, this.catalog);
                if (old != null && print) {
                    stdout.println(ResourcesUtils.getMessage("script.stdout.message032", this.catalog, name, StringUtils.toString(old)));
                }
            }
        }

        return 0;
    }
}
