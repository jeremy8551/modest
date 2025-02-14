package cn.org.expect.script.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import cn.org.expect.database.Jdbc;
import cn.org.expect.expression.WordIterator;
import cn.org.expect.os.OSConnectCommand;
import cn.org.expect.os.OSShellCommand;
import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.StringUtils;

@EasyCommandCompiler(name = "declare", keywords = {"declare", "global", "catalog"})
public class DeclareCatalogCommandCompiler extends AbstractGlobalCommandCompiler {

    public final static String file = "file";

    public final static String REGEX = "^(?i)\\s*(declare)\\s+([global\\s+]*)(\\S+)\\s+catalog\\s+configuration\\s+[use]*\\s+(.*)";

    private Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL | Pattern.MULTILINE);

    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String script) {
        return pattern.matcher(script).find() ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws IOException {
        WordIterator it = analysis.parse(command);
        it.assertNext("declare");
        boolean global = it.isNext("global");
        if (global) {
            it.assertNext("global");
        }
        String name = it.next();
        it.assertNext("catalog");
        it.assertNext("configuration");
        it.assertNext("use");

        Properties config = new Properties();
        String[] keys = {DeclareCatalogCommandCompiler.file, Jdbc.DRIVER_CLASS_NAME, Jdbc.URL, OSConnectCommand.USERNAME, OSConnectCommand.PASSWORD, Jdbc.ADMIN_USERNAME, Jdbc.ADMIN_PASSWORD, OSShellCommand.SSH_PORT};
        String part = it.readOther();
        List<String> list = new ArrayList<String>();
        analysis.split(part, list);
        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);

            if (key.equalsIgnoreCase(OSConnectCommand.HOST)) {
                key = OSConnectCommand.HOST;
            } else if (key.equalsIgnoreCase(Jdbc.driver)) {
                key = Jdbc.DRIVER_CLASS_NAME;
            } else if (key.equalsIgnoreCase(Jdbc.URL)) {
                key = Jdbc.URL;
            } else if (key.equalsIgnoreCase(OSConnectCommand.USERNAME)) {
                key = OSConnectCommand.USERNAME;
            } else if (key.equalsIgnoreCase(OSConnectCommand.PASSWORD)) {
                key = OSConnectCommand.PASSWORD;
            } else if (key.equalsIgnoreCase(Jdbc.ADMIN_USERNAME)) {
                key = Jdbc.ADMIN_USERNAME;
            } else if (key.equalsIgnoreCase(Jdbc.ADMIN_PASSWORD)) {
                key = Jdbc.ADMIN_PASSWORD;
            } else if (key.equalsIgnoreCase(DeclareCatalogCommandCompiler.file)) {
                key = DeclareCatalogCommandCompiler.file;
            } else if (key.equalsIgnoreCase(OSShellCommand.SSH_USERNAME)) {
                key = OSShellCommand.SSH_USERNAME;
            } else if (key.equalsIgnoreCase(OSShellCommand.SSH_PASSWORD)) {
                key = OSShellCommand.SSH_PASSWORD;
            } else if (key.equalsIgnoreCase(OSShellCommand.SSH_PORT)) {
                key = OSShellCommand.SSH_PORT;
            } else {
                throw new UniversalScriptException("script.stderr.message094", command, key, StringUtils.join(keys, " || "));
            }

            int next = i + 1;
            if (next < list.size()) {
                String value = list.get(next);
                if (StringUtils.inArrayIgnoreCase(value, keys)) {
                    config.setProperty(key, "");
                } else {
                    config.setProperty(key, analysis.unQuotation(value));
                    i++;
                }
            } else {
                config.setProperty(key, "");
            }
        }

        return new DeclareCatalogCommand(this, command, name, config, global);
    }
}
