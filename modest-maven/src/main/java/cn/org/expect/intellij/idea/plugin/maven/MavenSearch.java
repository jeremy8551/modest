package cn.org.expect.intellij.idea.plugin.maven;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenSearchNavigationList;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationClass;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationHead;
import cn.org.expect.intellij.idea.plugin.maven.settings.MavenSearchPluginSettings;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.repository.clazz.SearchClassInRepository;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.search.ArtifactSearch;
import cn.org.expect.util.ClassUtils;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;

public interface MavenSearch extends ArtifactSearch {

    MavenSearchPluginSettings getSettings();

    SearchEverywhereContributor<?> getContributor();

    default MavenSearchNavigationList toNavigationList(ArtifactSearchResult result) {
        if (result == null) {
            return new MavenSearchNavigationList(new ArrayList<>(0), 0, false);
        }

        int priority = this.getSettings().getNavigationPriority();
        SearchEverywhereContributor<?> contributor = this.getContributor();
        List<SearchEverywhereFoundElementInfo> list = new ArrayList<>();

        // 按类名搜索
        if (ClassUtils.equals(this.getRepository().getClass(), SearchClassInRepository.class)) {
            for (Artifact artifact : result.getList()) {
                SearchNavigationClass navigation = new SearchNavigationClass(artifact);
                list.add(new SearchEverywhereFoundElementInfo(navigation, priority, contributor));
            }
            return new MavenSearchNavigationList(list, result.getFoundNumber(), result.isHasMore());
        }

        // 搜索工件
        for (Artifact artifact : result.getList()) {
            list.add(new SearchEverywhereFoundElementInfo(new SearchNavigationHead(artifact), priority, contributor));
        }
        return new MavenSearchNavigationList(list, result.getFoundNumber(), result.isHasMore());
    }
}
