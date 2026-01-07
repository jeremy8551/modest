package cn.org.expect.database.internal;

import cn.org.expect.database.DatabaseType;

public class StandardDatabaseType implements DatabaseType {

    private String name;

    private Integer sqlType;

    private Integer sqlDataType;

    private Integer precision;

    private String literalPrefix;

    private String literalSuffix;

    private String createParams;

    private Integer nullAble;

    private Integer maximumScale;

    private Integer minimumScale;

    private Integer numberPrecRadix;

    private Integer fixedPrecScale;

    private Integer searchable;

    private Integer autoIncrement;

    private Integer caseSesitive;

    private Integer unsignedAttribute;

    private Integer sqlDateTimeSub;

    private String localTypeName;

    public StandardDatabaseType() {
    }

    /**
     * 初始化
     *
     * @param obj 类型
     */
    public StandardDatabaseType(StandardDatabaseType obj) {
        this.name = obj.name;
        this.sqlType = obj.sqlType;
        this.sqlDataType = obj.sqlDataType;
        this.precision = obj.precision;
        this.literalPrefix = obj.literalPrefix;
        this.literalSuffix = obj.literalSuffix;
        this.createParams = obj.createParams;
        this.caseSesitive = obj.caseSesitive;
        this.nullAble = obj.nullAble;
        this.maximumScale = obj.maximumScale;
        this.minimumScale = obj.minimumScale;
        this.numberPrecRadix = obj.numberPrecRadix;
        this.fixedPrecScale = obj.fixedPrecScale;
        this.searchable = obj.searchable;
        this.autoIncrement = obj.autoIncrement;
        this.unsignedAttribute = obj.unsignedAttribute;
        this.sqlDateTimeSub = obj.sqlDateTimeSub;
        this.localTypeName = obj.localTypeName;
    }

    public StandardDatabaseType clone() {
        return new StandardDatabaseType(this);
    }

    public String getName() {
        return name;
    }

    public Integer getSqlType() {
        return sqlType;
    }

    public Integer getPrecision() {
        return precision;
    }

    public String getTextPrefix() {
        return literalPrefix;
    }

    public String getTextSuffix() {
        return literalSuffix;
    }

    public String toText(CharSequence value) {
        StringBuilder buf = new StringBuilder();
        buf.append(this.literalPrefix);
        buf.append(value);
        buf.append(this.literalSuffix);
        return buf.toString();
    }

    public String getExpression() {
        return createParams;
    }

    public Integer getNullAble() {
        return nullAble;
    }

    public Integer getMaxScale() {
        return maximumScale;
    }

    public Integer getMinScale() {
        return minimumScale;
    }

    public Integer getFixedPrecScale() {
        return fixedPrecScale;
    }

    public Integer getRadix() {
        return numberPrecRadix;
    }

    public Integer getSearchable() {
        return searchable;
    }

    public Integer getAutoIncrement() {
        return autoIncrement;
    }

    public Integer getUnsigned() {
        return unsignedAttribute;
    }

    public String getLocalName() {
        return localTypeName;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setSqlType(final Integer sqlType) {
        this.sqlType = sqlType;
    }

    public void setSqlDataType(final Integer sqlDataType) {
        this.sqlDataType = sqlDataType;
    }

    public void setPrecision(final Integer precision) {
        this.precision = precision;
    }

    public void setLiteralPrefix(final String literalPrefix) {
        this.literalPrefix = literalPrefix;
    }

    public void setLiteralSuffix(final String literalSuffix) {
        this.literalSuffix = literalSuffix;
    }

    public void setCreateParams(final String createParams) {
        this.createParams = createParams;
    }

    public void setNullAble(final Integer nullAble) {
        this.nullAble = nullAble;
    }

    public void setMaximumScale(final Integer maximumScale) {
        this.maximumScale = maximumScale;
    }

    public void setMinimumScale(final Integer minimumScale) {
        this.minimumScale = minimumScale;
    }

    public void setNumberPrecRadix(final Integer numberPrecRadix) {
        this.numberPrecRadix = numberPrecRadix;
    }

    public void setFixedPrecScale(final Integer fixedPrecScale) {
        this.fixedPrecScale = fixedPrecScale;
    }

    public void setSearchable(final Integer searchable) {
        this.searchable = searchable;
    }

    public void setAutoIncrement(final Integer autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public void setUnsignedAttribute(final Integer unsignedAttribute) {
        this.unsignedAttribute = unsignedAttribute;
    }

    public void setSqlDateTimeSub(final Integer sqlDateTimeSub) {
        this.sqlDateTimeSub = sqlDateTimeSub;
    }

    public void setLocalTypeName(final String localTypeName) {
        this.localTypeName = localTypeName;
    }

    public Integer getCaseSesitive() {
        return caseSesitive;
    }

    public void setCaseSesitive(final Integer caseSesitive) {
        this.caseSesitive = caseSesitive;
    }

    //	public Integer getSqlDataType() {
//		return sqlDataType;
//	}
//
//	public Integer getSqlDateTimeSub() {
//		return sqlDateTimeSub;
//	}

    public String toString() {
        return "StandardDatabaseType [name=" + name + ", sqlType=" + sqlType + ", sqlDataType=" + sqlDataType + ", precision=" + precision + ", literalPrefix=" + literalPrefix + ", literalSuffix=" + literalSuffix + ", createParams=" + createParams + ", caseSesitive=" + caseSesitive + ", nullAble=" + nullAble + ", maximumScale=" + maximumScale + ", minimumScale=" + minimumScale + ", numberPrecRadix=" + numberPrecRadix + ", fixedPrecScale=" + fixedPrecScale + ", searchable=" + searchable + ", autoIncrement=" + autoIncrement + ", unsignedAttribute=" + unsignedAttribute + ", sqlDateTimeSub=" + sqlDateTimeSub + ", localTypeName=" + localTypeName + "]";
    }
}
