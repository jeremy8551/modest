package cn.org.expect.maven.intellij.idea.navigation;

import javax.swing.*;

import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.util.Ensure;
import com.intellij.navigation.ItemPresentation;
import com.intellij.util.TextWithIcon;

public abstract class AbstractSearchNavigation implements MavenSearchNavigation, ItemPresentation {

    protected final MavenArtifact artifact;

    protected static volatile long NUMBER = 1;

    protected final long id;

    protected volatile Icon icon;

    public AbstractSearchNavigation(MavenArtifact artifact, Icon icon) {
        this.id = NUMBER++;
        this.artifact = Ensure.notNull(artifact);
        this.icon = Ensure.notNull(icon);
    }

    public MavenArtifact getArtifact() {
        return artifact;
    }

    public TextWithIcon getRightIcon() {
        return new TextWithIcon("", this.icon);
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return this.id + ":" + this.artifact.getGroupId() + ":" + this.artifact.getArtifactId() + ":" + this.artifact.getVersion();
    }

    public ItemPresentation getPresentation() {
        return this;
    }

    public String getPresentableText() {
        return this.artifact.getArtifactId();
    }

    public void navigate(boolean requestFocus) {
    }

    public boolean equals(Object o) {
        return o != null && o.getClass().equals(this.getClass()) && ((AbstractSearchNavigation) o).id == this.id;
    }
}
