package cn.org.expect.modest.idea.plugin;

import javax.swing.*;

public class LabelExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JPanel with Text");
        JPanel panel = new JPanel();

        // 创建一个 JLabel 并设置文本
        JLabel label = new JLabel("This is a JLabel text");
        panel.add(label);  // 将 JLabel 添加到 JPanel 上

        // 设置窗口基本属性
        frame.add(panel);
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

