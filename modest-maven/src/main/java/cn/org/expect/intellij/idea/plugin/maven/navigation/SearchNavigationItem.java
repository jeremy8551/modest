package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearch;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenSearchPomInfoJob;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.MavenIcon;
import cn.org.expect.maven.pom.PomInfo;
import cn.org.expect.util.StringUtils;

public class SearchNavigationItem extends AbstractSearchNavigation {
    private final static Log log = LogFactory.getLog(SearchNavigationItem.class);

    /** 子节点 */
    private final List<SearchNavigationDetail> child;

    public SearchNavigationItem(Artifact artifact) {
        super(artifact);
        this.child = new ArrayList<>();
        this.setDepth(2);
        this.setPresentableText(artifact.getVersion());
        this.setLocationString("");
        this.setLeftIcon(null);
        this.setRightIcon(MavenIcon.RIGHT_REMOTE);
        this.setRightText("");
    }

    public List<? extends MavenSearchNavigation> getNavigationList() {
        return this.child;
    }

    public boolean supportMenu() {
        return true;
    }

    public void displayMenu(MavenSearchPlugin plugin, MavenSearchNavigation navigation, JPopupMenu topMenu, int selectedIndex) {
        plugin.getResultMenu().displayItemMenu(plugin, navigation, topMenu, selectedIndex, 30);
    }

    public boolean supportFold(MavenSearch search) {
        return true;
    }

    public void setUnfold(MavenSearch search) {
        this.setFold(false);
        search.asyncPomInfo(this.getArtifact());
        this.update(search, this.getArtifact());
        search.display();
    }

    public void setFold(MavenSearch search) {
        this.setFold(true);
        this.setLeftIcon(null);
        search.display();
    }

    public void unfold(MavenSearch search) {
        Artifact artifact = this.getArtifact();
        PomInfo pomInfo = search.getPomInfoRepository().select(artifact);
        if (pomInfo != null) {
            if (this.child.isEmpty()) {
                List<PomInfo.Developer> developers = pomInfo.getDevelopers();
                for (int i = 0; i < developers.size(); i++) {
                    PomInfo.Developer developer = developers.get(i);
                    this.child.add(new SearchNavigationDetail(artifact, MavenIcon.RIGHT_DEVELOPER, "", developer.getName(), "Name"));

                    if (StringUtils.isNotBlank(developer.getEmail())) {
                        this.child.add(new SearchNavigationDetail(artifact, MavenIcon.RIGHT_DEVELOPER, "", developer.getEmail(), "Email"));
                    }

                    if (StringUtils.isNotBlank(developer.getOrganization())) {
                        this.child.add(new SearchNavigationDetail(artifact, MavenIcon.RIGHT_DEVELOPER, "", developer.getOrganization(), "Organization"));
                    }

                    if (StringUtils.isNotBlank(developer.getOrganizationUrl())) {
                        this.child.add(new SearchNavigationDetail(artifact, MavenIcon.RIGHT_DEVELOPER, "", developer.getOrganizationUrl(), "OrganizationUrl"));
                    }

                    if (!developer.getRoles().isEmpty()) {
                        for (String role : developer.getRoles()) {
                            this.child.add(new SearchNavigationDetail(artifact, MavenIcon.RIGHT_DEVELOPER, "", role, "Role"));
                        }
                    }

                    if (StringUtils.isNotBlank(developer.getTimezone())) {
                        this.child.add(new SearchNavigationDetail(artifact, MavenIcon.RIGHT_DEVELOPER, "", this.parseTimezone(developer.getTimezone()), "Timezone"));
                    }
                }

                List<PomInfo.License> licenses = pomInfo.getLicenses();
                for (int i = 0; i < licenses.size(); i++) {
                    PomInfo.License license = licenses.get(i);
                    this.child.add(new SearchNavigationDetail(artifact, MavenIcon.RIGHT_LICENSE, "", license.getName(), "Name"));

                    if (StringUtils.isNotBlank(license.getUrl())) {
                        this.child.add(new SearchNavigationDetail(artifact, MavenIcon.RIGHT_LICENSE, "", license.getUrl(), "URL"));
                    }

                    if (StringUtils.isNotBlank(license.getComments())) {
                        this.child.add(new SearchNavigationDetail(artifact, MavenIcon.RIGHT_LICENSE, "", license.getComments(), "Comment"));
                    }
                }
            }

            this.setLeftIcon(null);
        } else {
            this.update(search, artifact);
        }
    }

    public void fold(MavenSearch search) {
        this.child.clear();
        this.setLeftIcon(null);
    }

    private void update(MavenSearch search, Artifact artifact) {
        if (search.getService().isRunning(MavenSearchPomInfoJob.class, job -> job.getArtifact().equals(artifact))) {
            this.setLeftIcon(MavenIcon.LEFT_WAITING);
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
