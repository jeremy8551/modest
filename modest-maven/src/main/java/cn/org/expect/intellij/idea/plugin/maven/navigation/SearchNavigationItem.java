package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearch;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginIcon;
import cn.org.expect.maven.concurrent.SearchPomJob;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.pom.Pom;
import cn.org.expect.maven.search.SearchNavigation;
import cn.org.expect.util.StringUtils;

public class SearchNavigationItem extends AbstractSearchNavigation {
    private final static Log log = LogFactory.getLog(SearchNavigationItem.class);

    /** 子节点 */
    private final List<SearchNavigationDetail> child;

    public SearchNavigationItem(MavenSearchPlugin plugin, Artifact artifact) {
        super(plugin, artifact);
        this.child = new ArrayList<>();
        this.setDepth(2);
        this.setPresentableText(artifact.getVersion());
        this.setLocationString("");
        this.setLeftIcon(null);
        this.setRightIcon(MavenSearchPluginIcon.RIGHT_REMOTE);
        this.setRightText("");
    }

    public List<? extends SearchNavigation> getNavigationList() {
        return this.child;
    }

    public boolean supportMenu() {
        return true;
    }

    public void displayMenu(SearchNavigation navigation, JPopupMenu topMenu, int selectedIndex) {
        MavenSearchPlugin plugin = this.getSearch();
        plugin.getResultMenu().displayItemMenu(plugin, navigation, topMenu, selectedIndex, 30);
    }

    public boolean supportFold() {
        return true;
    }

    public void setUnfold() {
        this.setFold(false);
        MavenSearchPlugin plugin = this.getSearch();
        plugin.asyncPom(this.getArtifact());
        this.update(plugin, this.getArtifact());
    }

    public void setFold() {
        this.setFold(true);
        this.setLeftIcon(null);
    }

    public void unfold() {
        MavenSearchPlugin plugin = this.getSearch();
        Artifact artifact = this.getArtifact();
        Pom pom = plugin.getPomRepository().select(artifact);
        if (pom != null) {
            if (this.child.isEmpty()) {
                Pom.Parent parent = pom.getParent();
                if (StringUtils.isNotBlank(parent.getGroupId()) && StringUtils.isNotBlank(parent.getArtifactId())) {
                    this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_PARENT, "", parent.getGroupId() + ":" + parent.getArtifactId() + ":" + parent.getVersion(), "Parent"));
                }

                if (StringUtils.isNotBlank(pom.getDescription())) {
                    this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_PROJECT, "", pom.getDescription(), "Description"));
                }

                if (StringUtils.isNotBlank(pom.getProjectUrl())) {
                    this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_PROJECT, "", pom.getProjectUrl(), "URL"));
                }

                // 源代码管理系统
                Pom.Scm scm = pom.getScm();
                if (StringUtils.isNotBlank(scm.getConnection())) {
                    this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_SCM, "", scm.getConnection(), "Connection"));
                }
                if (StringUtils.isNotBlank(scm.getDeveloperConnection())) {
                    this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_SCM, "", scm.getDeveloperConnection(), "DeveloperConnection"));
                }
                if (StringUtils.isNotBlank(scm.getUrl())) {
                    this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_SCM, "", scm.getUrl(), "URL"));
                }
                if (StringUtils.isNotBlank(scm.getTag())) {
                    this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_SCM, "", scm.getTag(), "Tag"));
                }

                // 开发人员
                List<Pom.Developer> developers = pom.getDevelopers();
                for (int i = 0; i < developers.size(); i++) {
                    Pom.Developer developer = developers.get(i);
                    this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_DEVELOPER, "", developer.getName(), "Name"));

                    if (StringUtils.isNotBlank(developer.getEmail())) {
                        this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_DEVELOPER, "", developer.getEmail(), "Email"));
                    }

                    if (StringUtils.isNotBlank(developer.getOrganization())) {
                        this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_DEVELOPER, "", developer.getOrganization(), "Organization"));
                    }

                    if (StringUtils.isNotBlank(developer.getOrganizationUrl())) {
                        this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_DEVELOPER, "", developer.getOrganizationUrl(), "OrganizationUrl"));
                    }

                    if (!developer.getRoles().isEmpty()) {
                        for (String role : developer.getRoles()) {
                            this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_DEVELOPER, "", role, "Role"));
                        }
                    }

                    if (StringUtils.isNotBlank(developer.getTimezone())) {
                        this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_DEVELOPER, "", this.parseTimezone(developer.getTimezone()), "Timezone"));
                    }
                }

                // 开源许可证
                List<Pom.License> licenses = pom.getLicenses();
                for (int i = 0; i < licenses.size(); i++) {
                    Pom.License license = licenses.get(i);
                    this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_LICENSE, "", license.getName(), "Name"));

                    if (StringUtils.isNotBlank(license.getUrl())) {
                        this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_LICENSE, "", license.getUrl(), "URL"));
                    }

                    if (StringUtils.isNotBlank(license.getComments())) {
                        this.child.add(new SearchNavigationDetail(plugin, artifact, MavenSearchPluginIcon.RIGHT_LICENSE, "", license.getComments(), "Comment"));
                    }
                }
            }

            this.setLeftIcon(null);
        } else {
            this.update(plugin, artifact);
        }
    }

    public void fold() {
        this.child.clear();
        this.setLeftIcon(null);
    }

    private void update(MavenSearch search, Artifact artifact) {
        if (search.getService().isRunning(SearchPomJob.class, job -> job.getArtifact().equals(artifact))) {
            this.setLeftIcon(MavenSearchPluginIcon.LEFT_WAITING);
        } else {
            this.setLeftIcon(null);
        }
    }

    /**
     * 解析时区字符串为 ZoneId。
     *
     * @param timezone 时区字符串，例如 "+8", "-05:30", "Asia/Shanghai"
     * @return ZoneId 对象，解析失败返回系统默认时区。
     */
    private String parseTimezone(String timezone) {
        if (StringUtils.isBlank(timezone)) {
            return null;
        }

        try {
            return ZoneId.of(timezone).toString(); // 尝试解析为标准时区 ID
        } catch (Throwable ex) {
            try {
                return ZoneId.ofOffset("UTC", java.time.ZoneOffset.of(timezone)).toString(); // 如果解析失败，尝试解析为 UTC 偏移量
            } catch (Throwable e) {
                // 最终解析失败，使用默认时区
                if (log.isWarnEnabled()) {
                    log.warn("can not parse Timezone: {} ", timezone);
                }
                return null;
            }
        }
    }
}
