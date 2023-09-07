package cn.com.tzy.spingbootstartermybatis.config;


import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.provider.AbstractDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShadowDataSource;
import org.apache.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;

/**
 * 动态数据源配置：
 * 使用{@link com.baomidou.dynamic.datasource.annotation.DS}注解，切换数据源
 * @author lixiaoyi
 */
@Configuration
@AutoConfigureBefore({DynamicDataSourceAutoConfiguration.class, SpringBootConfiguration.class})
@ConditionalOnProperty( name = "spring.shardingsphere.enabled", havingValue = "true") //开启分库分表加载 默认不加载
public class ShardingDataSourceConfig {

    /**
     * 分表数据源名称
     */
    @Value("spring.shardingsphere.datasource.names")
    private String SHARDING_DATA_SOURCE_NAME;

    /**
     * 动态数据源配置项
     */
    @Resource
    private DynamicDataSourceProperties dynamicDataSourceProperties;

    /**
     * shardingjdbc有四种数据源，需要根据业务注入不同的数据源
     * <p>1. 未使用分片, 脱敏的名称(默认): shardingDataSource;
     * <p>2. 主从数据源: masterSlaveDataSource;
     * <p>3. 脱敏数据源：encryptDataSource;
     * <p>4. 影子数据源：shadowDataSource
     */
    @Lazy
    @Resource
    private ShadowDataSource shardingDataSource;

    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        Map<String, DataSourceProperty> datasourceMap = dynamicDataSourceProperties.getDatasource();
        return new AbstractDataSourceProvider() {
            @Override
            public Map<String, DataSource> loadDataSources() {
                Map<String, DataSource> dataSourceMap = createDataSourceMap(datasourceMap);
                // 将 shardingjdbc 管理的数据源也交给动态数据源管理
                dataSourceMap.put(SHARDING_DATA_SOURCE_NAME, shardingDataSource);
                return dataSourceMap;
            }
        };
    }

    /**
     * 将动态数据源设置为首选的 （此处可以不注入 此对象 因为配置里已经表明了primary数据源）
     * 当spring存在多个数据源时, 自动注入的是首选的对象
     * 设置为主要的数据源之后，就可以支持shardingjdbc原生的配置方式了
     */
    @Primary
    @Bean
    public DataSource dataSource(DynamicDataSourceProvider dynamicDataSourceProvider) {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        dataSource.setPrimary(dynamicDataSourceProperties.getPrimary());
        dataSource.setStrict(dynamicDataSourceProperties.getStrict());
        dataSource.setStrategy(dynamicDataSourceProperties.getStrategy());
        dataSource.setProvider(dynamicDataSourceProvider);
        dataSource.setP6spy(dynamicDataSourceProperties.getP6spy());
        dataSource.setSeata(dynamicDataSourceProperties.getSeata());
        return dataSource;
    }
}
