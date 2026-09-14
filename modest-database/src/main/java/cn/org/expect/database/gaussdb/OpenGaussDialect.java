package cn.org.expect.database.gaussdb;

import cn.org.expect.ioc.annotation.EasyBean;

/**
 * openGauss JDBC URL 的高斯数据库方言别名。
 */
@EasyBean(value = "opengauss")
public class OpenGaussDialect extends GaussDBDialect {
}
