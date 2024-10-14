package cn.org.expect.modest.idea.plugin;

import cn.org.expect.util.Ensure;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MavenFinderFilteringGotoByModel extends FilteringGotoByModel<Object> {

    public MavenFinderFilteringGotoByModel(@NotNull Project project, ChooseByNameContributor contributor) {
        super(project, new ChooseByNameContributor[]{contributor});
    }

    protected @Nullable Object filterValueFor(NavigationItem item) {
        return item instanceof MavenFinderNavigationItem ? item : null;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    public String getPromptText() {
        return "Search article, procedure and chapter titles";
    }

    public @NotNull String getNotInMessage() {
        String message = this.getNotFoundMessage();
        return Ensure.notNull(message);
    }

    public @NotNull String getNotFoundMessage() {
        return "No matching results";
    }

    public @NotNull String getCheckBoxName() {
        return "MavenFinder";
    }

    public boolean loadInitialCheckBoxState() {
        return false;
    }

    public void saveInitialCheckBoxState(boolean state) {
    }

    public String @NotNull [] getSeparators() {
        return new String[0];
    }

    public @Nullable String getFullName(@NotNull Object element) {
        return ((NavigationItem) element).getName();
    }

    public boolean willOpenEditor() {
        return false;
    }
}
