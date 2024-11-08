package cn.org.expect.intellijidea.plugin.maven.navigation;

import javax.swing.*;

import cn.org.expect.intellijidea.plugin.maven.impl.MavenArtifactImpl;

public class MavenFinderBlankItem extends MavenFinderNavigationCatalog {

    public MavenFinderBlankItem() {
        super(new MavenArtifactImpl());
    }

    @Override
    public Icon getIcon(boolean unused) {
        return null;
    }

    @Override
    public String getLocationString() {
        return "";
    }
}
