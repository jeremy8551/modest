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
        CharTable cb = new CharTable();
        String title = ResourcesUtils.getMessage("database.stdout.message021");
        String[] array = StringUtils.split(title, ';');
        for (String str : array) {
            cb.addTitle(str);
        }

        Set<String> keySet = this.map.keySet();
        for (String key : keySet) {
            DatabaseType type = this.map.get(key);
            cb.addCell(type.getName());
            cb.addCell(new StringBuilder().append(type.getTextPrefix()).append(type.getTextSuffix()));
            cb.addCell(type.getExpression());
            cb.addCell(type.getScale());
            cb.addCell(type.getMaxScale());
            cb.addCell(type.getMinScale());
            cb.addCell(type.getNullAble());
            cb.addCell(type.getRadix());
            cb.addCell(type.getPrecision());
            cb.addCell(type.getSearchable());
            cb.addCell(type.getUnsigned());
            cb.addCell(type.getLocalName());
        }

        return cb.toString(CharTable.Style.DB2);
    }
}
