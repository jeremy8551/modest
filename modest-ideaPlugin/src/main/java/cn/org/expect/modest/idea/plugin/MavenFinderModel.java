package cn.org.expect.modest.idea.plugin;

import cn.org.expect.util.Ensure;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;

public class MavenFinderModel extends FilteringGotoByModel<Object> {

    public MavenFinderModel(Project project, ChooseByNameContributor contributor) {
        super(project, new ChooseByNameContributor[]{contributor});
    }

    protected Object filterValueFor(NavigationItem item) {
        return item instanceof MavenFinderNavigationItem ? item : null;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    public String getPromptText() {
        return "Search artifact in Maven Repository ..";
    }

    public String getNotInMessage() {
        String message = this.getNotFoundMessage();
        return Ensure.notNull(message);
    }

    public String getNotFoundMessage() {
        return "No matching results";
    }

    public String getCheckBoxName() {
        return "MavenFinderBox";
    }

    public boolean loadInitialCheckBoxState() {
        return false;
    }

    public void saveInitialCheckBoxState(boolean state) {
    }

    public String[] getSeparators() {
        return new String[0];
    }

    public String getFullName(Object element) {
        return ((NavigationItem) element).getName();
    }

    public boolean willOpenEditor() {
        return false;
    }
}
