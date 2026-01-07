package cn.org.expect.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库支持的数据类型 <br>
 * <br>
 * precision意为“精密度、精确”，表示该字段的有效数字位数了。 <br>
 * scale意为“刻度、数值范围”，表示该字段的小数位数。 <br>
 * radix：可选参数，数字基数，可以理解为进制，范围为2~36 <br>
 * <br>
 * 举个简单的例子 <br>
 * 123.45：precision = 5 ，scale = 2 <br>
 * precision 数据长度 <br>
 * scale 小数长度 <br>
 * <br>
 * <br>
 * sql boolean 0/1表示true或False <br>
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-03-23
 */
public interface DatabaseTypeFactory {

    /**
     * 创建一个字段类型信息
     *
     * @param resultSet 查询结果集
     * @return 字段类型信息
     * @throws SQLException 数据库错误
     */
    DatabaseType newInstance(ResultSet resultSet) throws SQLException;
}
