package cn.org.expect.maven.concurrent;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.impl.SimpleMavenSearchResult;
import cn.org.expect.maven.search.MavenSearch;
import cn.org.expect.maven.search.MavenSearchAdvertiser;
import cn.org.expect.maven.search.MavenSearchMessage;
import cn.org.expect.maven.search.MavenSearchUtils;
import cn.org.expect.maven.search.db.MavenSearchDatabase;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

public class MavenSearchPatternJob extends MavenSearchJob {

    protected final String pattern;

    public MavenSearchPatternJob(String pattern) {
        super();
        this.pattern = Ensure.notNull(pattern);
    }

    public String getPattern() {
        return this.pattern;
    }

    public int execute() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("{} search pattern: {}", this.getName(), pattern);
        }

        MavenSearch search = this.getSearch();
        MavenSearchResult result = this.query(search, pattern);
        if (this.terminate) {
            return 0;
        }

        // 查询失败
        if (result == null) {
            String message = MavenSearchMessage.get("maven.search.send.url.fail");
            search.setProgressText(message);
            search.setStatusbarText(MavenSearchAdvertiser.ERROR, message);
            return 0;
        }

        // 查询结果为空
        if (result.size() == 0) {
            String message = MavenSearchMessage.get("maven.search.noting.found");
            search.setProgressText(message);
            search.setStatusbarText(MavenSearchAdvertiser.NORMAL, message);
            return 0;
        }

        // 保存查询结果
        search.getContext().setSearchResult(result);
        search.showSearchResult(result);
        return 0;
    }

    private MavenSearchResult query(MavenSearch search, String pattern) {
        String patternFinal = MavenSearchUtils.parse(pattern);
        if (StringUtils.isBlank(patternFinal)) {
            return null;
        }

        MavenSearchDatabase database = search.getDatabase();
        MavenSearchResult result = database.select(patternFinal);
        try {
            if (result == null || result.size() == 0) {
                if (!MavenSearchUtils.isExtraSearch(patternFinal)) {
                    MavenSearchResult resultSet = this.getRemoteRepository().query(StringUtils.trimBlank(StringUtils.replaceAll(patternFinal, ".", "%2E")), 1);
                    if (resultSet == null) {
                        return null;
                    } else {
                        database.insert(patternFinal, resultSet);
                        return resultSet;
                    }
                }

                // 精确查询
                String[] array = StringUtils.split(patternFinal, ':');
                MavenSearchResult extraResult = this.getRemoteRepository().query(array[0], array[1]);
                if (extraResult == null) {
                    return null;
                }

                if (extraResult.size() >= 2) {
                    database.insert(array[0], array[1], extraResult);
                    MavenArtifact last = extraResult.getList().get(0);
                    List<MavenArtifact> list = new ArrayList<>();
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
