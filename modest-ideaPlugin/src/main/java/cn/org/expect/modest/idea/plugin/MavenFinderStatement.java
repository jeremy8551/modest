package cn.org.expect.modest.idea.plugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.org.expect.util.StringUtils;
import com.intellij.openapi.diagnostic.Logger;

public class MavenFinderStatement {
    private static final Logger log = Logger.getInstance(MavenFinderStatement.class);

    public final static MavenFinderStatement INSTANCE = new MavenFinderStatement();

    protected final Map<String, MavenFinderResult> map;

    protected volatile MavenFinderResult last;

    protected final MavenFinderQuery query;

    protected MavenFinderStatement() {
        this.map = new ConcurrentHashMap<String, MavenFinderResult>();
        this.query = new MavenFinderQuery();
    }

    public synchronized MavenFinderResult query(String pattern) {
        String patternFinal = MavenFinderPattern.parse(pattern);
        if (StringUtils.isBlank(patternFinal)) {
            return null;
        }

        log.warn("search Pattern: " + patternFinal);
        MavenFinderResult result = this.map.get(patternFinal);
        if (result == null) {
            List<MavenFinderItem> list = null;
            try {
                list = this.query.execute(StringUtils.trimBlank(StringUtils.replaceAll(pattern, ".", "%2E")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (list != null && !list.isEmpty()) {
                result = new MavenFinderResult(patternFinal, list);
                this.map.put(result.getPattern(), result);
            }
        }

        if (result == null) {
            log.warn("search Pattern: " + patternFinal + ", result is null!");
        } else {
            this.last = result;
            log.warn("search Pattern: " + patternFinal + ", Size: " + result.getItems().size() + ", List: " + StringUtils.toString(result.getItems()));
        }

        return result;
    }

    /**
     * 返回上一次查询结果
     *
     * @return
     */
    public MavenFinderResult last() {
        return this.last;
    }

    public MavenFinderResult getResult(String pattern) {
        String patternFinal = MavenFinderPattern.parse(pattern);
        return this.map.get(patternFinal);
    }
}
