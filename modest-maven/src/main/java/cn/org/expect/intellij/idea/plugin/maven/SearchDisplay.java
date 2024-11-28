package cn.org.expect.intellij.idea.plugin.maven;

import java.awt.*;
import java.awt.event.MouseListener;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationHead;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationItem;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.ui.components.JBList;

public class SearchDisplay extends SearchDisplayModel {

    protected JBList<Object> list;

    public SearchDisplay(JBList<Object> list, SearchListModel model) {
        super(model);
        this.list = list;
    }

    public void addMouseListener(MouseListener listener) {
        this.list.addMouseListener(listener);
    }

    public int locationToIndex(Point location) {
        return this.list.locationToIndex(location);
    }

    public void setSelectedIndex(int index) {
        this.list.setSelectedIndex(index);
    }

    public int getX() {
        return this.list.getX();
    }

    public Rectangle getCellBounds(int index0, int index1) {
        return this.list.getCellBounds(index0, index1);
    }

    public void showMenu(JPopupMenu menu, int x, int y) {
        menu.show(this.list, x, y);
    }

    public void setProgress(String message) {
        try {
            this.list.setEmptyText(message);
            this.list.repaint();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public void paint() {
        try {
            this.list.revalidate();
            this.list.repaint();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 选中记录
     */
    public void select(SearchNavigationHead selectHead, SearchNavigationItem selectItem) {
        int selectedIndex = -1;

        SearchListModel listModel = this.model;
        if (selectHead != null) {
            for (int i = 0, size = listModel.getSize(); i < size; i++) {
                Object object = listModel.getElementAt(i);
                if (object instanceof SearchNavigationHead) {
                    SearchNavigationHead head = (SearchNavigationHead) object;
                    if (selectHead.getArtifact().equals(head.getArtifact())) {
                        selectedIndex = i;
                        break;
                    }
                }
            }
        }

        if (selectItem != null) {
            for (int i = 0, size = listModel.getSize(); i < size; i++) {
                Object object = listModel.getElementAt(i);
                if (object instanceof SearchNavigationItem) {
                    SearchNavigationItem item = (SearchNavigationItem) object;
                    if (selectItem.getArtifact().equals(item.getArtifact())) {
                        selectedIndex = i;
                        break;
                    }
                }
            }
        }

        this.select(selectedIndex);
    }

    public void select(int index) {
        try {
            if (index == -1) {
                this.list.clearSelection();
            } else {
                this.list.setSelectedIndex(index); // 选中
                this.list.ensureIndexIsVisible(index); // 设置 JList 显示 index 位置
            }
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public void setVisibleRange(Rectangle visibleRect) {
        if (visibleRect == null) {
            return;
        }
        
        int index = -1;
        for (int i = 0; i < this.model.getSize(); i++) {
            Rectangle itemRect = list.getCellBounds(i, i); // 获取第 i 项的矩形区域
            if (itemRect != null && itemRect.intersects(visibleRect)) {
                index = i;
            }
        }

        if (index != -1) {
            this.list.ensureIndexIsVisible(index); // 设置 JList 显示 index 位置
        }
    }

    public Rectangle getVisibleRect() {
        return this.list.getVisibleRect();
    }
}
