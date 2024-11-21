package cn.org.expect.intellij.idea.plugin.maven;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.ioc.EasyBeanInfo;
import cn.org.expect.maven.repository.MavenRepository;
import com.intellij.ide.util.scopeChooser.ScopeDescriptor;
import com.intellij.psi.search.SearchScope;
import org.jetbrains.annotations.Nullable;

public class MavenSearchScopeDescriptor extends ScopeDescriptor {

    public static List<MavenSearchScopeDescriptor> getList() {
        List<MavenSearchScopeDescriptor> list = new ArrayList<>();
        for (EasyBeanInfo beanInfo : MavenSearchPluginApplication.get().getBeanInfoList(MavenRepository.class)) {
            list.add(new MavenSearchScopeDescriptor(new MavenSearchScope(beanInfo)));
        }
        return list;
    }

    public static String toRepositoryId(String displayName) {
        List<MavenSearchScopeDescriptor> list = MavenSearchScopeDescriptor.getList();
        for (MavenSearchScopeDescriptor scope : list) {
            if (displayName.equals(scope.getDisplayName())) {
                return scope.getScope().getRepositoryId();
            }
        }
        return null;
    }

    public MavenSearchScopeDescriptor(@Nullable SearchScope scope) {
        super(scope);
    }

    public @Nullable MavenSearchScope getScope() {
        return (MavenSearchScope) super.getScope();
    }
}
