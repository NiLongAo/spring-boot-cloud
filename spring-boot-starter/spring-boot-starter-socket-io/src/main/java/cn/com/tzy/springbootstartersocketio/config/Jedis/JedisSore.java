package cn.com.tzy.springbootstartersocketio.config.Jedis;

import com.corundumstudio.socketio.store.Store;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.UUID;

public class JedisSore implements Store {

    private final Map<Object, Object> map;

    public JedisSore(UUID sessionId, RedisTemplate<String, Object> redisTemplate){
        this.map = redisTemplate.opsForHash().entries(sessionId.toString());
    }
    @Override
    public void set(String key, Object value) {
        map.put(key, value);
    }

    @Override
    public <T> T get(String key) {
        return (T) map.get(key);
    }

    @Override
    public boolean has(String key) {
        return map.containsKey(key);
    }

    @Override
    public void del(String key) {
        map.remove(key);
    }
}
