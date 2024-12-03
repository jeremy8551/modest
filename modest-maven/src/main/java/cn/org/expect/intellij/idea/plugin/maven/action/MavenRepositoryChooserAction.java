package cn.org.expect.intellij.idea.plugin.maven.action;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginApplication;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchScope;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchScopeDescriptor;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.ArtifactRepository;
import cn.org.expect.util.CollectionUtils;
import com.intellij.ide.actions.searcheverywhere.ScopeChooserAction;
import com.intellij.ide.util.scopeChooser.ScopeDescriptor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

public class MavenRepositoryChooserAction extends ScopeChooserAction {
    private final static Log log = LogFactory.getLog(MavenRepositoryChooserAction.class);

    private final List<MavenSearchScopeDescriptor> descriptors;

    private final MavenSearchPlugin plugin;

    private final Runnable onChanged;

    public MavenRepositoryChooserAction(MavenSearchPlugin plugin, Runnable onChanged) {
        this.plugin = plugin;
        this.onChanged = onChanged;
        this.descriptors = new ArrayList<>();
        MavenSearchScope[] list = MavenSearchPluginApplication.get().getScopes();
        for (MavenSearchScope scope : list) {
            this.descriptors.add(new MavenSearchScopeDescriptor(scope));
        }
    }

    public boolean isEverywhere() {
        return true;
    }

    public void setEverywhere(boolean everywhere) {
    }

    public boolean canToggleEverywhere() {
        return true;
    }

    /**
     * 在下拉列表中，光标在选项上触发的事件
     *
     * @param e 事件
     * @return 事件的处理逻辑
     */
    public AnAction @NotNull [] getChildren(AnActionEvent e) {
        if (log.isTraceEnabled()) {
            log.trace("getChildren({}) ", e);
        }
        return new AnAction[0];
    }

    /**
     * 在下拉列表中，选中某一个选项触发的事件
     *
     * @param descriptor 选项信息
     */
    protected void onScopeSelected(@NotNull ScopeDescriptor descriptor) {
        String repositoryId = ((MavenSearchScope) descriptor.getScope()).getRepositoryId();
        plugin.setRepositoryId(repositoryId);
        onChanged.run(); // 更新：搜索框右侧的广告信息
        plugin.asyncSearch();
    }

    protected @NotNull ScopeDescriptor getSelectedScope() {
        ArtifactRepository repository = plugin.getRepository();
        if (repository != null) {
            for (MavenSearchScopeDescriptor descriptor : descriptors) {
                if (repository.getId().equals(descriptor.getScope().getRepositoryId())) {
                    return descriptor;
                }
            }
        }
        return CollectionUtils.firstElement(descriptors);
    }

    protected void onProjectScopeToggled() {
    }

    protected boolean processScopes(@NotNull Processor<? super ScopeDescriptor> processor) {
        return ContainerUtil.process(descriptors, processor);
    }
}
