package cn.com.tzy.springbootstarternacos.config.rule;

import com.alibaba.cloud.nacos.ribbon.NacosRule;
import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * 不能注册bean加载
 *
 * 注意在自定义负载均衡规则时，要在启动类中添加
 * @ComponentScan(basePackages = "cn.com.tzy.springbootwebapi",excludeFilters = {
 *         @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,value = MyRibbonConfig.class)
 * })
 * 防止在同一服务调用多个feign服务时，出现404
 * 参考文档 https://blog.csdn.net/ooyhao/article/details/102583102
 * 负载均衡的配置
 */
@Configuration
public class MyRibbonConfig {
//  使用nacos权重负载均衡
    @Bean
    public IRule iRule(){
        return new NacosRule();
    }

}
