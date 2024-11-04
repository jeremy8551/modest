package cn.org.expect.intellijidea.plugin.maven.navigation;

import javax.swing.*;

import cn.org.expect.intellijidea.plugin.maven.impl.MavenArtifactImpl;
import com.intellij.navigation.ItemPresentation;

public class MavenFinderBlankItem extends MavenFinderNavigationCatalog {

    public MavenFinderBlankItem() {
        super(new MavenArtifactImpl());
    }

    @Override
    public ItemPresentation getPresentation() {
        return super.getPresentation();
    }

    @Override
    public String getPresentableText() {
        return "";
    }

    @Override
    public Icon getIcon(boolean unused) {
        return null;
    }

    @Override
    public String getLocationString() {
        return "";
    }

    @Override
    public String getName() {
        return super.getName();
    }
}
