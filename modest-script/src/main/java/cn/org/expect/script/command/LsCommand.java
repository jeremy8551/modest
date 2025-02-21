package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.os.OSFile;
import cn.org.expect.os.OSFtpCommand;
import cn.org.expect.os.linux.LinuxLocalOS;
import cn.org.expect.os.linux.Linuxs;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptInputStream;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.internal.FtpList;
import cn.org.expect.script.io.ScriptFile;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.CharTable;
import cn.org.expect.util.CollectionUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * 显示本地文件信息<br>
 * 显示远程文件信息
 */
public class LsCommand extends AbstractFileCommand implements UniversalScriptInputStream, NohupCommandSupported {

    /** 文件绝对路径 */
    private List<String> filepathList;

    /** true表示本地 false表示远程服务器 */
    private final boolean localhost;

    public LsCommand(UniversalCommandCompiler compiler, String command, List<String> filepathList, boolean localhost) {
        super(compiler, command);
        this.filepathList = filepathList;
        this.localhost = localhost;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (CollectionUtils.isEmpty(this.filepathList)) {
            String expression = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
            this.filepathList = new ArrayList<String>();
            analysis.split(expression, this.filepathList);
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "ls", this.filepathList);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        List<String> filepaths = new ArrayList<String>(this.filepathList.size());
        for (String filepath : this.filepathList) {
            filepaths.add(filepath);
        }

        StringBuilder buf = new StringBuilder("ls " + StringUtils.join(filepaths, " ")).append(Settings.getLineSeparator());

        CharTable table = new CharTable();
        table.addTitle("filename");

        OSFtpCommand ftp = FtpList.get(context).getFTPClient();
        if (this.localhost || ftp == null) {
            if (filepaths.isEmpty()) {
                filepaths = ArrayUtils.asList(session.getDirectory());
            }

            for (String filepath : filepaths) {
                File file = new ScriptFile(session, context, filepath);
                if (LinuxLocalOS.KEY_FILENAMES.contains(file.getName())) {
                    continue;
                }

                if (file.isDirectory()) {
                    File[] files = FileUtils.array(file.listFiles());
                    for (File f : files) {
                        if (LinuxLocalOS.KEY_FILENAMES.contains(file.getName())) {
                            continue;
                        }

                        table.addCell(Linuxs.toLongname(f));
                    }
                } else {
                    table.addCell(Linuxs.toLongname(file));
                }
            }
        } else {
            if (filepaths.isEmpty()) {
                filepaths = ArrayUtils.asList(ftp.pwd());
            }

            for (String path : filepaths) {
                List<OSFile> list = ftp.ls(ScriptFile.replaceFilepath(session, context, path, false));
                for (OSFile file : list) {
                    table.addCell(file.getLongname());
                }
            }
        }

        buf.append(table.toString(CharTable.Style.SIMPLE));

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(buf);
        }
        return 0;
    }

    public boolean enableNohup() {
        return true;
    }
}
