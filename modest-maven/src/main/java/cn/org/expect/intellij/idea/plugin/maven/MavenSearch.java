package cn.org.expect.intellij.idea.plugin.maven;

import cn.org.expect.intellij.idea.plugin.maven.settings.MavenPluginSettings;
import cn.org.expect.maven.search.ArtifactSearch;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;

public interface MavenSearch extends ArtifactSearch {

    MavenPluginSettings getSettings();

    SearchEverywhereContributor<?> getContributor();
}
