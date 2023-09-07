package cn.com.tzy.spingbootstartermybatis.config;

import cn.com.tzy.spingbootstartermybatis.core.mapper.handler.DefaultDBFieldHandler;
import cn.com.tzy.spingbootstartermybatis.core.mapper.utils.MyBatisUtils;
import cn.com.tzy.spingbootstartermybatis.core.tenant.aop.TenantIgnoreAspect;
import cn.com.tzy.spingbootstartermybatis.core.tenant.context.TenantContextWebFilter;
import cn.com.tzy.spingbootstartermybatis.core.tenant.db.DefaultTenantDatabaseInterceptor;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "mybatis.tenant", value = "enable", matchIfMissing = true) // 允许使用 yudao.tenant.enable=false 禁用多租户
@EnableConfigurationProperties(TenantProperties.class)
public class TenantConfiguration {

    // ========== AOP ==========
    @Bean
    public TenantIgnoreAspect tenantIgnoreAspect() {
        return new TenantIgnoreAspect();
    }

    // ========== DB ==========

    @Bean
    public TenantLineInnerInterceptor tenantLineInnerInterceptor(TenantProperties properties,
                                                                 MybatisPlusInterceptor interceptor,
                                                                 ObjectProvider<TenantLineHandler> tenantLineHandler) {
        TenantLineHandler ifAvailable = tenantLineHandler.getIfAvailable();
        if(ifAvailable == null){
            ifAvailable = new DefaultTenantDatabaseInterceptor(properties);
        }
        TenantLineInnerInterceptor inner = new TenantLineInnerInterceptor(ifAvailable);
        // 添加到 interceptor 中
        // 需要加在首个，主要是为了在分页插件前面。这个是 MyBatis Plus 的规定
        MyBatisUtils.addInterceptor(interceptor, inner, 0);
        return inner;
    }

    // ========== JWT ==========
    @Bean
    public FilterRegistrationBean<TenantContextWebFilter> tenantContextWebFilter() {
        FilterRegistrationBean<TenantContextWebFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TenantContextWebFilter());
        return registrationBean;
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor()); // 分页插件
        return mybatisPlusInterceptor;
    }

    @Bean
    public MetaObjectHandler defaultMetaObjectHandler(){
        return new DefaultDBFieldHandler(); // 自动填充参数类
    }

}
