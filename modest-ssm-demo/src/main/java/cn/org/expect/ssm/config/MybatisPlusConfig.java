package cn.org.expect.ssm.config;

import java.sql.SQLException;

import cn.org.expect.util.StringUtils;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() throws SQLException {
        DbType dbType = MybatisPlusConfig.parseUrl(this.jdbcUrl);
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor()); // 乐观锁拦截器
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor()); // 阻止全表更新与删除操作
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType)); // 如果配置多个插件, 切记分页最后添加
        return interceptor;
    }

    /**
     * 解析 JDBC URL 字符串中的数据库信息
     *
     * @param jdbcUrl JDBC URL字符串
     * @return 数据库类型
     */
    public static DbType parseUrl(String jdbcUrl) {
        DbType type = null;
        String[] array = StringUtils.split(jdbcUrl, ':');
        for (String str : array) {
            type = DbType.getDbType(str);
            if (!type.equals(DbType.OTHER)) {
                return type;
            }
        }
        return type;
    }
}
