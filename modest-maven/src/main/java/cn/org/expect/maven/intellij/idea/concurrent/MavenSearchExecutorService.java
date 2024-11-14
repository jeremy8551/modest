package cn.org.expect.maven.intellij.idea.concurrent;

import java.util.concurrent.ExecutorService;

import com.intellij.util.Alarm;

public interface MavenSearchExecutorService extends ExecutorService {

    /**
     * 设置 SearchEverywhere UI 使用的线程池
     *
     * @param service 线程池
     */
    void setSearchEverywhereService(Alarm service);
}
