package cn.org.expect.intellij.idea.plugin.maven.concurrent;

/**
 * 表示使用 EDT 线程执行任务 <br>
 * 如果想要使用官方的 EDT 线程，需要让 task 实现 {@linkplain EDTJob} 接口 <br>
 * 想要刷新 Idea 查询结果的 JList，需要与 Idea 使用同一个数据库连接池：{@link com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI#rebuildListAlarm}，否则多线程刷新 JList 时会出现混乱
 */
public interface EDTJob {
}
