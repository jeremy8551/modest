package cn.org.expect.modest.idea.plugin.action;

import javax.swing.*;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

public class MavenFinderAnAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if (project != null) {
            JPanel panel = new JPanel();

            // 创建文件选择框描述符
            FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
            descriptor.setTitle("请选择一个文件!");

            //过滤文件
            descriptor.withFileFilter(file -> {
                String extension = file.getExtension();
                return extension != null && (extension.equals("xls") || extension.equals("xlsx") || extension.equals("XLS") || extension.equals("XLSX"));
            });

            // 添加文件选择框到面板
            VirtualFile[] virtualFiles = FileChooser.chooseFiles(descriptor, project, null);
            for (VirtualFile virtualFile : virtualFiles) {
                String filename = virtualFile.getName();
                System.out.println(" =======>>>>>>> you select a file :" + filename);
                Messages.showMessageDialog(project, "你选择了:" + filename, "tips", Messages.getInformationIcon());

                JLabel label = new JLabel(virtualFile.getPath());
                panel.add(label);
            }

            // 显示带有文件选择框的自定义弹窗
            Messages.wrapToScrollPaneIfNeeded(panel, 6, 6);
        }
    }
}
