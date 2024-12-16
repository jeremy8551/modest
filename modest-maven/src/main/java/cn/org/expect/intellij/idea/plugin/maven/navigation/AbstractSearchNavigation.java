package cn.org.expect.intellij.idea.plugin.maven.navigation;

import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearch;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginIcon;
import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenSearchDownloadJob;
import cn.org.expect.maven.Artifact;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.UniqueSequenceGenerator;
import com.intellij.navigation.ItemPresentation;

public abstract class AbstractSearchNavigation implements MavenSearchNavigation, ItemPresentation {

    /** 序号生成器 */
    protected final static UniqueSequenceGenerator UNIQUE = new UniqueSequenceGenerator("Navigation-{}", 1);

    /** 层级 */
    private int depth;

    /** 工件 */
    private final Artifact artifact;

    /** 序号 */
    protected final String id;

    /** 左侧图标 */
    private volatile Icon leftIcon;

    /** 右侧图标 */
    private volatile Icon rightIcon;

    /** 右侧文本 */
    private String rightText;

    /** true表示折叠，false表示展开 */
    private volatile boolean fold;

    /** 左侧文本 */
    private String presentableText;

    /** 左侧小字的文本 */
    private String locationString;

    public AbstractSearchNavigation(Artifact artifact) {
        this.id = UNIQUE.nextString();
        this.artifact = Ensure.notNull(artifact);
        this.fold = true;
        this.depth = 1;
    }

    public String getName() {
        return this.id + ":" + this.artifact.toMavenId();
    }

    /**
     * 左侧图标
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     * @return 图标
     */
    public Icon getIcon(boolean unused) {
        return this.leftIcon;
    }

    /**
     * 返回导航记录对象
     *
     * @return 导航记录对象
     */
    public ItemPresentation getPresentation() {
        return this;
    }

    /**
     * 左键点击导航记录执行的操作
     *
     * @param requestFocus {@code true} if focus requesting is necessary
     */
    public void navigate(boolean requestFocus) {
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public void setLeftIcon(Icon icon) {
        this.leftIcon = icon;
    }

    public Icon getLeftIcon() {
        return leftIcon;
    }

    public void setRightIcon(Icon icon) {
        this.rightIcon = icon;
    }

    public Icon getRightIcon() {
        return this.rightIcon;
    }

    public void setRightText(String rightText) {
        this.rightText = StringUtils.defaultString(rightText, "");
    }

    public String getRightText() {
        return rightText;
    }

    public void setPresentableText(String text) {
        this.presentableText = StringUtils.defaultString(text, "");
    }

    public String getPresentableText() {
        return this.presentableText;
    }

    public void setLocationString(String text) {
        this.locationString = StringUtils.defaultString(text, "");
    }

    public String getLocationString() {
        return this.locationString;
    }

    public boolean isFold() {
        return this.fold;
    }

    /**
     * 设置是否折叠
     *
     * @param fold true表示折叠 false表示展开
     */
    public void setFold(boolean fold) {
        this.fold = fold;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = Ensure.fromOne(depth);
    }

    public void update(MavenSearch search) {
        Artifact artifact = this.getArtifact();

        // 如果正在下载工件，则更新图标
        if (search.getService().isRunning(MavenSearchDownloadJob.class, job -> job.getArtifact().equals(artifact))) { // 正在下载
            this.setRightIcon(MavenSearchPluginIcon.RIGHT_DOWNLOAD);
            return;
        }

        // 如果工件已下载，则更新图标
        if (search.getLocalRepository().exists(artifact)) {
            this.setRightIcon(MavenSearchPluginIcon.RIGHT_LOCAL);
        } else {
            this.setRightIcon(MavenSearchPluginIcon.RIGHT_REMOTE);
        }
    }

    public boolean equals(Object o) {
        return o != null && o.getClass().equals(this.getClass()) && ((AbstractSearchNavigation) o).id == this.id;
    }
}
