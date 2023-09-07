package cn.com.tzy.springbootstarterredis.config;

import cn.com.tzy.springbootstarterredis.pool.AbstractMessageListener;
import cn.com.tzy.springbootstarterredis.pool.RedisPool;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@AutoConfigureBefore(RedisAutoConfiguration.class)
@Import({RedisUtils.class})
public class RedisConfig {


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        Jackson2JsonRedisSerializer<Object> serializer = RedisPool.serializer();
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer); // key
        redisTemplate.setValueSerializer(serializer); //value
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


    //注册redis 发布订阅
    @Bean(destroyMethod = "destroy")
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory factory, ObjectProvider<AbstractMessageListener> messageListener){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        //自定义线程池
        container.setTaskExecutor(executor());
        for (AbstractMessageListener abstractMessageListener : messageListener) {
            //添加订阅
            container.addMessageListener(abstractMessageListener,new PatternTopic(abstractMessageListener.getPatternTopicName()));
        }
        return container;
    }


    @Bean
    public TaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;
    }

}
