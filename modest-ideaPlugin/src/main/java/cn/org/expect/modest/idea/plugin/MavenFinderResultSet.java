package cn.org.expect.modest.idea.plugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.org.expect.util.StringUtils;

public class MavenFinderResultSet {

    public final static MavenFinderResultSet INSTANCE = new MavenFinderResultSet();

    protected volatile Map<String, MavenFinderResult> map;

    protected volatile MavenFinderResult last;

    private MavenFinderResultSet() {
        this.map = new ConcurrentHashMap<String, MavenFinderResult>();
    }

    public synchronized MavenFinderResult query(String pattern) {
        MavenFinderResult result = this.map.get(pattern);
        if (result == null) {
            List<MavenFinderItem> list = null;
            try {
                MavenFinderQuery finder = new MavenFinderQuery();
                String newPattern = StringUtils.replaceAll(pattern, ".", "%2E");
                list = finder.execute(StringUtils.trimBlank(newPattern));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (list != null && !list.isEmpty()) {
                result = new MavenFinderResult(pattern).addAll(list);
                this.map.put(result.getPattern(), result);
            }
        }

        this.last = result;
        return result;
    }

    /**
     * 返回上一次查询结果
     *
     * @return
     */
    public MavenFinderResult getLast() {
        return this.last;
    }

    public MavenFinderResult get(String pattern) {
        MavenFinderResult result = this.map.get(pattern);
        this.last = result;
        return result;
    }
}
