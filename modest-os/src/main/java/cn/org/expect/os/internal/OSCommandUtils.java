package cn.org.expect.os.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.org.expect.collection.CaseSensitivMap;
import cn.org.expect.io.BufferedLineReader;
import cn.org.expect.os.OSCommandStdouts;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

public class OSCommandUtils {

    public final static String START_PREFIX = "[*|*|*|*|*|*|*]";

    private OSCommandUtils() {
    }

    /**
     * 将多命令输出解析为一行
     *
     * @param list 命令集合
     * @return 字符串
     */
    public static String join(List<String> list) {
        return list == null ? "" : StringUtils.trimBlank(StringUtils.join(list, ""));
    }

    /**
     * 与 {@link #splitMultiCommandStdout(CharSequence)} 配套使用
     *
     * @param commands 命令集合
     * @return 字符串
     */
    public static String toMultiCommand(List<String> commands) {
        return toMultiCommand(OSCommandUtils.START_PREFIX, commands.toArray(new String[commands.size()]));
    }

    /**
     * 与 {@link #splitMultiCommandStdout(String, CharSequence)} 配套使用
     *
     * @param prefix 前缀
     * @param cmds   命令数组
     * @return 命令
     */
    private static String toMultiCommand(String prefix, CharSequence... cmds) {
        Ensure.isTrue(cmds.length % 2 == 0, StringUtils.toString(cmds));
        Ensure.isTrue(StringUtils.isNotBlank(prefix) && prefix.indexOf(';') == -1 && !prefix.contains("\""), prefix, cmds);

        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < cmds.length; i++) {
            String key = cmds[i].toString();
            String value = cmds[++i].toString();
            Ensure.isTrue(key.indexOf(';') == -1 && !key.contains("\""), key, value);

            buf.append("echo \"");
            buf.append(prefix);
            buf.append(key);
            buf.append("\"");
            buf.append("; ");
            buf.append(StringUtils.trimBlank(value, ';'));
            buf.append("; ");
        }
        return buf.toString();
    }

    /**
     * 解析 ps 命令的输出
     *
     * @param stdout 标准输出信息
     * @param titles 输出信息标题, 为空时自动使用stdout第一行作为标题行解析
     * @return 标准输出信息集合
     */
    public static List<Map<String, String>> splitPSCmdStdout(CharSequence stdout, String... titles) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (StringUtils.isNotBlank(stdout)) {
            BufferedLineReader in = new BufferedLineReader(StringUtils.ltrimBlank(stdout));
            try {
                if (titles == null || titles.length == 0) {
                    if (in.hasNext()) {
                        String line = in.next();
                        String names = StringUtils.trimBlank(line);
                        titles = StringUtils.splitByBlank(names);
                    } else {
                        return list;
                    }
                }

                Ensure.isTrue(StringUtils.indexOf(titles, "pid", 0, titles.length - 1, true) != -1, stdout, titles);
                while (in.hasNext()) {
                    String line = StringUtils.trimBlank(in.next());
                    List<String> array = StringUtils.splitByBlank(line, titles.length);

                    Map<String, String> map = new CaseSensitivMap<String>();
                    for (int i = 0; i < titles.length; i++) {
                        String key = StringUtils.trimBlank(titles[i]);
                        map.put(key, array.get(i));
                    }
                    list.add(map);
                }
            } finally {
                IO.close(in);
            }
        }
        return list;
    }

    /**
     * 分隔多命令输出信息 <br>
     * 与 {@linkplain #toMultiCommand(List)} 配套使用
     *
     * @param stdout 命令信息
     * @return 命令输出信息
     */
    public static OSCommandStdouts splitMultiCommandStdout(CharSequence stdout) {
        return splitMultiCommandStdout(OSCommandUtils.START_PREFIX, stdout);
    }

    /**
     * 分隔多命令输出信息
     *
     * @param prefix 每个命令输出的前缀, 后面是命令结果标志
     * @param stdout 标准输出信息
     * @return 命令输出信息
     */
    private static OSCommandStdouts splitMultiCommandStdout(String prefix, CharSequence stdout) {
        Ensure.notBlank(stdout);

        String key = "";
        OSCommandStdoutsImpl map = new OSCommandStdoutsImpl();
        List<String> list = new ArrayList<String>();
        BufferedLineReader in = new BufferedLineReader(stdout);
        try {
            while (in.hasNext()) {
                String line = in.next();
                if (line.startsWith(prefix)) {
                    if (key.length() == 0) { // for first row
                        key = StringUtils.trimBlank(line.substring(prefix.length()));
                        continue;
                    } else {
                        map.put(Ensure.notBlank(key), new ArrayList<String>(list));
                        list.clear();
                        key = StringUtils.trimBlank(line.substring(prefix.length()));
                        continue;
                    }
                } else {
                    list.add(line);
                }
            }

            map.put(key, new ArrayList<String>(list));
            list.clear();
            return map;
        } finally {
            IO.close(in);
        }
    }
}
