package cn.org.expect.intellijidea.plugin.maven.search;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.MavenArtifactSet;
import cn.org.expect.intellijidea.plugin.maven.MavenFinder;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderIcon;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderMessage;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderPattern;
import cn.org.expect.intellijidea.plugin.maven.db.MavenArtifactDatabase;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;
import org.jetbrains.annotations.NotNull;

public class MavenRepositorySearchPattern extends MavenRepositorySearch<MavenRepositorySearchPattern.QueueElement> {

    public MavenRepositorySearchPattern() {
        super();
    }

    /**
     * 添加搜索任务
     *
     * @param mavenFinder Maven工具
     * @param pattern     字符串
     */
    public void search(MavenFinder mavenFinder, String pattern) {
        if (StringUtils.isNotBlank(pattern)) {
            String message = MavenFinderMessage.SEARCHING_PATTERN.fill(StringUtils.escapeLineSeparator(pattern));
            mavenFinder.setAdvertiser(message, MavenFinderIcon.BOTTOM_WAITING);
            mavenFinder.getContext().setSelectList(null);
            this.add(new QueueElement(mavenFinder, pattern));
        }
    }

    private void add(QueueElement pattern) {
        this.getRepository().terminate();

        try {
            this.queue.put(pattern);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public void run() {
        log.warn("start MavenFinder Search Thread ..");
        while (this.notTerminate) {
            try {
                QueueElement take = this.queue.take();
                String pattern = take.pattern;
                MavenFinder mavenFinder = take.mavenFinder;

                // 设置未返回结果时显示的内容与广告栏信息
                mavenFinder.setReminderText(MavenFinderMessage.SEARCHING.getMessage());
                mavenFinder.setAdvertiser(MavenFinderMessage.SEARCHING_PATTERN.fill(StringUtils.escapeLineSeparator(pattern)), MavenFinderIcon.BOTTOM_WAITING);

                // 如果线程等待期间又添加了其他查询条件，则直接执行最后一个查询条件
                Dates.sleep(mavenFinder.getContext().getInputIntervalTime());

                // 查询
                if (this.queue.isEmpty()) {
                    MavenArtifactDatabase database = mavenFinder.getDatabase();
                    MavenArtifactSet result = this.query(database, pattern);
                    if (result == null) {
                        String message = MavenFinderMessage.FAIL_SEND_REQUEST.getMessage();
                        mavenFinder.setReminderText(message);
                        mavenFinder.setAdvertiser(message, MavenFinderIcon.BOTTOM_ERROR);
                    } else if (result.size() == 0) {
                        String message = MavenFinderMessage.NOTHING_FOUND.getMessage();
                        mavenFinder.setReminderText(message);
                        mavenFinder.setAdvertiser(message, MavenFinderIcon.BOTTOM);
                    } else {
                        mavenFinder.getContext().setPatternSearchResult(result);
                        mavenFinder.repaint(result);
                    }
                }
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    public MavenArtifactSet query(@NotNull MavenArtifactDatabase database, String pattern) {
        String patternFinal = MavenFinderPattern.parse(pattern);
        if (StringUtils.isBlank(patternFinal)) {
            return null;
        }

        log.warn("search Pattern: " + patternFinal);
        MavenArtifactSet result = database.select(patternFinal);
        if (result == null || result.size() == 0) {
            try {
                List<MavenArtifact> list;
                if (MavenFinderPattern.isExtraSearch(patternFinal)) {
                    String[] array = StringUtils.split(patternFinal, ':');
                    List<MavenArtifact> extraList = this.getRepository().query(array[0], array[1]);
                    if (extraList.size() >= 2) {
                        database.insert(array[0], array[1], extraList);
                        MavenArtifact last = extraList.get(extraList.size() - 1);
                        list = new ArrayList<MavenArtifact>();
                        list.add(last);
                    } else {
                        list = extraList;
                    }
                } else {
                    list = this.getRepository().query(StringUtils.trimBlank(StringUtils.replaceAll(patternFinal, ".", "%2E")));
                }

                result = database.insert(patternFinal, list);
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }

        if (result == null) {
            log.warn("search Pattern: " + patternFinal + ", result is null!");
            return null;
        } else {
            log.warn("search Pattern: " + patternFinal + ", Size: " + result.size() + ", List: " + StringUtils.toString(result.getArtifacts()));
            return result;
        }
    }

    protected static class QueueElement {
        MavenFinder mavenFinder;
        String pattern;

        public QueueElement(MavenFinder mavenFinder, String pattern) {
            this.mavenFinder = mavenFinder;
            this.pattern = pattern;
        }
    }
}
