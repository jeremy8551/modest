package cn.org.expect.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 批量修改模块中资源文件中的属性名
 */
public class ModifyResourcePropertyName {

    private final static Map<String, Integer> map1 = new HashMap<String, Integer>();
    private final static Map<File, Properties> map2 = new HashMap<File, Properties>();

    public static void main(String[] args) throws IOException {
        new ModifyResourcePropertyName().load("cn.standard.output.msg", "cn.stdout.message", "/Users/user/Documents/project/modest/modest-cn/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("timer.standard.output.msg", "timer.stdout.message", "/Users/user/Documents/project/modest/modest-concurrent/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("concurrent.job.executor", "concurrent.stdout.message", "/Users/user/Documents/project/modest/modest-concurrent/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("crypto.standard.output.msg", "crypto.stdout.message", "/Users/user/Documents/project/modest/modest-crypto/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("database.standard.output.msg", "database.stdout.message", "/Users/user/Documents/project/modest/modest-database/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("extract.standard.output.msg", "extract.stdout.message", "/Users/user/Documents/project/modest/modest-database/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("load.standard.output.msg", "load.stdout.message", "/Users/user/Documents/project/modest/modest-database/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("script.message.stderr", "engine.stdout.message", "/Users/user/Documents/project/modest/modest-engine/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("param.standard.output", "expression.stdout.message", "/Users/user/Documents/project/modest/modest-expression/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("expression.standard.output.msg", "expression.stdout.message", "/Users/user/Documents/project/modest/modest-expression/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("increment.standard.output.msg", "increment.stdout.message", "/Users/user/Documents/project/modest/modest-increment/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("io.standard.output.msg", "increment.stdout.message", "/Users/user/Documents/project/modest/modest-increment/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("io.standard.output.msg", "io.stdout.message", "/Users/user/Documents/project/modest/modest-io/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("ioc.standard.output.msg", "ioc.stdout.message", "/Users/user/Documents/project/modest/modest-ioc/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("class.standard.output.msg", "ioc.stdout.message", "/Users/user/Documents/project/modest/modest-ioc/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("jdk.standard.output.msg", "jdk.stdout.message", "/Users/user/Documents/project/modest/modest-jdk/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("os.standard.output.msg", "os.stdout.message", "/Users/user/Documents/project/modest/modest-os/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("ssh2.jsch.standard.output.msg", "ssh2.jsch.stdout.message", "/Users/user/Documents/project/modest/modest-os/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("ftp.apache.standard.output.msg", "ftp.apache.stdout.message", "/Users/user/Documents/project/modest/modest-os/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("printer.standard.output.msg", "printer.stdout.message", "/Users/user/Documents/project/modest/modest-printer/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("mail.standard.output.msg", "mail.stdout.message", "/Users/user/Documents/project/modest/modest-script/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("script.engine.usage.msg", "script.stdout.message", "/Users/user/Documents/project/modest/modest-script/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("script.message.stdout", "script.stdout.message", "/Users/user/Documents/project/modest/modest-script/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("script.message.stderr", "script.stderr.message", "/Users/user/Documents/project/modest/modest-script/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("class.standard.output.msg", "class.stdout.message", "/Users/user/Documents/project/modest/modest-tool/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("xml.standard.output.msg", "xml.stdout.message", "/Users/user/Documents/project/modest/modest-tool/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("io.standard.output.msg", "file.stdout.message", "/Users/user/Documents/project/modest/modest-tool/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("file.standard.output.msg", "file.stdout.message", "/Users/user/Documents/project/modest/modest-tool/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("date.standard.output.msg", "date.stdout.message", "/Users/user/Documents/project/modest/modest-tool/src/main/resources/cn/org/expect/util/Messages.properties");
        new ModifyResourcePropertyName().load("commons.standard.output.msg", "tool.stdout.message", "/Users/user/Documents/project/modest/modest-tool/src/main/resources/cn/org/expect/util/Messages.properties");

        Logs.info("");
        Logs.info("");
        Logs.info("");
        for (File file : map2.keySet()) {
            Properties properties = map2.get(file);

            Logs.info("store: " + file.getAbsolutePath());
            FileUtils.deleteFile(file);
            FileUtils.store(properties, file);
        }
    }

    public void load(String oldKeyPrefix, String newKeyPrefix, String filepath) throws IOException {
        File file = new File(filepath);

        Integer i = map1.get(newKeyPrefix);
        if (i == null) {
            i = 0;
        }

        int count = i;

        UniqueSequenceGenerator generator = new UniqueSequenceGenerator("uuid------- {} ----- uuid", 1);
        List<JavaSource> javaSources = new ArrayList<JavaSource>();
        File sourceDir = this.getSourceDir(file);
        this.load(sourceDir, javaSources);
        String name = sourceDir.getParentFile().getName(); // 模块名: modest-tool

        LinkedHashMap<String, Task> map = new LinkedHashMap<String, Task>();

        Properties properties = map2.get(file);
        if (properties == null) {
            properties = new Properties();
            map2.put(file, properties);
        }

        StringBuilder buf = new StringBuilder();
        BufferedReader in = IO.getBufferedReader(file, CharsetName.UTF_8);
        String line;
        while ((line = in.readLine()) != null) {
            if (StringUtils.isNotBlank(line)) {
                String[] array = StringUtils.splitProperty(StringUtils.unescape(line));
                if (array == null) {
                    continue;
                }

                String key = array[0];
                String value = array[1];

                if (StringUtils.isNotBlank(oldKeyPrefix) && !key.startsWith(oldKeyPrefix)) {
                    continue;
                }

                Logs.info("find: " + key);
                Task task = this.find(javaSources, key);
                if (task != null) {
                    task.newKey = newKeyPrefix + StringUtils.right(++count, 3, '0');
                    task.uuid = generator.nextString();
                    map.put(key, task);

                    buf.append(task.newKey).append('=').append(StringUtils.escapeLineSeparator(value)).append('\n');
                    properties.setProperty(task.newKey, value);
                }
            }
        }

        map1.put(newKeyPrefix, count);

        for (String oldKey : map.keySet()) {
            Task task = map.get(oldKey);
            for (JavaSource javaSource : task.list) {
                javaSource.fileContent = StringUtils.replaceAll(javaSource.fileContent, task.oldKey, task.uuid);
            }
        }

        for (String oldKey : map.keySet()) {
            Task task = map.get(oldKey);
            for (JavaSource javaSource : task.list) {
                String newFileContent = StringUtils.replaceAll(javaSource.fileContent, task.uuid, task.newKey);
                javaSource.fileContent = newFileContent;

                FileUtils.write(javaSource.file, CharsetName.UTF_8, false, newFileContent);
            }
        }

        Logs.info(StringUtils.rtrimBlank(buf));

        for (JavaSource javaSource : javaSources) {
            List<String> keys = this.split(javaSource);
            for (String key : keys) {
                if (!properties.containsKey(key)) {
                    Logs.error("key " + key + " not exists!");
                }
            }
        }
    }

    public List<String> split(JavaSource javaSource) {
        List<String> list = new ArrayList<String>();
        int begin = 0, index;
        String str = "ResourcesUtils.getMessage(\"";
        while ((index = javaSource.fileContent.indexOf(str, begin)) != -1) {
            index += str.length();
            int end = javaSource.fileContent.indexOf("\"", index);
            if (end == -1) {
                throw new UnsupportedOperationException(javaSource.file.getAbsolutePath());
            }

            list.add(javaSource.fileContent.substring(index, end));
            begin += end + 2;
        }
        return list;
    }

    protected File getSourceDir(File file) {
        while (!file.getName().equalsIgnoreCase("src")) {
            file = file.getParentFile();
        }
        return file;
    }

    protected Task find(List<JavaSource> javaSourceList, String key) {
        List<JavaSource> list = new ArrayList<JavaSource>();
        for (JavaSource javaSource : javaSourceList) {
            String str = "\"" + key + "\"";
            if (javaSource.fileContent.contains(str)) {
                list.add(javaSource);
            }
        }
        return list.isEmpty() ? null : new Task(key, list);
    }

    protected void load(File dir, List<JavaSource> javaFiles) throws IOException {
        File[] files = FileUtils.array(dir.listFiles());
        for (File file : files) {
            if (file.isDirectory()) {
                this.load(file, javaFiles);
            } else if (file.isFile() && FileUtils.getFilenameExt(file.getAbsolutePath()).equalsIgnoreCase("java")) {
                String javaSource = FileUtils.readline(file, CharsetName.UTF_8, 0);
                javaFiles.add(new JavaSource(file, javaSource));
            }
        }
    }

    private static class JavaSource {
        private File file;
        private String fileContent;

        public JavaSource(File file, String fileContent) {
            this.file = file;
            this.fileContent = fileContent;
        }
    }

    private static class Task {
        private String oldKey;
        private String newKey;
        private String uuid;
        private List<JavaSource> list;

        public Task(String oldKey, List<JavaSource> list) {
            this.oldKey = oldKey;
            this.list = list;
        }
    }
}
