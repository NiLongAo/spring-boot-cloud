package cn.com.tzy.springbootstartersocketio.config.Jedis;

import com.corundumstudio.socketio.store.Store;
import com.corundumstudio.socketio.store.pubsub.BaseStoreFactory;
import com.corundumstudio.socketio.store.pubsub.PubSubStore;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.Map;
import java.util.UUID;

public class JedisStoreFactory extends BaseStoreFactory {

    private final RedisTemplate<String, Object> redisClient;
    private final PubSubStore pubSubStore;

    public JedisStoreFactory(RedisMessageListenerContainer redisMessageListenerContainer, RedisTemplate<String, Object> redisClient) {
        this.redisClient = redisClient;
        this.pubSubStore = new JedisPubSubStore(redisMessageListenerContainer,redisClient,getNodeId());;
    }


    @Override
    public PubSubStore pubSubStore() {
        return pubSubStore;
    }

    @Override
    public <K, V> Map<K, V> createMap(String name) {
        return  (Map<K, V>) redisClient.opsForHash().entries(name);
    }

    @Override
    public Store createStore(UUID sessionId) {
        return new JedisSore(sessionId, redisClient);
    }

    @Override
    public void shutdown() {

    }
}
