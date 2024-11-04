package cn.org.expect.intellijidea.plugin.maven.search;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.MavenFinder;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderIcon;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderMessage;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderPattern;
import cn.org.expect.intellijidea.plugin.maven.MavenSearchResult;
import cn.org.expect.intellijidea.plugin.maven.db.MavenArtifactDatabase;
import cn.org.expect.intellijidea.plugin.maven.impl.SimpleMavenSearchResult;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 监听并执行：用户输入的模糊查询
 */
public class MavenRepositoryInputSearch extends AbstractMavenRepositorySearch<PatternElement> {

    public MavenRepositoryInputSearch() {
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
            this.add(new PatternElement(mavenFinder, pattern));
        }
    }

    private void add(PatternElement pattern) {
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
                PatternElement take = this.queue.take();
                String pattern = take.getPattern();
                MavenFinder mavenFinder = take.getMavenFinder();

                // 设置未返回结果时显示的内容与广告栏信息
                mavenFinder.setReminderText(MavenFinderMessage.SEARCHING.getMessage());
                mavenFinder.setAdvertiser(MavenFinderMessage.SEARCHING_PATTERN.fill(StringUtils.escapeLineSeparator(pattern)), MavenFinderIcon.BOTTOM_WAITING);

                // 如果线程等待期间又添加了其他查询条件，则直接执行最后一个查询条件
                Dates.sleep(mavenFinder.getContext().getInputIntervalTime());

                // 查询
                if (this.queue.isEmpty()) {
                    MavenArtifactDatabase database = mavenFinder.getDatabase();
                    MavenSearchResult result = this.query(database, pattern);
                    if (!this.notTerminate) {
                        continue;
                    } else if (result == null) {
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

    private MavenSearchResult query(@NotNull MavenArtifactDatabase database, String pattern) {
        String patternFinal = MavenFinderPattern.parse(pattern);
        if (StringUtils.isBlank(patternFinal)) {
            return null;
        }

        log.warn("search Pattern: " + patternFinal);
        MavenSearchResult result = database.select(patternFinal);
        try {
            if (result == null || result.size() == 0) {
                if (!MavenFinderPattern.isExtraSearch(patternFinal)) {
                    MavenSearchResult resultSet = this.getRepository().query(StringUtils.trimBlank(StringUtils.replaceAll(patternFinal, ".", "%2E")), 1);
                    if (resultSet == null) {
                        return null;
                    } else {
                        return database.insert(patternFinal, resultSet);
                    }
                }

                // 精确查询
                String[] array = StringUtils.split(patternFinal, ':');
                MavenSearchResult extraResult = this.getRepository().query(array[0], array[1]);
                if (extraResult == null) {
                    return null;
                } else if (extraResult.size() >= 2) {
                    database.insert(array[0], array[1], extraResult);
                    MavenArtifact last = extraResult.getList().get(0);
                    List<MavenArtifact> list = new ArrayList<MavenArtifact>();
                    list.add(last);
                    SimpleMavenSearchResult newResult = new SimpleMavenSearchResult(list, 1, 1);
                    return database.insert(patternFinal, newResult);
                } else {
                    return database.insert(patternFinal, extraResult);
                }
            }
            return result;
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
            return null;
        }
    }
}
