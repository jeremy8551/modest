package cn.org.expect.increment;

import java.util.Date;

import cn.org.expect.database.DatabaseTableColumn;
import cn.org.expect.database.DatabaseTableColumnList;
import cn.org.expect.expression.Analysis;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyBeanFactory;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;

@EasyBean
public class IncrementReplaceFactory implements EasyBeanFactory<IncrementReplace> {

    public IncrementReplace build(EasyContext context, Object... args) throws Exception {
        Analysis analysis = ArrayUtils.indexOf(args, Analysis.class, 0);
        String str = ArrayUtils.indexOf(args, String.class, 0); // 1:date-
        DatabaseTableColumnList columns = ArrayUtils.indexOf(args, DatabaseTableColumnList.class, 0);

        char mapdel = (analysis == null) ? ':' : analysis.getMapdel();
        String[] attributes = StringUtils.split(str, mapdel);
        String field = attributes[0];
        String value = attributes[1];

        if (StringUtils.startsWith(value, "date-", 0, true, false)) {
            return new DateReplace(columns, field, value.substring("date-".length()));
        } else if (value.equalsIgnoreCase("uuid")) {
            return new UUIDReplace(columns, field);
        } else { // 自定义
            String[] beans = StringUtils.split(value, '/');
            EasyBeanEntry entry = context.getBeanEntry(IncrementReplace.class, beans[0]);
            if (entry == null) {
                return new StandardReplace(columns, field, value);
            } else {
                return context.newInstance(entry.getType());
            }
        }
    }

    /**
     * 将字段替换成日期
     */
    protected static class DateReplace implements IncrementReplace {

        private int position;
        private String pattern;

        public DateReplace(DatabaseTableColumnList list, String nameOrPosition, String value) {
            DatabaseTableColumn column;
            if (list != null && (column = list.getColumn(nameOrPosition)) != null) {
                this.position = column.getPosition();
            } else if (StringUtils.isNumber(nameOrPosition)) {
                this.position = Integer.parseInt(nameOrPosition);
            } else {
                throw new IllegalArgumentException(nameOrPosition);
            }
            this.pattern = value;
        }

        public int getPosition() {
            return position;
        }

        public String getValue() {
            return Dates.format(new Date(), this.pattern);
        }
    }

    /**
     * 将字段替换成 UUID 值
     */
    protected static class UUIDReplace implements IncrementReplace {

        private int position;

        public UUIDReplace(DatabaseTableColumnList list, String nameOrPosition) {
            DatabaseTableColumn column;
            if (list != null && (column = list.getColumn(nameOrPosition)) != null) {
                this.position = column.getPosition();
            } else if (StringUtils.isNumber(nameOrPosition)) {
                this.position = Integer.parseInt(nameOrPosition);
            } else {
                throw new IllegalArgumentException(nameOrPosition);
            }
        }

        public int getPosition() {
            return position;
        }

        public String getValue() {
            return StringUtils.toRandomUUID();
        }
    }

    /**
     * 将字段替换成指定值
     */
    protected static class StandardReplace implements IncrementReplace {

        private int position;
        private String value;

        public StandardReplace(DatabaseTableColumnList list, String nameOrPosition, String value) {
            DatabaseTableColumn column;
            if (list != null && (column = list.getColumn(nameOrPosition)) != null) {
                this.position = column.getPosition();
            } else if (StringUtils.isNumber(nameOrPosition)) {
                this.position = Integer.parseInt(nameOrPosition);
            } else {
                throw new IllegalArgumentException(nameOrPosition);
            }
            this.value = value == null ? "" : value;
        }

        public int getPosition() {
            return position;
        }

        public String getValue() {
            return value;
        }
    }
}
