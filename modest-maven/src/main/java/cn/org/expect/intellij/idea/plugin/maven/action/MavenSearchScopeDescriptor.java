package cn.org.expect.intellij.idea.plugin.maven.action;

import com.intellij.ide.util.scopeChooser.ScopeDescriptor;
import com.intellij.psi.search.SearchScope;
import org.jetbrains.annotations.Nullable;

public class MavenSearchScopeDescriptor extends ScopeDescriptor {

    public MavenSearchScopeDescriptor(@Nullable SearchScope scope) {
        super(scope);
    }

    public @Nullable MavenSearchScope getScope() {
        return (MavenSearchScope) super.getScope();
    }
}
