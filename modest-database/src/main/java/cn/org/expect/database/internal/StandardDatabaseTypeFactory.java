package cn.org.expect.database.internal;

import java.sql.ResultSet;
import java.sql.SQLException;

import cn.org.expect.database.DatabaseType;
import cn.org.expect.database.DatabaseTypeFactory;
import cn.org.expect.database.Jdbc;

public class StandardDatabaseTypeFactory implements DatabaseTypeFactory {

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
        type.setUnsignedAttribute(Jdbc.getInt(resultSet, "UNSIGNED_ATTRIBUTE"));
        type.setCaseSesitive(Jdbc.getInt(resultSet, "CASE_SENSITIVE"));
        type.setFixedPrecScale(Jdbc.getInt(resultSet, "FIXED_PREC_SCALE"));
        type.setAutoIncrement(Jdbc.getInt(resultSet, "AUTO_INCREMENT"));
        type.setLocalTypeName(Jdbc.getString(resultSet, "LOCAL_TYPE_NAME"));
        type.setMinimumScale(Jdbc.getInt(resultSet, "MINIMUM_SCALE"));
        type.setMaximumScale(Jdbc.getInt(resultSet, "MAXIMUM_SCALE"));
        type.setNumberPrecRadix(Jdbc.getInt(resultSet, "NUM_PREC_RADIX"));
        type.setSqlDataType(Jdbc.getInt(resultSet, "SQL_DATA_TYPE"));
        type.setSqlDateTimeSub(Jdbc.getInt(resultSet, "SQL_DATETIME_SUB"));
        return type;
    }
}
