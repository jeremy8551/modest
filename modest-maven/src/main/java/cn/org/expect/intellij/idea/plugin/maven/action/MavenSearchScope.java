package cn.org.expect.intellij.idea.plugin.maven.action;

import javax.swing.*;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.MavenOption;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import org.jetbrains.annotations.NotNull;

public class MavenSearchScope extends GlobalSearchScope {
    private final static Log log = LogFactory.getLog(MavenSearchScope.class);

    private final MavenOption option;

    public MavenSearchScope(@NotNull MavenOption option) {
        super();
        this.option = option;
    }

    public @NotNull String getDisplayName() {
        return this.option.getDisplayName();
    }

    public MavenOption getOption() {
        return option;
    }

    public Icon getIcon() {
        return null;
    }

    public boolean isSearchInModuleContent(@NotNull Module aModule) {
        return true;
    }

    public boolean isSearchInLibraries() {
        return true;
    }

    /**
     * 交集
     *
     * @param scope2 搜索范围
     * @return 集合
     */
    public @NotNull SearchScope intersectWith(@NotNull SearchScope scope2) {
        return scope2;
    }

    /**
     * 并集
     *
     * @param scope 搜素范围
     * @return 集合
     */
    public @NotNull GlobalSearchScope union(@NotNull SearchScope scope) {
        return this;
    }

    public boolean contains(VirtualFile file) {
        if (log.isDebugEnabled()) {
            log.debug("contains({}) ", file);
        }
        return true;
    }
}
