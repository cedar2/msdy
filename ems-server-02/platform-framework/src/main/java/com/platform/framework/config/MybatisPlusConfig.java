package com.platform.framework.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.StringUtils;
import com.platform.framework.security.properties.TableWhiteProperties;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.platform.*.mapper.*")
@SuppressWarnings("all")
public class MybatisPlusConfig {

    // 排除过滤的表名，nacos自行添加
    @Autowired
    private TableWhiteProperties whiteTables;

    /**
     * 新多租户插件配置,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存万一出现问题
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public String getTenantIdColumn() {
                return "client_id";
            }

            @Override
            public Expression getTenantId() {
                LoginUser user = ApiThreadLocalUtil.get();
                if (user != null && user.getSysUser() != null && user.getSysUser().getClientId() != null) {
                    return new LongValue(user.getSysUser().getClientId());
                }
                return new LongValue(10001);
            }

            // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
            @Override
            public boolean ignoreTable(String tableName) {
                // 全部转为小写
                tableName = tableName.toLowerCase();
                boolean result = StringUtils.matches(tableName, whiteTables.getWhites());
                if (result) {
                    return result;
                }
                LoginUser user = ApiThreadLocalUtil.get();
                if (user != null && ((user.getUserid() != null && 1L == user.getUserid())  || "10000".equals(user.getClientId()))) {
                    // admin账号显示所有数据
                    return true;
                }
                return result;
            }
        }));
        return interceptor;
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }

}

