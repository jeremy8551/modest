package cn.org.expect.intellij.idea.plugin.maven;

import javax.swing.*;

import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.search.ArtifactSearchStatusMessageType;
import cn.org.expect.util.Dates;
import cn.org.expect.util.MessageFormatter;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.ide.actions.searcheverywhere.footer.ExtendedInfoComponent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.Advertiser;

public class IdeaSearchUI {
    protected final static Log log = LogFactory.getLog(IdeaSearchUI.class);

    /** Idea搜索功能UI */
    protected volatile SearchEverywhereUI ui;

    /** 扩展模式状态栏信息 */
    private volatile String extendedInfoText;

    /** 状态栏更新请求 */
    private volatile StatusBar statusBar;

    /** 搜索显示 */
    private volatile SearchDisplay display;

    public IdeaSearchUI() {
    }

    public SearchEverywhereUI getSearchEverywhereUI() {
        return this.ui;
    }

    public void setSearchEverywhereUI(SearchEverywhereUI ui) {
        this.ui = ui;
    }

    public String getSelectedTabID() {
        if (this.ui == null) {
            return "";
        }

        return this.ui.getSelectedTabID();
    }

    public JTextField getSearchField() {
        if (this.ui == null) {
            return null;
        }

        return this.ui.getSearchField();
    }

    public void setStatusBar(ArtifactSearchStatusMessageType type, String message) {
        if (this.ui == null) {
            return;
        }

        this.statusBar = new StatusBar(type, message);
        Icon icon = this.getIcon(type);
        String fontColor = ArtifactSearchStatusMessageType.ERROR == type ? "red" : "orange";
        String text = new MessageFormatter("<html><span style='color:{};'>{}</span></html>").fill(fontColor, message);

        try {
            // 检查注册项是否启用，为true，表示使用扩展模式作为状态栏
            if (Registry.is("search.everywhere.footer.extended.info")) {
                this.extendedInfoText = text;
                if (StringUtils.isBlank(message)) {
                    return;
                }

                // 更新状态栏中的文本信息
                ExtendedInfoComponent info = JavaDialectFactory.get().getField(this.ui, "myExtendedInfoComponent");
                JBLabel label = JavaDialectFactory.get().getField(info, "text");
                if (label != null) {
                    label.setIcon(icon);
                    label.setText(text);
                    label.repaint();
                }
                return;
            }

            Advertiser advertiser = JavaDialectFactory.get().getField(this.ui, "myHintLabel");
            if (advertiser == null) {
                return;
            }

            // 如果文本信息为空，则显示默认的广告信息
            if (StringUtils.isBlank(message)) {
                advertiser.showRandomText();
                return;
            }

            JLabel myTextPanel = JavaDialectFactory.get().getField(advertiser, "myTextPanel");
            myTextPanel.setText(text);
            myTextPanel.setIcon(icon);
            myTextPanel.repaint();

            JLabel myNextLabel = JavaDialectFactory.get().getField(advertiser, "myNextLabel");
            myNextLabel.setText(null);
            myNextLabel.repaint();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    protected Icon getIcon(ArtifactSearchStatusMessageType type) {
        if (type == null) {
            return null;
        }

        switch (type) {
            case NORMAL:
                return MavenSearchPluginIcon.BOTTOM;

            case RUNNING:
                return MavenSearchPluginIcon.BOTTOM_WAITING;

            case ERROR:
                return MavenSearchPluginIcon.BOTTOM_ERROR;

            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    public SearchDisplay getDisplay() {
        if (this.ui == null) {
            return null;
        }

        if (this.display == null) {
            synchronized (this) {
                if (this.display == null) {
                    JBList<Object> list = JavaDialectFactory.get().getField(this.ui, "myResultsList");
                    SearchListModel listModel = JavaDialectFactory.get().getField(this.ui, "myListModel");
                    this.display = new SearchDisplay(list, listModel);
                }
            }
        }
        return this.display;
    }

    public ProgressIndicator getProgressIndicator() {
        if (this.ui == null) {
            return null;
        }

        return JavaDialectFactory.get().getField(this.ui, "mySearchProgressIndicator");
    }

    /**
     * 等待 idea 默认的搜索功能执行完毕
     *
     * @param timeout 超时时间，单位毫秒
     */
    public void waitFor(long timeout) {
        ProgressIndicator indicator = this.getProgressIndicator();
        Throwable e = Dates.waitFor(() -> indicator != null && indicator.isRunning() && !indicator.isCanceled(), 100, timeout);
        if (e != null && log.isErrorEnabled()) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public void switchToTab(String tabID) {
        if (this.ui == null) {
            return;
        }

        this.ui.switchToTab(tabID);
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public String getAdvertiserText(Object obj) {
        return this.extendedInfoText;
    }

    public static class StatusBar {
        private final ArtifactSearchStatusMessageType type;
        private final String message;

        public StatusBar(ArtifactSearchStatusMessageType type, String message) {
            this.type = type;
            this.message = message;
        }

        public ArtifactSearchStatusMessageType getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }
    }
}
