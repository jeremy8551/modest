package cn.org.expect.database.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import cn.org.expect.database.DatabaseType;
import cn.org.expect.database.DatabaseTypeFactory;
import cn.org.expect.database.Jdbc;
import cn.org.expect.database.internal.StandardDatabaseType;

public class MysqlDatabaseTypeFactory implements DatabaseTypeFactory {

    public DatabaseType newInstance(ResultSet resultSet) throws SQLException {
        StandardDatabaseType type = new StandardDatabaseType();
        type.setName(resultSet.getString("TYPE_NAME"));
        type.setSqlType(Jdbc.getInt(resultSet, "DATA_TYPE"));
        type.setPrecision(Jdbc.getInt(resultSet, "PRECISION"));
        type.setLiteralPrefix(Jdbc.getString(resultSet, "LITERAL_PREFIX"));
        type.setLiteralSuffix(Jdbc.getString(resultSet, "LITERAL_SUFFIX"));
        type.setCreateParams(Jdbc.getString(resultSet, "CREATE_PARAMS"));
        type.setNullAble(Jdbc.getInt(resultSet, "NULLABLE"));
        type.setSearchable(Jdbc.getInt(resultSet, "SEARCHABLE"));
        type.setUnsignedAttribute(Jdbc.getBoolean(resultSet, "UNSIGNED_ATTRIBUTE") ? 0 : 1);
        type.setCaseSesitive(Jdbc.getBoolean(resultSet, "CASE_SENSITIVE") ? 1 : 0);
        type.setFixedPrecScale(Jdbc.getBoolean(resultSet, "FIXED_PREC_SCALE") ? 1 : 0);
        type.setAutoIncrement(Jdbc.getBoolean(resultSet, "AUTO_INCREMENT") ? 1 : 0);
        type.setLocalTypeName(Jdbc.getString(resultSet, "LOCAL_TYPE_NAME"));
        type.setMinimumScale(Jdbc.getInt(resultSet, "MINIMUM_SCALE"));
        type.setMaximumScale(Jdbc.getInt(resultSet, "MAXIMUM_SCALE"));
        type.setNumberPrecRadix(Jdbc.getInt(resultSet, "NUM_PREC_RADIX"));
        type.setSqlDataType(Jdbc.getInt(resultSet, "SQL_DATA_TYPE"));
        type.setSqlDateTimeSub(Jdbc.getInt(resultSet, "SQL_DATETIME_SUB"));
        return type;
    }
}
