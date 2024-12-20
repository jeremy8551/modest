package cn.org.expect.maven.repository.gradle;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.impl.SimpleArtifact;
import cn.org.expect.maven.repository.impl.SimpleArtifactSearchResult;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.repository.ArtifactSearchResultType;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 */
public class GradlePluginRepositoryAnalysis {
    protected final static Log log = LogFactory.getLog(GradlePluginRepositoryAnalysis.class);

    public final static String TYPE = "GradlePlugin";

    public GradlePluginRepositoryAnalysis() {
    }

    public ArtifactSearchResult parsePatternResult(String responseBody, int start) {
        if (StringUtils.isBlank(responseBody)) {
            return null;
        }

        int begin = responseBody.indexOf("<table id=\"search-results\"");
        String str = "</table>";
        int end = -1;
        if (begin != -1) {
            end = responseBody.indexOf(str, begin);
        }
        if (begin == -1 || end == -1) {
            return null;
        }

        List<Artifact> list = new ArrayList<>(10);
        String tableHtml = responseBody.substring(begin, end + str.length());
        Document root = XMLUtils.getXmlDocument(new ByteArrayInputStream(StringUtils.toBytes(tableHtml, CharsetName.UTF_8)));
        NodeList nodes = root.getChildNodes();
        NodeList tableList = nodes.item(this.find(nodes, 0, "table")).getChildNodes();
        NodeList tBody = tableList.item(this.find(tableList, 0, "tbody")).getChildNodes();
        for (int i = 0; i < tBody.getLength(); i++) {
            Node trNode = tBody.item(i);
            if ("tr".equalsIgnoreCase(trNode.getNodeName())) {
                NodeList tdList = trNode.getChildNodes();
                int index = this.find(tdList, 0, "td");
                NodeList td1List = tdList.item(index).getChildNodes();

                Node emNode = this.get(td1List, 0, "em");
                if (emNode != null) {
                    break; // <em>No plugins found.</em>
                }

                NodeList h3List = td1List.item(this.find(td1List, 0, "h3")).getChildNodes();
                String pluginID = StringUtils.trimBlank(h3List.item(this.find(h3List, 0, "a")).getTextContent());

                index = this.find(tdList, index + 1, "td");
                NodeList td2List = tdList.item(index).getChildNodes();
                index = this.find(td2List, 0, "span");
                String version = StringUtils.trimBlank(td2List.item(index).getTextContent());
                String date = StringUtils.trimBlank(td2List.item(this.find(td2List, index + 1, "span")).getTextContent(), '(', ')');

                list.add(new SimpleArtifact("", pluginID, version, GradlePluginRepositoryAnalysis.TYPE, this.parseDate(date), -1));
            }
        }

        return new SimpleArtifactSearchResult(GradlePluginRepository.class.getName(), ArtifactSearchResultType.NO_TOTAL, list, start + 1, list.size(), System.currentTimeMillis(), true);
    }

    public List<SimpleArtifact> parseExtraResult(String groupId, String artifactId, String responseBody) {
        int begin = responseBody.indexOf("<body>");
        if (begin == -1) {
            return new ArrayList<>(0);
        }

        String str = "</body>";
        int end = responseBody.indexOf(str, begin);
        if (end == -1) {
            if (log.isErrorEnabled()) {
                log.error("{} responseBody: {}", this.getClass().getSimpleName(), responseBody);
            }
            return null;
        }

        List<SimpleArtifact> list = new ArrayList<>(10);
        String tableHtml = responseBody.substring(begin, end + str.length());
        Document root = XMLUtils.getXmlDocument(new ByteArrayInputStream(StringUtils.toBytes(tableHtml, CharsetName.UTF_8)));
        NodeList nodeList = root.getChildNodes();
        NodeList nodes = nodeList.item(this.find(nodeList, 0, "body")).getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if ("pre".equalsIgnoreCase(node.getNodeName())) {
                NodeList aList = node.getChildNodes();
                int index = this.find(aList, 0, "a");
                String version = StringUtils.trimBlank(aList.item(index).getTextContent());
                if (!version.startsWith("maven-metadata.xml")) {
                    version = StringUtils.rtrim(version, '/');
                    list.add(new SimpleArtifact(groupId, artifactId, version, GradlePluginRepositoryAnalysis.TYPE, null, -1));
                }
            }
        }
        return list;
    }

    public int find(NodeList nodes, int start, String nodeName) {
        for (int i = start; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (nodeName.equalsIgnoreCase(node.getNodeName())) {
                return i;
            }
        }
        throw new UnsupportedOperationException(nodeName);
    }

    public Node get(NodeList nodes, int start, String nodeName) {
        for (int i = start; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (nodeName.equalsIgnoreCase(node.getNodeName())) {
                return node;
            }
        }
        return null;
    }

    public Date parseDate(String str) {
        try {
            return Dates.parse(str);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
            return null;
        }
    }
}
