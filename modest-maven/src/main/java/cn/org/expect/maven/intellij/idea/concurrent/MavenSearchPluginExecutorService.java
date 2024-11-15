package cn.org.expect.maven.intellij.idea.concurrent;

import cn.org.expect.maven.concurrent.MavenSearchExecutorService;
import com.intellij.util.Alarm;

public interface MavenSearchPluginExecutorService extends MavenSearchExecutorService {

    /**
     * 设置 SearchEverywhere UI 使用的线程池
     *
     * @param service 线程池
     */
    void setSearchEverywhereService(Alarm service);
}
