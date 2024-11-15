package cn.org.expect.maven.intellij.idea;

import cn.org.expect.maven.intellij.idea.navigation.MavenSearchNavigation;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;

public class MavenSearchPluginModel extends FilteringGotoByModel<Object> {

    public MavenSearchPluginModel(Project project, ChooseByNameContributor contributor) {
        super(project, new ChooseByNameContributor[]{contributor});
    }

    protected NavigationItem filterValueFor(NavigationItem item) {
        return null;
    }

    public boolean useMiddleMatching() {
        return super.useMiddleMatching();
    }

    protected boolean acceptItem(NavigationItem item) {
        return item instanceof MavenSearchNavigation;
    }

    public String getPromptText() {
        return "Search artifact in Maven Repository ..";
    }

    public String getNotInMessage() {
        return this.getNotFoundMessage();
    }

    public String getNotFoundMessage() {
        return "No matching results";
    }

    public String getCheckBoxName() {
        return MavenSearchPlugin.class.getSimpleName() + "CheckBox";
    }

    public boolean loadInitialCheckBoxState() {
        return false;
    }

    public void saveInitialCheckBoxState(boolean state) {
    }

    public String[] getSeparators() {
        return new String[]{":"};
    }

    public String getFullName(Object element) {
        if (element instanceof NavigationItem) {
            return ((NavigationItem) element).getName();
        }

        throw new UnsupportedOperationException(element == null ? "" : element.toString());
    }

    public boolean willOpenEditor() {
        return false;
    }
}
