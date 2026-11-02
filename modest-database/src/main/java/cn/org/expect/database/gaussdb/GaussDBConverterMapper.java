package cn.org.expect.database.gaussdb;

import cn.org.expect.database.internal.StandardJdbcConverterMapper;
import cn.org.expect.util.StringUtils;

/**
 * 高斯扩展类型使用数据库文本协议，保留数组、枚举、域与复合类型的表示。
 */
public class GaussDBConverterMapper extends StandardJdbcConverterMapper {

    /** 仅在内置映射缺失时使用的转换器键 */
    private final static String TEXT_TYPE = "__GAUSS_TEXT_TYPE__";

    /**
     * 初始化扩展类型的文本转换器。
     *
     * @param converter 导出或导入转换器
     */
    public GaussDBConverterMapper(Class<?> converter) {
        this.add(TEXT_TYPE, converter);
    }

    @Override
    public boolean contains(String key) {
        return StringUtils.isNotBlank(key);
    }

    @Override
    public <E> E get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return super.get(super.contains(key) ? key : TEXT_TYPE);
    }
}
