package cn.org.expect.maven.search;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.impl.SimpleMavenSearchResult;
import cn.org.expect.maven.search.db.MavenSearchDatabase;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;

/**
 * 监听并执行：用户输入的模糊查询
 */
public class SearchInputThread extends AbstractSearchThread<SearchElementPattern> {
    protected final static Log log = LogFactory.getLog(SearchInputThread.class);

    public SearchInputThread() {
        super();
    }

    /**
     * 执行模糊搜索
     *
     * @param search  搜索接口
     * @param pattern 字符串
     */
    public void search(MavenSearch search, String pattern) {
        if (StringUtils.isNotBlank(pattern)) {
            search.setRunningText(MavenSearchAdvertiser.RUNNING, MavenSearchMessage.SEARCHING_PATTERN.fill(StringUtils.escapeLineSeparator(pattern)));
            this.add(new SearchElementPattern(search, pattern));
        }
    }

    private void add(SearchElementPattern pattern) {
        this.getRepository().terminate();

        try {
            this.queue.put(pattern);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public void run() {
        log.info("start " + SearchInputThread.class.getSimpleName() + " ..");
        while (this.notTerminate) {
            try {
                SearchElementPattern take = this.queue.take();
                String pattern = take.getPattern();
                MavenSearch search = take.getSearch();

                // 设置未返回结果时显示的内容与广告栏信息
                search.setWaitingText(MavenSearchMessage.SEARCHING.getMessage());
                search.setRunningText(MavenSearchAdvertiser.RUNNING, MavenSearchMessage.SEARCHING_PATTERN.fill(StringUtils.escapeLineSeparator(pattern)));

                // 如果线程等待期间又添加了其他查询条件，则直接执行最后一个查询条件
                Dates.sleep(search.getContext().getInputIntervalTime());

                // 查询
                if (this.queue.isEmpty()) {
                    MavenSearchDatabase database = search.getDatabase();
                    MavenSearchResult result = this.query(database, pattern);
                    if (!this.notTerminate) {
                        continue;
                    } else if (result == null) {
                        String message = MavenSearchMessage.FAIL_SEND_REQUEST.getMessage();
                        search.setWaitingText(message);
                        search.setRunningText(MavenSearchAdvertiser.ERROR, message);
                    } else if (result.size() == 0) {
                        String message = MavenSearchMessage.NOTHING_FOUND.getMessage();
                        search.setWaitingText(message);
                        search.setRunningText(MavenSearchAdvertiser.NORMAL, message);
                    } else {
                        search.getContext().setSearchResult(result);
                        search.repaint(result);
                    }
                }
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    private MavenSearchResult query(MavenSearchDatabase database, String pattern) {
        String patternFinal = MavenSearchUtils.parse(pattern);
        if (StringUtils.isBlank(patternFinal)) {
            return null;
        }

        log.info("search Pattern: " + patternFinal);
        MavenSearchResult result = database.select(patternFinal);
        try {
            if (result == null || result.size() == 0) {
                if (!MavenSearchUtils.isExtraSearch(patternFinal)) {
                    MavenSearchResult resultSet = this.getRepository().query(StringUtils.trimBlank(StringUtils.replaceAll(patternFinal, ".", "%2E")), 1);
                    if (resultSet == null) {
                        return null;
                    } else {
                        database.insert(patternFinal, resultSet);
                        return resultSet;
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
                    database.insert(patternFinal, newResult);
                    return newResult;
                } else {
                    database.insert(patternFinal, extraResult);
                    return extraResult;
                }
            }
            return result;
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
            return null;
        }
    }
}
