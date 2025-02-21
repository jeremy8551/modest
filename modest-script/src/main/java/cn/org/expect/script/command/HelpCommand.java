package cn.org.expect.script.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import cn.org.expect.ProjectPom;
import cn.org.expect.database.DatabaseDialect;
import cn.org.expect.database.Jdbc;
import cn.org.expect.database.JdbcObjectConverter;
import cn.org.expect.database.db2.DB2Dialect;
import cn.org.expect.database.export.ExtractUserListener;
import cn.org.expect.database.export.ExtractWriter;
import cn.org.expect.database.internal.AbstractDialect;
import cn.org.expect.database.internal.DatabaseDialectFactory;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyBeanFactory;
import cn.org.expect.ioc.EasyClassScan;
import cn.org.expect.ioc.EasyClassScanner;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.ioc.internal.BeanRepository;
import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.log.LogFactory;
import cn.org.expect.message.ResourceMessageInternalBundle;
import cn.org.expect.os.OSConnectCommand;
import cn.org.expect.os.OSShellCommand;
import cn.org.expect.os.linux.Linuxs;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptCompiler;
import cn.org.expect.script.UniversalScriptConfiguration;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.script.UniversalScriptFormatter;
import cn.org.expect.script.UniversalScriptInputStream;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.annotation.EasyVariableExtension;
import cn.org.expect.script.annotation.EasyVariableMethod;
import cn.org.expect.script.command.feature.CallbackCommandSupported;
import cn.org.expect.script.command.feature.JumpCommandSupported;
import cn.org.expect.script.command.feature.LoopCommandKind;
import cn.org.expect.script.command.feature.LoopCommandSupported;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.command.feature.WithBodyCommandSupported;
import cn.org.expect.script.compiler.ScriptAnalysis;
import cn.org.expect.script.internal.MethodNote;
import cn.org.expect.script.method.VariableMethodEntry;
import cn.org.expect.script.method.VariableMethodNoteParse;
import cn.org.expect.script.method.VariableMethodRepository;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.CharTable;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.CollectionUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StackTraceUtils;
import cn.org.expect.util.StringUtils;

/**
 * 打印脚本引擎的使用说明
 */
public class HelpCommand extends AbstractTraceCommand implements NohupCommandSupported {

