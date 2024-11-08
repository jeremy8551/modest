package cn.org.expect.intellijidea.plugin.maven.navigation;

import javax.swing.*;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.util.Ensure;
import com.intellij.navigation.ItemPresentation;
import com.intellij.util.TextWithIcon;

public abstract class AbstractMavenFinderNavigation implements MavenFinderNavigation, ItemPresentation {

    protected final MavenArtifact artifact;

    protected static volatile long NUMBER = 1;

    protected final long id;

    protected volatile Icon icon;

    public AbstractMavenFinderNavigation(MavenArtifact artifact, Icon icon) {
        this.id = NUMBER++;
        this.artifact = Ensure.notNull(artifact);
        this.icon = Ensure.notNull(icon);
    }

    @Override
    public MavenArtifact getArtifact() {
        return artifact;
    }

    @Override
    public TextWithIcon getRightIcon() {
        return new TextWithIcon("", this.icon);
    }

    @Override
    public void setLeftIcon(Icon icon) {
        this.icon = icon;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return this.id + ":" + this.artifact.getName();
    }

    @Override
    public ItemPresentation getPresentation() {
        return this;
    }

    @Override
    public String getPresentableText() {
        return this.artifact.getArtifactId();
    }

    @Override
    public void navigate(boolean requestFocus) {
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass().equals(this.getClass()) && ((AbstractMavenFinderNavigation) o).id == this.id;
    }
}
