package cn.org.expect.database.internal;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.org.expect.collection.CaseSensitivMap;
import cn.org.expect.database.DatabaseType;
import cn.org.expect.database.DatabaseTypeSet;
import cn.org.expect.util.CharTable;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

public class StandardDatabaseTypes implements DatabaseTypeSet {

    private Map<String, DatabaseType> map;

    public StandardDatabaseTypes() {
        this.map = new CaseSensitivMap<DatabaseType>();
    }

    public void put(String name, DatabaseType type) {
        this.map.put(name, type);
    }

    public DatabaseType get(String name) {
        return this.map.get(name);
    }

    public DatabaseType get(int sqltype) {
        Set<Entry<String, DatabaseType>> set = this.map.entrySet();
        for (Entry<String, DatabaseType> entry : set) {
            DatabaseType type = entry.getValue();
            if (type.getSqlType() == sqltype) {
                return type;
            }
        }
        return null;
    }

    public String toString() {
        CharTable table = new CharTable();
        String[] array = ResourcesUtils.getMessageArray("database.stdout.message021");
        for (String str : array) {
            table.addTitle(str);
        }

        Set<String> keySet = this.map.keySet();
        for (String key : keySet) {
            DatabaseType type = this.map.get(key);
            table.addCell(type.getName());
            table.addCell(new StringBuilder().append(type.getTextPrefix()).append(type.getTextSuffix()));
            table.addCell(type.getExpression());
            table.addCell(type.getFixedPrecScale());
            table.addCell(type.getMaxScale());
            table.addCell(type.getMinScale());
            table.addCell(type.getNullAble());
            table.addCell(type.getRadix());
            table.addCell(type.getPrecision());
            table.addCell(type.getSearchable());
            table.addCell(type.getUnsigned());
            table.addCell(type.getLocalName());
        }

        return table.toString(CharTable.Style.DB2);
    }
}
