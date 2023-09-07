package cn.com.tzy.springbootstartersocketio.config.Jedis;

import cn.com.tzy.springbootstarterredis.pool.AbstractMessageListener;
import com.corundumstudio.socketio.store.pubsub.PubSubListener;
import com.corundumstudio.socketio.store.pubsub.PubSubMessage;
import com.corundumstudio.socketio.store.pubsub.PubSubStore;
import com.corundumstudio.socketio.store.pubsub.PubSubType;
import io.netty.util.internal.PlatformDependent;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * 实现redis 队列同步消息
 */
public class JedisPubSubStore implements PubSubStore {
    private final RedisTemplate<String, Object> jedisPub;
    private final Long nodeId;
    private final ConcurrentMap<String, Queue<AbstractMessageListener>> map = PlatformDependent.newConcurrentHashMap();
    private final RedisMessageListenerContainer redisMessageListenerContainer;

    public JedisPubSubStore(RedisMessageListenerContainer redisMessageListenerContainer,RedisTemplate<String, Object> redissonPub, Long nodeId) {
        this.redisMessageListenerContainer =redisMessageListenerContainer;
        this.jedisPub = redissonPub;
        this.nodeId = nodeId;
    }

    @Override
    public void publish(PubSubType type, PubSubMessage msg) {
        msg.setNodeId(nodeId);
        jedisPub.convertAndSend(type.toString(),msg);
    }
    @Override
    public <T extends PubSubMessage> void subscribe(PubSubType type, PubSubListener<T> listener, Class<T> clazz) {
        String name = type.toString();
        AbstractMessageListener abstractMessageListener = new AbstractMessageListener(name) {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                Object deserialize = jedisPub.getValueSerializer().deserialize(message.getBody());
                PubSubMessage pubSubMessage = (PubSubMessage)deserialize;
                if (!nodeId.equals(pubSubMessage.getNodeId())) {
                    listener.onMessage((T) pubSubMessage);
                }
            }
        };
        //添加监听
        redisMessageListenerContainer.addMessageListener(abstractMessageListener,new PatternTopic(abstractMessageListener.getPatternTopicName()));
        //添加通道
        Queue<AbstractMessageListener> list= map.computeIfAbsent(name,k->new ConcurrentLinkedQueue<AbstractMessageListener>());
        list.add(abstractMessageListener);
    }
    @Override
    public void unsubscribe(PubSubType type) {
        String name = type.toString();
        Queue<AbstractMessageListener> redisConnections = map.remove(name);
        for (AbstractMessageListener redisConnection : redisConnections) {
            redisMessageListenerContainer.removeMessageListener(redisConnection,new PatternTopic(redisConnection.getPatternTopicName()));
        }
    }
    @Override
    public void shutdown() {

    }
}