    public HelpCommand(UniversalCommandCompiler compiler, String command) {
        super(compiler, command);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        String charsetName = context.getCharsetName();

        Object[] args = { //
            ProjectPom.getGroupID() // 0 groupId
            , ProjectPom.getArtifactID() // 1 artifactId
            , ProjectPom.getVersion() // 2 version
            , ArrayUtils.first(StringUtils.split(ProjectPom.getArtifactID(), '-')) // 3 springboot 场景启动器
            , UniversalScriptVariable.SESSION_VARNAME_PWD // 4
            , UniversalScriptVariable.SESSION_VARNAME_SCRIPTNAME // 5
            , UniversalScriptVariable.VARNAME_CHARSET // 6
            , CharsetUtils.get() // 7
            , UniversalScriptVariable.SESSION_VARNAME_LINESEPARATOR // 8
            , StringUtils.escapeLineSeparator(Settings.getLineSeparator()) // 9
            , UniversalScriptVariable.VARNAME_EXCEPTION // 10
            , UniversalScriptVariable.VARNAME_ERRORSCRIPT // 11
            , UniversalScriptVariable.VARNAME_ERRORCODE // 12
            , UniversalScriptVariable.VARNAME_SQLSTATE // 13
            , UniversalScriptVariable.VARNAME_EXITCODE // 14
            , UniversalScriptVariable.VARNAME_UPDATEROWS // 15
            , UniversalScriptVariable.SESSION_VARNAME_JUMP // 16
            , UniversalScriptVariable.SESSION_VARNAME_STEP // 17
            , UniversalScriptVariable.SESSION_VARNAME_TEMP // 18
            , UniversalScriptVariable.SESSION_VARNAME_SCRIPTFILE // 19
            , UniversalScriptVariable.VARNAME_CATALOG // 20
            , UniversalScriptVariable.SESSION_VARNAME_THIS // 21
            , EasyBean.class.getSimpleName() // 22
            , TextTableFile.class.getSimpleName() // 23
            , ExtractWriter.class.getSimpleName() // 24
            , AbstractDialect.class.getName() // 25 基础数据库方言类
            , DatabaseDialect.class.getName() // 26 数据库方言类
            , this.supportedDatabase(context) // 27 所有数据库方言类
            , EasyContext.class.getName()  // 28
            , EasyBean.class.getName() // 29
            , DatabaseDialect.class.getSimpleName() // 30
            , DatabaseDialectFactory.class.getName() // 31
            , EasyContext.class.getSimpleName() // 32
            , DB2Dialect.class.getSimpleName() // 33
            , Arrays.toString(context.getContainer().getScanPackages()) // 34
            , FileUtils.getTempDir(false).getAbsolutePath() // 35
            , BeanRepository.class.getName() // 36
            , EasyBeanFactory.class.getName() // 37
            , EasyBeanEntry.class.getName() // 38
            , ClassUtils.toMethodName(EasyContext.class, "getBean", Class.class, Object[].class) // 39
            , CollectionUtils.first(JavaDialectFactory.DIALECT_CLASS_VERSION_LIST) // 40
            , UniversalScriptVariable.SESSION_VARNAME_HOME // 41
            , DefaultEasyContext.class.getName() // 42
            , FileUtils.class.getSimpleName() // 43
            , ClassUtils.toMethodName(FileUtils.class, "getTempDir", String[].class) // 44
            , "" // 45
            , "" // 46
            , "" // 47
            , "" // 48
            , "" // 49
            , EasyCommandCompiler.class.getName() // 50
            , EasyCommandCompiler.class.getSimpleName() // 51
            , EasyVariableMethod.class.getName() // 52
            , EasyVariableMethod.class.getSimpleName() // 53
            , UniversalScriptVariableMethod.class.getName() // 54
            , this.readCommandUsage(session.getAnalysis(), context) // 55
            , this.readVariableMethodUsage(session, context) // 56
            , ClassUtils.toMethodName(EasyVariableMethod.class, "name") // 57
            , EasyVariableExtension.class.getName() // 58
            , EasyVariableExtension.class.getSimpleName() // 59
            , "" // 60
            , "" // 61
            , "" // 62
            , "" // 63
            , "" // 64
            , "" // 65
            , "" // 66
            , "" // 67
            , "" // 68
            , "" // 69
            , EasyClassScanner.PROPERTY_SCAN_PKG // 70
            , LogFactory.PROPERTY_LOGGER // 71
            , LogFactory.PROPERTY_LOG_SOUT // 72
            , CharsetUtils.PROPERTY_CHARSET // 73
            , IO.PROPERTY_BYTE_ARRAY_LENGTH // 74
            , ResourcesUtils.PROPERTY_RESOURCE // 75
            , IO.PROPERTY_CHAR_ARRAY_LENGTH // 76
            , FileUtils.PROPERTY_TEMP_DIR // 77
            , StackTraceUtils.PROPERTY_LOG_STACKTRACE // 78
            , Jdbc.PROPERTY_DATABASE_LOG // 79
            , Linuxs.PROPERTY_LINUX_BUILTIN_ACCT // 80
            , LogFactory.SOUT_PLUS_PATTERN // 81
            , ResourcesUtils.PROPERTY_RESOURCE_NAME // 82
            , ResourcesUtils.PROPERTY_RESOURCE_LOCALE // 83
            , ResourceMessageInternalBundle.RESOURCE_NAME // 84
            , "" // 85
            , "" // 86
            , "" // 87
            , "" // 88
            , ResourcesUtils.class.getPackage().getName() // 89
            , UniversalScriptEngineFactory.class.getName() // 90
            , UniversalScriptEngine.class.getName() // 91
            , UniversalScriptContext.class.getName() // 92
            , UniversalCommandCompiler.class.getName() // 93
            , UniversalScriptParser.class.getName() // 94
            , UniversalScriptReader.class.getName() // 95
            , UniversalScriptAnalysis.class.getName() // 96
            , UniversalScriptFormatter.class.getName() // 97
            , UniversalScriptConfiguration.class.getName() // 98
            , UniversalScriptCommand.class.getName() // 99
            , UniversalScriptVariableMethod.class.getName() // 100
            , ResourcesUtils.class.getName() // 101
            , EasyClassScan.class.getName() // 102
            , ClassUtils.toMethodName(UniversalCommandCompiler.class, "read", UniversalScriptReader.class, UniversalScriptAnalysis.class) // 103
            , ClassUtils.toMethodName(UniversalCommandCompiler.class, "compile", UniversalScriptSession.class, UniversalScriptContext.class, UniversalScriptParser.class, UniversalScriptAnalysis.class, String.class) // 104
            , ClassUtils.toMethodName(UniversalScriptCommand.class, "execute", UniversalScriptSession.class, UniversalScriptContext.class, UniversalScriptStdout.class, UniversalScriptStderr.class, Boolean.class) // 105
            , ClassUtils.toMethodName(UniversalCommandCompiler.class, "match", String.class, String.class) // 106
            , EasyClassScanner.class.getName() // 107
            , EasyClassScan.class.getSimpleName() // 108
            , UniversalScriptCompiler.class.getName() // 109
            , AbstractTraceCommandCompiler.class.getName() // 110
            , AbstractCommandCompiler.class.getName() // 111
            , AbstractFileCommandCompiler.class.getName() // 112
            , AbstractGlobalCommandCompiler.class.getName() // 113
            , AbstractSlaveCommandCompiler.class.getName() // 114
            , UniversalScriptInputStream.class.getName() // 115
            , LoopCommandKind.class.getName() // 116
            , NohupCommandSupported.class.getName() // 117
            , CallbackCommandSupported.class.getName() // 118
            , LoopCommandSupported.class.getName() // 119
            , LoopCommandKind.class.getName() // 120
            , JumpCommandSupported.class.getName() // 121
            , WithBodyCommandSupported.class.getName() // 122
            , "" // 123
            , "" // 124
            , "" // 125
            , "" // 126
            , "" // 127
            , "" // 128
            , "" // 129
            , "" // 130
            , "" // 131
            , "" // 132
            , "" // 133
            , "" // 134
            , "" // 135
            , "" // 136
            , "" // 137
            , "" // 138
            , "" // 139
            , "" // 140
            , "" // 141
            , "" // 142
            , "" // 143
            , "" // 144
            , "" // 145
            , "" // 146
            , "" // 147
            , "" // 148
            , "" // 149
            , this.toAllImplements(context) // 150
        };

        // 返回命令的使用说明文件
        InputStream in = UniversalScriptEngine.class.getResourceAsStream("Help.md");
        String readMe = new String(IO.read(in), charsetName);
        String markdownStr = StringUtils.replaceIndexHolder(readMe, args);
        stdout.println(markdownStr);
        return 0;
    }

