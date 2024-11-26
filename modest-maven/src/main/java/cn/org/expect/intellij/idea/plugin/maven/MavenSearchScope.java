package cn.org.expect.intellij.idea.plugin.maven;

import javax.swing.*;

import cn.org.expect.ioc.EasyBeanInfo;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.search.MavenSearchMessage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import org.jetbrains.annotations.NotNull;

public class MavenSearchScope extends GlobalSearchScope {
    private final static Log log = LogFactory.getLog(MavenSearchScope.class);

    private final String repositoryId;

    private final String description;

    private Class<?> type;

    private final Icon icon;

    public MavenSearchScope(EasyBeanInfo beanInfo) {
        super();
        this.repositoryId = beanInfo.getName();
        this.description = MavenSearchMessage.get("maven.search.repository." + this.repositoryId + ".id");
        this.icon = null;
        this.type = beanInfo.getType();
    }

    public Class<?> getType() {
        return type;
    }

    public @NotNull String getRepositoryId() {
        return repositoryId;
    }

    public @NotNull String getDisplayName() {
        return this.description;
    }

    public Icon getIcon() {
        return this.icon;
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
