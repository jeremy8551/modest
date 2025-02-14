package cn.org.expect.maven.plugin;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * 格式化代码
 *
 * @author jeremy8551@gmail.com
 * @createtime 2025/2/1
 */
@Mojo(name = "format", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class FormatMojo extends AbstractMojo {

    /**
     * 当前插件信息
     */
    @Parameter(defaultValue = "${plugin}", readonly = true)
    private PluginDescriptor plugin;

    /**
     * 会话信息
     */
    @Parameter(defaultValue = "${session}", required = true)
    private MavenSession session;

    /**
     * 项目信息
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * 工程源代码字符集
     */
    @Parameter(defaultValue = "${project.build.sourceEncoding}")
    private String charsetName;

    /**
     * 需要格式化代码的模块名集合
     */
    @Parameter
    private List<String> format;

    /**
     * 输出详细信息
     */
    @Parameter
    private boolean verbose;

    public FormatMojo() {
        this.verbose = false;
    }

    public void execute() throws MojoExecutionException {
        try {
            if (this.format == null || this.format.isEmpty()) {
                this.format(this.project);
            } else {
                List<MavenProject> projects = this.session.getAllProjects();
                String[] array = this.format.toArray(new String[projects.size()]);
                for (MavenProject project : projects) {
                    if (StringUtils.inArrayIgnoreCase(project.getName(), array)) {
                        this.format(project);
                    }
                }
            }
        } catch (Throwable e) {
            String message = this.plugin.getGroupId() + ":" + this.plugin.getArtifactId() + ":" + this.plugin.getVersion();
            throw new MojoExecutionException(message, e);
        }
    }

    private void format(MavenProject project) throws IOException {
        List<String> compileSourceRoots = project.getCompileSourceRoots();
        for (String filepath : compileSourceRoots) {
            getLog().info("Format Java Source file://" + filepath);
            this.load(new File(filepath));
        }

        List<String> testCompileSourceRoots = project.getTestCompileSourceRoots();
        for (String filepath : testCompileSourceRoots) {
            getLog().info("Format Java TestSource file://" + filepath);
            this.load(new File(filepath));
        }
    }

    protected void load(File dir) throws IOException {
        File[] files = FileUtils.array(dir.listFiles());
        for (File file : files) {
            if (file.isDirectory()) {
                this.load(file);
            } else if (file.isFile() && FileUtils.getFilenameExt(file.getAbsolutePath()).equalsIgnoreCase("java")) {
                this.format(file);
            }
        }
    }

    protected void format(File file) throws IOException {
        if (this.verbose) {
            getLog().info("Format Java Source file://" + file.getAbsolutePath());
        }

        String javaSource = FileUtils.readline(file, charsetName, 0);
        List<String> lines = this.readFile(javaSource);
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

    protected List<String> readFile(String javaSource) throws IOException {
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
