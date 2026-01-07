package cn.org.expect.maven.plugin.format;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.maven.plugin.MavenPluginLog;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

public class Format {

    private final MavenPluginLog log;

    private final boolean verbose;

    public Format(MavenPluginLog log, boolean verbose) {
        this.log = log;
        this.verbose = verbose;
    }

    /**
     * 处理指定目录下的Java源代码文件
     *
     * @param dir         目录
     * @param charsetName 源代码文件的字符集
     * @throws IOException 处理文件发生错误
     */
    public void execute(File dir, String charsetName) throws IOException {
        log.info("Format Java Source file: " + dir);
        if (dir.isDirectory()) {
            this.formatDir(dir, charsetName);
        } else {
            this.formatFile(dir, charsetName);
        }
    }

    private void formatDir(File dir, String charsetName) throws IOException {
        File[] files = FileUtils.array(dir.listFiles());
        for (File file : files) {
            if (file.isDirectory()) {
                this.formatDir(file, charsetName);
            } else if (file.isFile() && FileUtils.getFilenameExt(file.getAbsolutePath()).equalsIgnoreCase("java")) {
                this.formatFile(file, charsetName);
            }
        }
    }

    private void formatFile(File file, String charsetName) throws IOException {
        if (this.verbose) {
            log.info("Format Java Source file://" + file.getAbsolutePath());
        }

        String javaSource = FileUtils.readline(file, charsetName, 0);
        List<String> lines = this.readFile(javaSource, charsetName);
        this.removeEndOfLine(lines);
        this.removeBlankLineBeforeBrace(lines);
        this.removeLastBlankLine(lines);
        String str = this.toJavaSource(lines);
        FileUtils.write(file, charsetName, false, str);
    }

    /**
     * 删除 } 上一个空行
     *
     * @param lines Java源文件记录集合
     */
    protected void removeBlankLineBeforeBrace(List<String> lines) {
        List<Integer> lineNos = new ArrayList<Integer>();
        for (int i = 0; i < lines.size(); i++) {
            if (StringUtils.isBlank(lines.get(i))) {
                int next = i + 1;
                if (next < lines.size() && "}".equals(StringUtils.trimBlank(lines.get(next)))) {
                    lineNos.add(i);
                    i = next;
                }
            }
        }

        Collections.sort(lineNos, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o2.intValue() - o1.intValue();
            }
        });

        for (Integer lineNumber : lineNos) {
            lines.remove(lineNumber.intValue());
        }
    }

    /**
     * 删除 Java 文件结尾字符 } 与上一行之间的空白行
     *
     * @param lines Java源文件记录集合
     */
    protected void removeLastBlankLine(List<String> lines) {
        List<Integer> lineNos = new ArrayList<Integer>();
        for (int i = lines.size() - 2; i >= 0; i--) {
            if (StringUtils.isBlank(lines.get(i))) {
                lineNos.add(i);
            } else {
                break;
            }
        }

        Collections.sort(lineNos, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o2.intValue() - o1.intValue();
            }
        });

        for (Integer lineNumber : lineNos) {
            lines.remove(lineNumber.intValue());
        }
    }

    /**
     * 删除文件结尾的空行
     *
     * @param lines Java源文件记录集合
     */
    protected void removeEndOfLine(List<String> lines) {
        List<Integer> lineNos = new ArrayList<Integer>();
        for (int i = lines.size() - 1; i >= 0; i--) {
            if (StringUtils.isBlank(lines.get(i))) {
                lineNos.add(i);
            } else {
                break;
            }
        }

        for (Integer lineNumber : lineNos) {
            lines.remove(lineNumber.intValue());
        }
    }

    protected List<String> readFile(String javaSource, String charsetName) throws IOException {
        List<String> lines = new ArrayList<String>();
        BufferedReader in = IO.getBufferedReader(new InputStreamReader(new ByteArrayInputStream(javaSource.getBytes(charsetName))));
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    lines.add(StringUtils.rtrimBlank(line));
                } else {
                    lines.add("");
                }
            }
            return lines;
        } finally {
            IO.close(in);
        }
    }

    protected String toJavaSource(List<String> lines) {
        StringBuilder buf = new StringBuilder();
        for (String line : lines) {
            buf.append(line);
            buf.append(FileUtils.LINE_SEPARATOR_UNIX);
        }
        return buf.toString();
    }
}