    public String readCommandUsage(UniversalScriptAnalysis analysis, UniversalScriptContext context) throws IOException {
        String prefix = StringUtils.left("", 2, '#');
        StringBuilder buf = new StringBuilder(500);
        BufferedReader in = IO.getBufferedReader(new InputStreamReader(UniversalScriptEngine.class.getResourceAsStream("HelpCommand.md"), CharsetName.UTF_8));
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("#")) {
                    buf.append(prefix).append(line).append(Settings.getLineSeparator());
                } else {
                    buf.append(line).append(Settings.getLineSeparator());
                }
            }

            Object[] args = {
                DebugCommand.class.getName(), // 0
                analysis.getToken(), // 1
                UniversalScriptVariable.VARNAME_UPDATEROWS, // 2
                ScriptAnalysis.ESCAPE_STRING, // 3
                StringUtils.left(OSConnectCommand.HOST, 17, ' '), // 4
                StringUtils.left(Jdbc.driver, 17, ' '), // 5
                StringUtils.left(Jdbc.URL, 17, ' '), // 6
                StringUtils.left(OSConnectCommand.USERNAME, 17, ' '), // 7
                StringUtils.left(OSConnectCommand.PASSWORD, 17, ' '), // 8
                StringUtils.left(Jdbc.ADMIN_USERNAME, 17, ' '), // 9
                StringUtils.left(Jdbc.ADMIN_PASSWORD, 17, ' '), // 10
                StringUtils.left(DeclareCatalogCommandCompiler.file, 17, ' '), // 11
                StringUtils.left(OSShellCommand.SSH_USERNAME, 17, ' '), // 12
                StringUtils.left(OSShellCommand.SSH_PASSWORD, 17, ' '), // 13
                StringUtils.left(OSShellCommand.SSH_PORT, 17, ' '), // 14
                EasyBean.class.getSimpleName(), // 15
                this.supportedExtractWriter(context), // 16
                this.supportedTextTableFile(context), // 17
                TextTableFile.class.getName(), // 18
                ExtractUserListener.class.getName(), // 19
                JdbcObjectConverter.class.getName(), // 20
                StringUtils.left(UniversalScriptVariable.VARNAME_EXCEPTION, 15, ' '), // 21
                StringUtils.left(UniversalScriptVariable.VARNAME_ERRORCODE, 15, ' '), // 22
                StringUtils.left(UniversalScriptVariable.VARNAME_SQLSTATE, 15, ' '), // 23
                StringUtils.left(UniversalScriptVariable.VARNAME_ERRORSCRIPT, 15, ' '), // 24
                StringUtils.left(UniversalScriptVariable.VARNAME_EXITCODE, 15, ' '), // 25
                CallbackCommandSupported.class.getName(), // 26
                UniversalScriptFormatter.class.getName(), // 27
                AbstractJavaCommand.class.getName(), // 28
                ExtractWriter.class.getName(), // 29
                null, // 3
                null, // 3
            };
            return StringUtils.replaceIndexHolder(buf.toString(), args);
        } finally {
            in.close();
        }
    }

    public String readVariableMethodUsage(UniversalScriptSession session, UniversalScriptContext context) throws IOException {
        StringBuilder buf = new StringBuilder(500);
        VariableMethodRepository repository = session.getCompiler().getRepository().get(VariableMethodCommandCompiler.class).getRepository();
        VariableMethodNoteParse noteRepository = new VariableMethodNoteParse();
        String sourceDirectory = (String) context.getVariable("project.build.sourceDirectory");
        LinkedHashMap<VariableMethodEntry, MethodNote> methodNotes = noteRepository.load(repository, sourceDirectory);

        // 保证自定义变量方法按固定顺序排序
        List<VariableMethodEntry> entryList = new ArrayList<VariableMethodEntry>(methodNotes.keySet());
        Collections.sort(entryList, new Comparator<VariableMethodEntry>() {
            public int compare(VariableMethodEntry o1, VariableMethodEntry o2) {
                int nv = o1.getName().compareTo(o2.getName());
                if (nv != 0) {
                    return nv;
                }

                int vcn = o1.getVariableClass().getName().compareTo(o2.getVariableClass().getName());
                if (vcn != 0) {
                    return vcn;
                }

                Class<?>[] type1 = o1.getParameters();
                Class<?>[] type2 = o2.getParameters();
                int tlv = type1.length - type2.length;
                if (tlv != 0) {
                    return tlv;
                }

                for (int i = 0; i < type1.length; i++) {
                    Class<?> paramType1 = type1[i];
                    Class<?> paramType2 = type2[i];

                    int ptv = paramType1.getName().compareTo(paramType2.getName());
                    if (ptv != 0) {
                        return ptv;
                    }
                }

                return 0;
            }
        });

        for (VariableMethodEntry entry : entryList) {
            MethodNote note = methodNotes.get(entry); // 注释
            if (note.isIgnore()) {
                continue;
            }

            // 方法名
            buf.append(StringUtils.left("", 3, '#')).append(" ").append(entry.toTitle()).append(FileUtils.LINE_SEPARATOR_UNIX);

            // 方法功能
            buf.append(StringUtils.trimBlank(note.getText())).append(FileUtils.LINE_SEPARATOR_UNIX);

            buf.append(FileUtils.LINE_SEPARATOR_UNIX);
            buf.append(ResourcesUtils.getMessage("script.stdout.message066", entry.getVariableClass().getSimpleName()));
            if (note.getVariable() != null && StringUtils.isNotBlank(note.getVariable().getNote())) {
                buf.append(" ");
                buf.append(note.getVariable().getNote());
            }
            buf.append(FileUtils.LINE_SEPARATOR_UNIX);
            buf.append(FileUtils.LINE_SEPARATOR_UNIX);

            // 使用方法
            buf.append(FileUtils.LINE_SEPARATOR_UNIX);
            buf.append(ResourcesUtils.getMessage("script.stdout.message067", entry.toStandardString(note)));

            List<MethodNote.Property> list = note.getParameterList();
            for (int i = 0; i < list.size(); i++) {
                MethodNote.Property parameter = list.get(i);
                buf.append(ResourcesUtils.getMessage("script.stdout.message068", i + 1, parameter.getNote()));
            }

            if (StringUtils.isNotBlank(note.getReturn())) {
                buf.append(FileUtils.LINE_SEPARATOR_UNIX);
                buf.append(ResourcesUtils.getMessage("script.stdout.message069", note.getReturn()));
            }

            // 定义方法的位置
            buf.append(FileUtils.LINE_SEPARATOR_UNIX);
            buf.append(ResourcesUtils.getMessage("script.stdout.message070", entry.getMethodInfo()));
        }
        return buf.toString();
    }

    public String toAllImplements(UniversalScriptContext context) {
        EasyContext ioc = context.getContainer();

        List<Class<?>> types1 = ioc.getBeanClassList();
        Collections.sort(types1, new Comparator<Class<?>>() {
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getSimpleName().compareTo(o2.getSimpleName());
            }
        });
        LinkedHashSet<Class<?>> keys = new LinkedHashSet<Class<?>>(types1);

        // 以下这些接口的实现类已在帮助说明中删除
        keys.remove(UniversalScriptVariableMethod.class);
        keys.remove(UniversalCommandCompiler.class);
        keys.remove(DatabaseDialect.class);

        String[] array = StringUtils.split(ResourcesUtils.getMessage("script.stdout.message065"), ",");
        String colName1 = array[0];
        String colName2 = array[1];

        StringBuilder buf = new StringBuilder();
        for (Class<?> type : keys) {
            if (ioc.getBeanFactory(type) != null) {
                continue;
            }

            List<EasyBeanEntry> list = ioc.getBeanEntryCollection(type).values();
            if (list.isEmpty()) {
                continue;
            }

            // 排序
            Collections.sort(list, new Comparator<EasyBeanEntry>() {
                public int compare(EasyBeanEntry o1, EasyBeanEntry o2) {
                    return o1.getType().getSimpleName().compareTo(o2.getType().getSimpleName());
                }
            });

            CharTable table = new CharTable();
            table.addTitle(colName1);
            table.addTitle(colName2);
            for (EasyBeanEntry entry : list) {
                table.addCell("`" + entry.getType().getName() + "`");
                table.addCell(entry.getDescription());
            }

            buf.append("### ").append(type.getSimpleName());
            buf.append(FileUtils.LINE_SEPARATOR_UNIX);

            buf.append(table.toString(CharTable.Style.MARKDOWN));
            buf.append(FileUtils.LINE_SEPARATOR_UNIX);
            buf.append(FileUtils.LINE_SEPARATOR_UNIX);
            buf.append(FileUtils.LINE_SEPARATOR_UNIX);
        }

        List<Class<?>> types2 = ioc.getBeanFactoryClass();
        Collections.sort(types2, new Comparator<Class<?>>() {
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getSimpleName().compareTo(o2.getSimpleName());
            }
        });

        LinkedHashSet<Class<?>> factorys = new LinkedHashSet<Class<?>>(types2);
        factorys.remove(UniversalScriptVariableMethod.class);
        factorys.remove(UniversalCommandCompiler.class);
        factorys.remove(DatabaseDialect.class);
        for (Class<?> type : factorys) {
            EasyBeanFactory<?> factory = ioc.getBeanFactory(type);
            List<EasyBeanEntry> list = ioc.getBeanEntryCollection(type).values();
            if (list.isEmpty()) {
                continue;
            }

            CharTable ct = new CharTable();
            ct.addTitle(colName1);
            ct.addTitle(colName2);

            for (EasyBeanEntry entry : list) {
                ct.addCell("`" + entry.getType().getName() + "`");
                ct.addCell(entry.getDescription());
            }

            buf.append(ResourcesUtils.getMessage("script.stdout.message071", type.getSimpleName(), factory.getClass().getName())).append(FileUtils.LINE_SEPARATOR_UNIX);
            buf.append(ct.toString(CharTable.Style.MARKDOWN));
            buf.append(FileUtils.LINE_SEPARATOR_UNIX);
            buf.append(FileUtils.LINE_SEPARATOR_UNIX);
            buf.append(FileUtils.LINE_SEPARATOR_UNIX);
        }

        return StringUtils.rtrimBlank(buf);
    }

    /**
     * 查询当前支持的数据库
     *
     * @param context 脚本引擎上下文信息
     * @return 当前支持的数据库
     */
    public String supportedDatabase(UniversalScriptContext context) {
        List<EasyBeanEntry> list = context.getContainer().getBeanEntryCollection(DatabaseDialect.class).values();
        Collections.sort(list, new Comparator<EasyBeanEntry>() {
            public int compare(EasyBeanEntry o1, EasyBeanEntry o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        String[] array = StringUtils.split(ResourcesUtils.getMessage("script.stdout.message001"), ',');
        CharTable table = new CharTable(context.getCharsetName());
        table.addTitle(array[0], CharTable.ALIGN_MIDDLE);
        table.addTitle(array[1], CharTable.ALIGN_LEFT);
        table.addTitle(array[2], CharTable.ALIGN_RIGHT);

        for (EasyBeanEntry entry : list) {
            table.addCell("**" + entry.getName() + "**");
            table.addCell(StringUtils.coalesce(entry.getDescription(), "") + "     ");
            table.addCell("          `" + entry.getType().getName() + "`");
        }
        return table.toString(CharTable.Style.MARKDOWN);
    }

    /**
     * 查询当前支持的数据库
     *
     * @param context 脚本引擎上下文信息
     * @return 当前支持的数据库
     */
    public String supportedExtractWriter(UniversalScriptContext context) {
        List<EasyBeanEntry> list = context.getContainer().getBeanEntryCollection(ExtractWriter.class).values();
        Collections.sort(list, new Comparator<EasyBeanEntry>() {
            public int compare(EasyBeanEntry o1, EasyBeanEntry o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        String[] array = StringUtils.split(ResourcesUtils.getMessage("script.stdout.message055"), ',');
        CharTable table = new CharTable(context.getCharsetName());
        table.addTitle(array[0], CharTable.ALIGN_MIDDLE);
        table.addTitle(array[1], CharTable.ALIGN_LEFT);
        table.addTitle(array[2], CharTable.ALIGN_LEFT);

        for (EasyBeanEntry entry : list) {
            table.addCell("**" + entry.getName() + "**");
            table.addCell("`" + entry.getType().getName() + "`");
            table.addCell(entry.getDescription());
        }
        return table.toString(CharTable.Style.MARKDOWN);
    }

    /**
     * 查询当前支持的数据库
     *
     * @param context 脚本引擎上下文信息
     * @return 当前支持的数据库
     */
    public String supportedTextTableFile(UniversalScriptContext context) {
        List<EasyBeanEntry> list = context.getContainer().getBeanEntryCollection(TextTableFile.class).values();
        Collections.sort(list, new Comparator<EasyBeanEntry>() {
            public int compare(EasyBeanEntry o1, EasyBeanEntry o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        String[] array = StringUtils.split(ResourcesUtils.getMessage("script.stdout.message055"), ',');
        CharTable table = new CharTable(context.getCharsetName());
        table.addTitle(array[0], CharTable.ALIGN_MIDDLE);
        table.addTitle(array[1], CharTable.ALIGN_LEFT);
        table.addTitle(array[2], CharTable.ALIGN_LEFT);

        for (EasyBeanEntry entry : list) {
            table.addCell("**" + entry.getName() + "**");
            table.addCell("`" + entry.getType().getName() + "`");
            table.addCell(entry.getDescription());
        }
        return table.toString(CharTable.Style.MARKDOWN);
    }

    public boolean enableNohup() {
        return true;
    }
}
