package cn.org.expect.modest.idea.plugin;

import cn.org.expect.modest.idea.plugin.navigation.MavenFinderNavigation;
import cn.org.expect.modest.idea.plugin.navigation.MavenFinderNavigationItemWrapper;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;

public class MavenFinderModel extends FilteringGotoByModel<Object> {

    public MavenFinderModel(Project project, ChooseByNameContributor contributor) {
        super(project, new ChooseByNameContributor[]{contributor});
    }

    @Override
    protected NavigationItem filterValueFor(NavigationItem item) {
        if (item instanceof MavenFinderNavigationItemWrapper) {
            return null;
        } else {
            return item instanceof MavenFinderNavigation ? item : null;
        }
    }

    @Override
    public boolean useMiddleMatching() {
        return super.useMiddleMatching();
    }

//    @Override
//    protected boolean acceptItem(NavigationItem item) {
//        NavigationItem filterValue = this.filterValueFor(item);
//        return filterValue != null;
//    }

    @Override
    public String getPromptText() {
        return "Search artifact in Maven Repository ..";
    }

    @Override
    public String getNotInMessage() {
        return this.getNotFoundMessage();
    }

    @Override
    public String getNotFoundMessage() {
        return "No matching results";
    }

    @Override
    public String getCheckBoxName() {
        return "MavenFinderBox";
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        return false;
    }

    @Override
    public void saveInitialCheckBoxState(boolean state) {
    }

    @Override
    public String[] getSeparators() {
        return new String[0];
    }

    @Override
    public String getFullName(Object element) {
        return ((NavigationItem) element).getName();
    }

    @Override
    public boolean willOpenEditor() {
        return false;
    }
}
