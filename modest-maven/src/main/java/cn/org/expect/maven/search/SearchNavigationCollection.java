package cn.org.expect.maven.search;

public interface SearchNavigationCollection extends ArtifactSearchAware {

    /**
     * 返回总记录数
     *
     * @return 总记录数
     */
    int getFoundNumber();

    /**
     * 是否有未读数据
     *
     * @return true表示还有未读数据，false表示已全部读取
     */
    boolean isHasMore();

    /**
     * Maven 工件个数
     *
     * @return 工件个数
     */
    int size();

    /**
     * 返回指定位置上的导航记录
     *
     * @param index 位置信息，从0开始
     * @return 导航记录
     */
    SearchNavigation get(int index);
}
