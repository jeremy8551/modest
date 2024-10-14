package cn.org.expect.modest.idea.plugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MavenFinderResultSet {

    protected volatile Map<String, MavenFinderResult> map;

    protected volatile MavenFinderResult result;

    public MavenFinderResultSet() {
        this.map = new ConcurrentHashMap<String, MavenFinderResult>();
    }

    public void query(String pattern) {
        MavenFinderResult result = this.map.get(pattern);
        if (result == null) {
            List<MavenFinderItem> list = null;
            try {
                MavenFinderQuery finder = new MavenFinderQuery();
                list = finder.execute(pattern);
            } catch (Exception e) {
                e.printStackTrace();
            }

            result = new MavenFinderResult(pattern).addAll(list);
            this.map.put(result.getPattern(), result);
            this.result = result;
        }
    }

    /**
     * 返回上一次查询结果
     *
     * @return
     */
    public MavenFinderResult getLast() {
        return this.result;
    }
}
