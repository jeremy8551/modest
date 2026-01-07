package cn.org.expect.maven.plugin.pom;

import java.io.File;
import java.io.IOException;

import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class MavenPomFactory {

    /**
     * 创建一个 MavenPom 对象
     *
     * @param packageName Java包信息
     * @return MavenPom 对象
     */
    public static MavenPom newInstance(String packageName) {
        return newInstance(packageName, MavenPom.CLASS_NAME);
    }

    /**
     * 创建一个 MavenPom 对象
     *
     * @param packageName Java包信息
     * @param className   类名
     * @return MavenPom 对象
     */
    public static MavenPom newInstance(String packageName, String className) {
        String fullClassName = StringUtils.trimBlank(packageName, '.') + "." + className;
        return (MavenPom) ClassUtils.newInstance(fullClassName, ClassUtils.getClassLoader());
    }

    /**
     * 读取POM
     *
     * @param pom Maven POM 文件
     * @return MavenPom 对象
     */
    public static MavenPom newInstance(File pom) throws IOException {
        String charsetName = XMLUtils.getEncoding(pom);
        String xml = FileUtils.readline(pom, charsetName, 0);
        Document document = XMLUtils.newDocument(xml, charsetName);
        final Node groupId = XMLUtils.getChildNode(document, "groupId");
        final Node artifactId = XMLUtils.getChildNode(document, "artifactId");
        final Node version = XMLUtils.getChildNode(document, "version");

        return new MavenPom() {
            public String getGroupID() {
                return StringUtils.trimBlank(groupId.getTextContent());
            }

            public String getArtifactID() {
                return StringUtils.trimBlank(artifactId.getTextContent());
            }

            public String getVersion() {
                return StringUtils.trimBlank(version.getTextContent());
            }
        };
    }
}
