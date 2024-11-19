package cn.org.expect.intellij.idea.plugin.maven;

import javax.swing.*;

import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.search.MavenSearchAdvertiser;
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
    private final static Log log = LogFactory.getLog(IdeaSearchUI.class);

    protected volatile SearchEverywhereUI ui;

    /** 状态栏信息 */
    private volatile String advertiserText;

    public IdeaSearchUI() {
    }

    public SearchEverywhereUI getSearchEverywhereUI() {
        return this.ui;
    }

    public void setSearchEverywhereUI(SearchEverywhereUI ui) {
        this.ui = ui;
    }

    public String getSelectedTabID() {
        return this.ui.getSelectedTabID();
    }

    public JTextField getSearchField() {
        return this.ui.getSearchField();
    }

    public JBLabel getStatusBar() {
        ExtendedInfoComponent info = JavaDialectFactory.get().getField(ui, "myExtendedInfoComponent");
        return JavaDialectFactory.get().getField(info, "text");
    }

    public void setStatusbarText(MavenSearchAdvertiser type, String message) {
        try {
            Icon icon = MavenSearchUtils.getIcon(type);
            String fontColor = MavenSearchAdvertiser.ERROR == type ? "red" : "orange";
            String text = new MessageFormatter("<html><span style='color:{};'>{}</span></html>").fill(fontColor, message);

            // 检查注册项是否启用，为true，表示使用扩展模式作为状态栏
            if (Registry.is("search.everywhere.footer.extended.info")) {
                this.advertiserText = text;

                // 更新状态栏中的文本信息
                ExtendedInfoComponent info = JavaDialectFactory.get().getField(ui, "myExtendedInfoComponent");
                JBLabel label = JavaDialectFactory.get().getField(info, "text");
                label.setIcon(icon);
                label.setText(text);
                return;
            }

            Advertiser advertiser = JavaDialectFactory.get().getField(ui, "myHintLabel");
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

    public JBList<Object> getJBList() {
        return JavaDialectFactory.get().getField(this.ui, "myResultsList");
    }

    public SearchListModel getListModel() {
        return JavaDialectFactory.get().getField(this.ui, "myListModel");
    }

    public ProgressIndicator getProgressIndicator() {
        return JavaDialectFactory.get().getField(ui, "mySearchProgressIndicator");
    }

    public void switchToTab(String tabID) {
        this.ui.switchToTab(tabID);
    }

    public String getAdvertiserText(Object obj) {
        return this.advertiserText;
    }
}
