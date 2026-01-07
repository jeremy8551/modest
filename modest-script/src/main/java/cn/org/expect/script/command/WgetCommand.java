package cn.org.expect.script.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

/**
 * 从网络下载文件
 */
public class WgetCommand extends AbstractFileCommand implements UniversalScriptInputStream, NohupCommandSupported {

    /** 文件下载链接 */
    private String url;

    /** 下载文件的存储目录 */
    private String target;

    /** 文件存储的名字 */
    private String filename;

    /** 打印下载文件名 */
    private boolean echoName;

    public WgetCommand(UniversalCommandCompiler compiler, String command, String url, String target, String filename, boolean echoName) {
        super(compiler, command);
        this.url = url;
        this.target = target;
        this.filename = filename;
        this.echoName = echoName;
    }

    public void read(final UniversalScriptSession session, final UniversalScriptContext context, final UniversalScriptParser parser, final UniversalScriptAnalysis analysis, final Reader in) throws Exception {
        if (StringUtils.isNotBlank(this.url)) {
            throw new UniversalScriptException("script.stderr.message012", this.command, "wget", this.url);
        }

        this.url = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        boolean print = session.isEchoEnable() || forceStdout;
        if (print) {
            stdout.println(analysis.replaceShellVariable(session, context, this.getScript(), true, true));
        }

        String target = analysis.replaceShellVariable(session, context, analysis.unQuotation(this.target), true, !analysis.containsQuotation(this.target));
        String filename = analysis.replaceShellVariable(session, context, analysis.unQuotation(this.filename), true, !analysis.containsQuotation(this.filename));
        String url = analysis.replaceShellVariable(session, context, analysis.unQuotation(this.url), true, !analysis.containsQuotation(this.url));
        String str = this.download(url, StringUtils.coalesce(target, session.getDirectory().getAbsolutePath()), filename);
        session.setValue(str);

        if (print) {
            stdout.println(str);
        }
        return 0;
    }

    public String download(String fileURL, String dir, String fileName) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        String name = this.parseFilename(conn, fileURL, fileName);
        if (this.echoName) {
            conn.disconnect();
            return name;
        } else {
            File file = new File(FileUtils.replaceFolderSeparator(FileUtils.joinPath(dir, name)));
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                IO.write(in, new FileOutputStream(file), this);
            }

            conn.disconnect();
            return file.getAbsolutePath();
        }
    }

    private String parseFilename(HttpURLConnection conn, String fileURL, String name) {
        if (StringUtils.isBlank(name)) {
            String disposition = conn.getHeaderField("Content-Disposition");
            if (StringUtils.isNotBlank(disposition)) {
                Matcher matcher = Pattern.compile("filename=\"?([^\";]+)\"?").matcher(disposition);
                if (matcher.find()) {
                    name = matcher.group(1);
                }
            }

            if (StringUtils.isBlank(name)) {
                name = FileUtils.getFilename(fileURL);
            }

            if (StringUtils.isBlank(name)) {
                name = "download";
            }
        }

        return name;
    }

    public boolean enableNohup() {
        return true;
    }
}
