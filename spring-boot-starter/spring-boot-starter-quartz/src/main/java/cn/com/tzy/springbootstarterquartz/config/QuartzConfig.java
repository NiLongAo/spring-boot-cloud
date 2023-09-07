package cn.com.tzy.springbootstarterquartz.config;

import cn.com.tzy.springbootstarterquartz.config.factory.CronTrigger;
import cn.com.tzy.springbootstarterquartz.config.factory.IntervalTrigger;
import cn.com.tzy.springbootstarterquartz.config.factory.TriggerManager;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import java.io.IOException;

@Configuration
@AutoConfigureAfter(DynamicDataSourceAutoConfiguration.class)
@Import({CronTrigger.class, IntervalTrigger.class, TriggerManager.class,QuartzTaskManager.class})
public class QuartzConfig {

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;
    @Autowired
    private AutowireCapableBeanFactory capableBeanFactory;
    /**
     * 配置SchedulerFactoryBean
     *
     * @return
     * @throws IOException
     */
    @Bean //将一个方法产生为Bean并交给Spring容器管理(@Bean只能用在方法上)
    public SchedulerFactoryBean schedulerFactoryBean()
            throws IOException {
        //Spring提供SchedulerFactoryBean为Scheduler提供配置信息,并被Spring容器管理其生命周期
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        //设置数据源(使用系统的主数据源，覆盖propertis文件的dataSource配置)
        factory.setDataSource(dynamicRoutingDataSource.getDataSource("qrtz"));
        //修复job 无法注入bean
        factory.setJobFactory(jobFactory());
        return factory;
    }

    @Bean
    public JobFactory jobFactory() {
        return new AdaptableJobFactory() {
            @Override
            protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
                Object jobInstance = super.createJobInstance(bundle);
                capableBeanFactory.autowireBean(jobInstance);
                return jobInstance;
            }
        };
    }
}
