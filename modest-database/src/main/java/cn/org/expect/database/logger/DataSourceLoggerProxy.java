package cn.org.expect.database.logger;

import javax.sql.DataSource;

/**
 * 数据库连接池代理接口
 *
 * @author jeremy8551@qq.com
 */
public interface DataSourceLoggerProxy extends DataSource {

    /**
     * 返回被代理的数据库连接池对象
     *
     * @return 数据库连接池
     */
    DataSource getOrignalDataSource();

}
