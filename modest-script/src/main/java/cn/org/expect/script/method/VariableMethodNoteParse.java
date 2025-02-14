package cn.org.expect.script.method;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.internal.MethodNote;
import cn.org.expect.script.method.inernal.ClassMethodCollection;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.JarUtils;
import cn.org.expect.util.StringComparator;
import cn.org.expect.util.StringUtils;

/**
 * 变量方法注释加载器
 */
public class VariableMethodNoteParse {
    private final static Log log = LogFactory.getLog(VariableMethodNoteParse.class);

    /**
     * 返回所有脚本变量方法的使用说明
     *
     * @param repository      变量方法仓库
     * @param sourceDirectory 源代码目录
     * @return 使用说明
     */
    public LinkedHashMap<VariableMethodEntry, MethodNote> load(VariableMethodRepository repository, String sourceDirectory) throws IOException {
        LinkedHashMap<VariableMethodEntry, MethodNote> list = new LinkedHashMap<VariableMethodEntry, MethodNote>();
        Map<Class<?>, ClassMethodCollection> map = repository.values();

        // 方法名的集合
        List<String> names = new ArrayList<String>();
        for (ClassMethodCollection collection : map.values()) {
            names.addAll(collection.getNames());
        }
        Collections.sort(names, new StringComparator());

        for (String name : names) {
            // 变量方法的实现类
            LinkedHashSet<VariableMethodEntry> methods = new LinkedHashSet<VariableMethodEntry>();
            for (ClassMethodCollection collection : map.values()) {
                List<VariableMethodEntry> entryList = collection.get(name);
                methods.addAll(entryList);
            }

            // 同名、不同参数的方法
            for (VariableMethodEntry entry : methods) {
                MethodNote note = new MethodNote();
                Class<?> type = entry.getMethodClass();

                String text = this.readJavaSource(sourceDirectory, type);
                if (text == null) {
                    if (log.isWarnEnabled()) {
                        log.warn("script.stderr.message137", name, type.getName());
                    }
                    continue;
                }

                if (entry.getStaticMethod() == null) {
                    // 实现接口定义的变量方法
                    Pattern pattern = Pattern.compile("/\\*\\*([\\s\\S]*?)\\*/\\s*\\@");
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) {
                        String javadoc = matcher.group(1).trim();
                        this.parseJavadoc(note, javadoc);
                    }
                } else {
                    // 静态方法定义的变量方法
                    Method method = entry.getStaticMethod();
                    this.parseNote(method, text, note);
                }

                if (!note.isIgnore()) {
                    list.put(entry, note);
                }
            }
        }

        return list;
    }

    /**
     * 返回类的源文件内容
     *
     * @param sourceDirectory 源代码目录
     * @param type            类信息
     * @return 类的源文件内容
     * @throws IOException 读取源文件发生错误
     */
    public String readJavaSource(String sourceDirectory, Class<?> type) throws IOException {
        String target = type.getName().replace('.', '/') + ".java";

        File parent;
        File classpath = ClassUtils.getClasspath(type);
        if (FileUtils.isDirectory(classpath)) {
            String project = classpath.getParentFile().getParentFile().getAbsolutePath();
            parent = new File(FileUtils.joinPath(project, "src", "main", "java"));
        } else if (StringUtils.isNotBlank(sourceDirectory)) {
            parent = new File(sourceDirectory);
        } else {
            String jarFilepath = JarUtils.getPath(type);
            File jarFile = JarUtils.toSourceJar(jarFilepath);
            return JarUtils.read(jarFile, target, CharsetUtils.get());
        }

        File source = new File(parent, target);
        if (source.exists()) {
            return FileUtils.readline(source, CharsetUtils.get(), 0);
        }
        throw new UniversalScriptException("script.stderr.message138", source.getAbsolutePath());
    }

    protected void parseNote(Method method, String content, MethodNote note) {
        if (log.isDebugEnabled()) {
            log.debug("script.stdout.message059", method.toGenericString());
        }

        Pattern pattern = Pattern.compile("/\\*\\*([\\s\\S]*?)\\*/\\s*public\\s+static\\s+(\\S+)\\s+(\\w+)\\((.*?)\\)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String javadoc = StringUtils.trimBlank(matcher.group(1)); // 方法注释
            String methodName = StringUtils.trimBlank(matcher.group(3)); // 方法名
            String parameterExpression = StringUtils.trimBlank(matcher.group(4)); // 方法参数表达式

            if (log.isDebugEnabled()) {
                log.debug("script.stdout.message060", methodName, parameterExpression, javadoc);
            }

            // 方法名
            if (!method.getName().equals(methodName)) {
                if (log.isDebugEnabled()) {
                    log.debug("script.stdout.message061");
                }
                continue;
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            ArrayList<String> paramList = new ArrayList<String>();
            StringUtils.split(parameterExpression, ',', paramList);

            // 参数个数
            if (parameterTypes.length != paramList.size()) {
                if (log.isDebugEnabled()) {
                    log.debug("script.stdout.message062", parameterTypes.length, paramList.size());
                }
                continue;
            }

            // 参数类型
            boolean match = true;
            for (int i = 0; i < paramList.size(); i++) {
                String str = paramList.get(i);
                int index = StringUtils.lastIndexOfBlank(str, str.length() - 1);
                if (index == -1) {
                    throw new IllegalArgumentException(str);
                }

                Class<?> parameterType = parameterTypes[i];
                String paramType = StringUtils.trimBlank(str.substring(0, index));
                if (paramType.startsWith(parameterType.getSimpleName() + "<")) {
                    continue;
                }

                if (!parameterType.getSimpleName().equals(paramType)) {
                    if (log.isDebugEnabled()) {
                        log.debug("script.stdout.message063", parameterType.getSimpleName(), paramType);
                    }
                    match = false;
                    break;
                }
            }

            // 解析注释
            if (match) {
                if (log.isDebugEnabled()) {
                    log.debug("script.stdout.message064", method.toGenericString());
                }

                this.parseJavadoc(note, javadoc);
                return;
            }
        }
    }

    /**
     * 解析 Javadoc 注释
     *
     * @param note    方法的注释
     * @param javadoc 注释
     */
    protected void parseJavadoc(MethodNote note, String javadoc) {
        // 按行解析 JavaDoc
        StringBuilder buf = new StringBuilder();
        String[] lines = StringUtils.replaceAll(javadoc, "<br>", "\n").split("\n");

        int paramCount = 0;
        for (String line : lines) {
            line = StringUtils.ltrimBlank(line, '*'); // 去掉行首的 '*' 号
            line = StringUtils.trimBlank(line);

            // 解析 @param
            if (line.startsWith("@param")) {
                String[] array = line.split("\\s+", 3);
                if (array.length >= 2) {
                    MethodNote.Property property = new MethodNote.Property(array[1], array.length >= 3 ? array[2] : "");
                    if (paramCount == 0) {
                        note.setVariable(property);
                    } else {
                        note.getParameterList().add(property);
                    }
                    paramCount++;
                }
                continue;
            }

            // 解析 @return
            if (line.startsWith("@return")) {
                note.setReturn(StringUtils.trimBlank(line.substring(7)));
                continue;
            }

            // 忽略方法
            if (line.startsWith("@Ignore")) {
                note.setIgnore(true);
                continue;
            }

            // 其他注解
            if (line.startsWith("@")) {
                continue;
            }

            buf.append(line);
        }

        note.setText(buf.toString());
    }
}
