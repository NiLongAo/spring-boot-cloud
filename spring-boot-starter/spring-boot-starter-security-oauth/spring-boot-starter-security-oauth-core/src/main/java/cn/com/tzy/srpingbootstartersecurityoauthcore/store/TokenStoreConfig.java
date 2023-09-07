package cn.com.tzy.srpingbootstartersecurityoauthcore.store;

import cn.com.tzy.springbootstarterredis.pool.RedisPool;
import cn.com.tzy.srpingbootstartersecurityoauthcore.store.token.OAuth2AccessTokenMixIn;
import cn.com.tzy.srpingbootstartersecurityoauthcore.store.token.OAuth2AuthenticationMixin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.StandardStringSerializationStrategy;

import java.util.HashMap;

@Configuration
public class TokenStoreConfig {
    private final RedisConnectionFactory redisConnectionFactory;
    /**
     * 存储策略
     * 改为redis方式存储
     * @return
     */
    public TokenStoreConfig(RedisConnectionFactory redisConnectionFactory){
        this.redisConnectionFactory = redisConnectionFactory;
    }


    @Bean
    public TokenStore tokenStore(){
       // return new RedisClientTokenStore(redisConnectionFactory);
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);//使用JWT令牌
        Jackson2JsonRedisSerializer<Object> serializer = RedisPool.serializer(new HashMap<Class<?>,Class<?>>(){{
            put(OAuth2AccessToken.class, OAuth2AccessTokenMixIn.class);
            put(OAuth2Authentication.class, OAuth2AuthenticationMixin.class);
        }});
        redisTokenStore.setSerializationStrategy(new StandardStringSerializationStrategy() {
            @Override
            @SuppressWarnings("unchecked")
            protected <T> T deserializeInternal(byte[] bytes, Class<T> clazz) {
                return (T)serializer.deserialize(bytes);
            }
            @Override
            protected byte[] serializeInternal(Object object) {
                return serializer.serialize(object);
            }
        });
        return redisTokenStore;
    }

}
