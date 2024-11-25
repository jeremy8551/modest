package cn.org.expect.intellij.idea.plugin.maven.action;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContributor;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginFactory;
import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenSearchEDTJob;
import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenSearchExecutorServiceImpl;
import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenSearchPluginPinJob;
import cn.org.expect.maven.search.MavenSearchMessage;
import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.searcheverywhere.ActionSearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereSpellingCorrector;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class MavenSearchPluginPinAction extends ToggleAction {

    public final static PinTab PIN = new PinTab();

    private final MavenSearchPlugin plugin;

    public MavenSearchPluginPinAction(MavenSearchPlugin plugin) {
        super(MavenSearchMessage.get("maven.search.btn.pin.text"), MavenSearchMessage.get("maven.search.btn.pin.description"), AllIcons.General.Pin_tab);
        this.plugin = plugin;
//        int mask = SystemInfo.isMac ? 256 : 128;
//        this.registerCustomShortcutSet(68, mask, plugin.getIdeaUI().getSearchEverywhereUI());
    }

    public boolean isSelected(@NotNull AnActionEvent event) {
        return PIN.isPin();
    }

    public void setSelected(@NotNull AnActionEvent event, boolean state) {
    }

    public void actionPerformed(@NotNull AnActionEvent event) {
        if (MavenSearchPluginPinAction.PIN.isPin()) {
            MavenSearchPluginPinAction.PIN.dispose(); // 取消 pin 操作
        } else {
            this.pin(event); // 执行 pin 操作
        }
    }

    protected void pin(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        String tabID = this.plugin.getContributor().getSearchProviderId();
        try {
            SearchEverywhereManager manager = SearchEverywhereManager.getInstance(event.getProject());
            Method method = manager.getClass().getDeclaredMethod("createView", Project.class, List.class, SearchEverywhereSpellingCorrector.class);
            method.setAccessible(true);

            List<SearchEverywhereContributor<?>> contributors = createContributors(event, project);
            SearchEverywhereSpellingCorrector spellingCorrector = SearchEverywhereSpellingCorrector.getInstance(project);
            SearchEverywhereUI newUI = (SearchEverywhereUI) method.invoke(manager, project, contributors, spellingCorrector);
            MavenSearchPluginPinAction.PIN.setPin(newUI);
            this.plugin.execute(new MavenSearchEDTJob(() -> newUI.switchToTab(tabID)));
            plugin.getIdeaUI().waitFor(2000, t -> !tabID.equals(newUI.getSelectedTabID()));

            // 执行任务
            for (SearchEverywhereContributor<?> contributor : contributors) {
                if (contributor instanceof MavenSearchPluginContributor) {
                    MavenSearchPlugin searchPlugin = ((MavenSearchPluginContributor) contributor).getPlugin();
                    searchPlugin.getContext().clone(this.plugin.getContext());
                    searchPlugin.getService().setParameter(MavenSearchExecutorServiceImpl.PARAMETER, null);
                    searchPlugin.execute(new MavenSearchPluginPinJob(this.plugin));
                }
            }

            super.actionPerformed(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<SearchEverywhereContributor<?>> createContributors(@NotNull AnActionEvent initEvent, Project project) {
        if (project == null) {
            ActionSearchEverywhereContributor.Factory factory = new ActionSearchEverywhereContributor.Factory();
            return Collections.singletonList(factory.createContributor(initEvent));
        }

        List<SearchEverywhereContributor<?>> list = new ArrayList<>();
        for (SearchEverywhereContributorFactory<?> factory : SearchEverywhereContributor.EP_NAME.getExtensionList()) {
            if (factory.isAvailable(project)) {
                if (factory instanceof MavenSearchPluginFactory) {
                    list.add(((MavenSearchPluginFactory) factory).create(initEvent));
                } else {
                    list.add(factory.createContributor(initEvent));
                }
            }
        }
        return list;
    }

    public static class PinTab {

        private volatile JFrame frame;

        private volatile SearchEverywhereUI ui;

        public PinTab() {
        }

        public void setPin(SearchEverywhereUI ui) {
            JFrame frame = new JFrame(SearchEverywhereUI.class.getSimpleName());
            frame.setVisible(false);
            frame.setUndecorated(true);
            frame.add(ui, BorderLayout.CENTER);
            this.frame = frame;
            this.ui = ui;
        }

        public boolean isPin() {
            return this.frame != null;
        }

        public void show(Dimension dimension, Point location) {
            if (this.frame != null) {
                frame.setSize(dimension);
                frame.setLocation(location);
                frame.setVisible(true);
            }
        }

        public void dispose() {
            if (this.frame != null) {
                this.frame.setVisible(false);
                this.frame.dispose();
                this.frame = null;
            }

            if (this.ui != null) {
                this.ui.dispose();
                this.ui = null;
            }
        }

        public SearchEverywhereUI getUI() {
            return ui;
        }
    }
}
