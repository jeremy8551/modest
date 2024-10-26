package cn.org.expect.modest.idea.plugin.navigation;

public class MavenFinderNavigationItemWrapper extends MavenFinderNavigationItem {

    public MavenFinderNavigationItemWrapper(MavenFinderNavigationItem item) {
        super(item.getArtifact());
    }
}
