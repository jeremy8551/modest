package cn.org.expect.maven.intellij.idea;

import com.intellij.ide.ApplicationInitializedListener;

public class ApplicationInitializedListenerImpl implements ApplicationInitializedListener {

    @Override
    public void componentsInitialized() {
        // 插件在 IDEA 启动后要执行的逻辑
        System.out.println("IDEA 已启动，执行初始化操作...");
    }
}
