package cn.org.expect.maven.repository.impl;

public enum ArtifactSearchResultType {

    /** 每次查询所有记录 */
    ALL,

    /** 分页查询 */
    LIMIT_PAGE, //

    /** 没有总结果记录 */
    NO_TOTAL
}
