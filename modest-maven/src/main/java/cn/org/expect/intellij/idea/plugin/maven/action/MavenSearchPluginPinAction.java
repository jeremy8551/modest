package cn.org.expect.intellij.idea.plugin.maven.action;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContributor;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginFactory;
import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenSearchExecutorServiceImpl;
import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenSearchPluginPinJob;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.MavenMessage;
import cn.org.expect.util.Ensure;
import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.searcheverywhere.ActionSearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereSpellingCorrector;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.IdeGlassPane;
import com.intellij.openapi.wm.IdeGlassPaneUtil;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

public class MavenSearchPluginPinAction extends ToggleAction {
    private final static Log log = LogFactory.getLog(MavenSearchPluginPinAction.class);

    /** 组件 */
    public final static PinWindow PIN = new PinWindow();

    /** 搜索插件 */
    private final MavenSearchPlugin plugin;

    public MavenSearchPluginPinAction(MavenSearchPlugin plugin) {
        super(MavenMessage.get("maven.search.btn.pin.text"), MavenMessage.get("maven.search.btn.pin.description"), AllIcons.General.Pin_tab);
        this.plugin = Ensure.notNull(plugin);
//        int mask = SystemInfo.isMac ? 256 : 128;
//        this.registerCustomShortcutSet(68, mask, plugin.getIdeaUI().getSearchEverywhereUI());
    }

    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT; // 在后台线程更新动作状态
    }

    public boolean isSelected(@NotNull AnActionEvent event) {
        return PIN.isPin();
    }

    public void setSelected(@NotNull AnActionEvent event, boolean state) {
    }

    public void actionPerformed(@NotNull AnActionEvent event) {
        if (MavenSearchPluginPinAction.PIN.isPin()) {
            MavenSearchPluginPinAction.PIN.cancel(); // 取消 pin 操作
        } else {
            if (MavenSearchPluginPinAction.PIN.isOpen()) {
                MavenSearchPluginPinAction.PIN.active();
            } else {
                this.pin(event); // 执行 pin 操作
            }
        }
    }

    protected void pin(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        try {
            SearchEverywhereManager manager = SearchEverywhereManager.getInstance(event.getProject());
            Method method = manager.getClass().getDeclaredMethod("createView", Project.class, List.class, SearchEverywhereSpellingCorrector.class);
            method.setAccessible(true);

            List<SearchEverywhereContributor<?>> contributors = this.createContributors(event, project);
            SearchEverywhereSpellingCorrector spellingCorrector = SearchEverywhereSpellingCorrector.getInstance(project);
            SearchEverywhereUI newUI = (SearchEverywhereUI) method.invoke(manager, project, contributors, spellingCorrector);
            MavenSearchPluginPinAction.PIN.setParameter(newUI, project);

            // 执行任务
            for (SearchEverywhereContributor<?> contributor : contributors) {
                if (contributor instanceof MavenSearchPluginContributor) {
                    MavenSearchPlugin searchPlugin = ((MavenSearchPluginContributor) contributor).getPlugin();
                    searchPlugin.getContext().clone(this.plugin.getContext());
                    searchPlugin.getService().setParameter(MavenSearchExecutorServiceImpl.PARAMETER, null);
                    searchPlugin.execute(new MavenSearchPluginPinJob(this.plugin, () -> super.actionPerformed(event)));
                }
            }
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage());
        }
    }

    protected List<SearchEverywhereContributor<?>> createContributors(@NotNull AnActionEvent event, Project project) {
        if (project == null) {
            ActionSearchEverywhereContributor.Factory factory = new ActionSearchEverywhereContributor.Factory();
            return Collections.singletonList(factory.createContributor(event));
        }

        List<SearchEverywhereContributor<?>> list = new ArrayList<>();
        for (SearchEverywhereContributorFactory<?> factory : SearchEverywhereContributor.EP_NAME.getExtensionList()) {
            if (factory.isAvailable(project)) {
                if (factory instanceof MavenSearchPluginFactory) {
                    list.add(((MavenSearchPluginFactory) factory).create(event));
                } else {
                    list.add(factory.createContributor(event));
                }
            }
        }
        return list;
    }

    public static class PinWindow {

        /** JFrame框架 */
        private volatile JFrame frame;

        /** 搜索对话框 */
        private volatile SearchEverywhereUI ui;

        /** true表示 pin 窗口是最小的状态 */
        private volatile boolean mini;

        /** true表示显示 */
        private volatile boolean show;

        private volatile boolean pin;

        public PinWindow() {
            this.show = false;
            this.mini = false;
            this.pin = false;
        }

        public boolean isOpen() {
            return this.frame != null && this.ui != null;
        }

        /**
         * 如果 pin 窗口是最小的状态，则扩展窗口大小
         */
        public synchronized void extend() {
            if (this.frame != null && this.mini) {
                Dimension dimension = this.frame.getSize();
                this.frame.setSize(new Dimension(dimension.width, 700));
                this.frame.setVisible(true);
                this.mini = false;
            }
        }

        public synchronized void setParameter(SearchEverywhereUI ui, Project project) {
            if (this.frame == null) {
                JFrame frame = new JFrame();
                frame.setVisible(false);
                frame.setUndecorated(true);
                frame.setResizable(true);
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.addMouseListener(new ResizeMouseAdapter(frame));
                frame.addMouseMotionListener(new ResizeMouseAdapter(frame));
                frame.addWindowFocusListener(new WindowAdapter() {
                    public void windowLostFocus(WindowEvent event) { // 失去焦点
                        frame.setVisible(show);
                    }
                });

                try {
                    IdeFrame ideFrame = WindowManager.getInstance().getIdeFrame(project);
                    IdeGlassPane ideGlassPane = IdeGlassPaneUtil.find(ideFrame.getComponent());
                    frame.setGlassPane((Component) ideGlassPane);
                } catch (Exception e) {
                    log.error(e.getLocalizedMessage(), e);
                }

                this.frame = frame;
            }

            this.frame.add(ui, BorderLayout.CENTER);
            this.ui = ui;
        }

        public boolean isPin() {
            return this.pin;
        }

        public void show(Dimension dimension, Point location, boolean mini) {
            if (this.frame != null) {
                this.frame.setAlwaysOnTop(true);
                this.frame.setSize(dimension);
                this.frame.setLocation(location);
                this.frame.setVisible(true);
            }

            if (this.ui != null) {
                this.ui.repaint();
            }

            this.mini = mini;
            this.show = true;
            this.pin = true;
        }

        public void cancel() {
            this.pin = false;
            this.show = false;
            this.mini = false;

            if (this.frame != null) {
                this.frame.setAlwaysOnTop(false);
                this.frame.setVisible(true);
            }
        }

        public void active() {
            this.pin = true;
            this.show = true;

            if (this.frame != null) {
                this.frame.setAlwaysOnTop(true);
                this.frame.setVisible(true);
            }
        }

        public void dispose() {
            if (this.frame != null) {
                this.frame.setAlwaysOnTop(false);
                this.frame.setVisible(false);
                this.frame.dispose();
                this.frame = null;
            }

            if (this.ui != null) {
                this.ui.dispose();
                this.ui = null;
            }

            this.show = false;
            this.mini = false;
            this.pin = false;
        }

        public SearchEverywhereUI getUI() {
            return ui;
        }
    }

    private static class ResizeMouseAdapter extends MouseAdapter {

        private static final int BORDER_THICKNESS = 7; // 可调整边框的厚度
        private final JFrame frame;
        private volatile Cursor cursor = Cursor.getDefaultCursor();
        private volatile Point initialMousePosition;

        public ResizeMouseAdapter(JFrame frame) {
            this.frame = frame;
        }

        public void mouseMoved(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            int width = this.frame.getWidth();
            int height = this.frame.getHeight();

//            log.debug("x: {}, y: {}, with: {}, height: {}", x, y, width, height);

            // 判断鼠标位于边框的哪个位置，并设置相应的光标
            if (x >= width - BORDER_THICKNESS && x <= width && y >= height - BORDER_THICKNESS && y <= height) {
                this.cursor = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
            } else if (x >= width - BORDER_THICKNESS && x <= width && y >= BORDER_THICKNESS && y <= height - BORDER_THICKNESS) {
                this.cursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
            } else if (x >= BORDER_THICKNESS && x <= width && y >= height - BORDER_THICKNESS && y <= height) {
                this.cursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
            } else {
                this.cursor = Cursor.getDefaultCursor();
            }
            this.frame.setCursor(this.cursor);
        }

        public void mousePressed(MouseEvent e) {
            this.initialMousePosition = e.getPoint();
        }

        public void mouseReleased(MouseEvent e) {
            Point currentMousePosition = e.getPoint();
            int dx = currentMousePosition.x - this.initialMousePosition.x;
            int dy = currentMousePosition.y - this.initialMousePosition.y;

            Dimension dimension = new Dimension(this.frame.getWidth() + dx, this.frame.getHeight() + dy);
            this.frame.setSize(dimension);
            this.frame.setVisible(true);
            this.frame.setCursor(Cursor.getDefaultCursor());
            this.initialMousePosition = currentMousePosition;
        }
    }
}
