package cn.org.expect.intellij.idea.plugin.maven.settings;

import javax.swing.*;

public enum DownloadWay {

    CENTRAL("central"), MAVEN("maven"), ALIYUN("aliyun"), HUAWEI("huawei"), TENCENT("tencent");

    private final String name;

    DownloadWay(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public static SelectOption[] toOptions() {
        DownloadWay[] values = values();
        SelectOption[] array = new SelectOption[values.length];
        for (int i = 0; i < values.length; i++) {
            DownloadWay value = values[i];
            array[i] = new SelectOption(value.name);
        }
        return array;
    }

    public static DownloadWay getSelectedItem(JComboBox<SelectOption> comboBox) {
        SelectOption selected = (SelectOption) comboBox.getSelectedItem();
        if (selected == null) {
            return null;
        }

        for (DownloadWay way : values()) {
            if (way.name.equals(selected.getKey())) {
                return way;
            }
        }
        return null;
    }
}
