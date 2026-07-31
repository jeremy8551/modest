package cn.org.expect.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.org.expect.Modest;
import cn.org.expect.message.ResourceScanner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Codepage {

    /** 国家法定假日配置文件所在目录 */
    public final static String PROPERTY_CODEPAGE = Settings.getPropertyName("codepage");

    /** 国家法定假日配置文件默认存储目录 */
    public final static File CODEPAGE_CONFIG_DIR = new File(Settings.getProjectHome(), "config");

    /** 默认的国家法定假日配置文件资源名 */
    public final static String RESOURCE_NAME = ClassUtils.getResourceName(Modest.class, "codepages.xml");

    /** codepage 与 charset 的映射关系 */
    private final Map<String, String> map;

    /**
     * 初始化
     */
    public Codepage() {
        this.map = new HashMap<String, String>();
        this.reload();
    }

    /**
     * 查询代码页对应的字符集名
     *
     * @param codepage 代码页编号
     * @return 字符串
     */
    public synchronized String get(int codepage) {
        return this.map.get(String.valueOf(codepage));
    }

    /**
     * 查询代码页对应的字符集名 <br>
     * 根据字符集名查找对应的代码页 <br>
     *
     * @param key 代码页或字符集名
     * @return 字符串
     */
    public synchronized String get(String key) {
        if (StringUtils.isNumber(key)) {
            return this.map.get(key);
        } else {
            Set<Map.Entry<String, String>> entries = this.map.entrySet();
            for (Map.Entry<String, String> next : entries) {
                if (key.equalsIgnoreCase(next.getValue())) {
                    return next.getKey();
                }
            }
            return null;
        }
    }

    /**
     * 返回所有代码页与字符集的映射关系
     *
     * @return 映射关系
     */
    public synchronized Map<String, String> values() {
        return Collections.unmodifiableMap(this.map);
    }

    /**
     * 加载 codepage
     */
    public synchronized void reload() {
        // 加载默认配置文件
        ResourceScanner scanner = new ResourceScanner(ClassUtils.getClassLoader(), RESOURCE_NAME);
        while (scanner.hasNext()) {
            try {
                this.load(scanner.next());
            } catch (Throwable e) {
                Logs.error("load {} error!", RESOURCE_NAME, e);
            }
        }

        // 扫描用户根目录下的配置文件
        this.loadChildFiles(CODEPAGE_CONFIG_DIR);

        // 使用外部配置文件目录
        String filepath = StringUtils.trimBlank(Settings.getProperty(PROPERTY_CODEPAGE));
        if (StringUtils.isNotBlank(filepath)) {
            if (FileUtils.isDirectory(filepath)) {
                this.loadChildFiles(new File(filepath));
            } else {
                Logs.error("-D{}={}, The filepath is illegal!", PROPERTY_CODEPAGE, filepath);
            }
        }
    }

    /**
     * 加载目录中所有以 codepage 开头的 xml 文件
     *
     * @param dir 目录
     */
    protected void loadChildFiles(File dir) {
        if (FileUtils.isDirectory(dir)) {
            File[] files = FileUtils.array(dir.listFiles());
            for (File file : files) {
                if (file.isFile()) {
                    String filename = file.getName();
                    String filenameExt = FileUtils.getFilenameExt(filename);
                    if (filename.startsWith("codepage") && "xml".equalsIgnoreCase(filenameExt)) {
                        try {
                            this.load(new FileInputStream(file));
                        } catch (IOException e) {
                            Logs.error("load {} error!", file.getAbsolutePath(), e);
                        }
                    }
                }
            }
        }
    }

    /**
     * 加载默认的 codepages.xml
     *
     * @param in XML文件输入流
     */
    protected void load(InputStream in) {
        if (in == null) {
            return;
        }

        Document doc = XMLUtils.newDocument(in);
        Node root = XMLUtils.getChildNode(doc, "codepages");
        if (root == null) {
            return;
        }

        List<Node> codeNodes = XMLUtils.getChildNodes(root, "code");
        for (Node node : codeNodes) {
            String key = StringUtils.trimBlank(XMLUtils.getAttribute(node, "key"));
            String value = StringUtils.trimBlank(XMLUtils.getAttribute(node, "value"));
            this.map.put(key, value);
        }
    }
}
