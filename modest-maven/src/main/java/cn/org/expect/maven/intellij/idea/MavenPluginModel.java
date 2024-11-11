package cn.org.expect.maven.intellij.idea;

import cn.org.expect.maven.intellij.idea.navigation.MavenSearchNavigation;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;

public class MavenPluginModel extends FilteringGotoByModel<Object> {

    public MavenPluginModel(Project project, ChooseByNameContributor contributor) {
        super(project, new ChooseByNameContributor[]{contributor});
    }

    @Override
    protected NavigationItem filterValueFor(NavigationItem item) {
        return null;
    }

    @Override
    public boolean useMiddleMatching() {
        return super.useMiddleMatching();
    }

    @Override
    protected boolean acceptItem(NavigationItem item) {
        return item instanceof MavenSearchNavigation;
    }

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
        return MavenSearchPlugin.class.getSimpleName() + "CheckBox";
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
        return new String[]{":"};
    }

    @Override
    public String getFullName(Object element) {
        if (element instanceof NavigationItem) {
            return ((NavigationItem) element).getName();
        }

        throw new UnsupportedOperationException(element == null ? "" : element.toString());
    }

    @Override
    public boolean willOpenEditor() {
        return false;
    }
}
