package cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message;

import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterredis.pool.AbstractMessageListener;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.EventResult;
import cn.hutool.core.codec.Base64;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.time.Instant;
import java.util.EventObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class SipSubscribeHandle {

    private final static String VIDEO_EVENT_SUBSCRIBE_MANAGER = SipConstant.VIDEO_SIP_EVENT_SUBSCRIBE_MANAGER;

    public final static String VIDEO_SIP_ERROR_EVENT_SUBSCRIBE_MANAGER = SipConstant.VIDEO_SIP_ERROR_EVENT_SUBSCRIBE_MANAGER;

    public final static String VIDEO_SIP_OK_EVENT_SUBSCRIBE_MANAGER = SipConstant.VIDEO_SIP_OK_EVENT_SUBSCRIBE_MANAGER;
    private final Integer millis = 15;

    private final RedisMessageListenerContainer redisMessageListenerContainer;

    private final Map<String, AbstractMessageListener> errorMessageListener = new ConcurrentHashMap<>();

    private final Map<String, AbstractMessageListener> okMessageListener = new ConcurrentHashMap<>();
    private final Map<String, SipSubscribeEvent> errorSubscribes = new ConcurrentHashMap<>();

    private final Map<String, SipSubscribeEvent> okSubscribes = new ConcurrentHashMap<>();

    private final Map<String, Instant> okTimeSubscribes = new ConcurrentHashMap<>();

    private final Map<String, Instant> errorTimeSubscribes = new ConcurrentHashMap<>();


    public SipSubscribeHandle(DynamicTask dynamicTask, RedisMessageListenerContainer redisMessageListenerContainer){
        dynamicTask.startCron(VIDEO_EVENT_SUBSCRIBE_MANAGER,millis, this::execute);
        this.redisMessageListenerContainer = redisMessageListenerContainer;
    }

    public void execute(){
        Instant instant = Instant.now().minusMillis(TimeUnit.SECONDS.toMillis(millis));
        for (String key : okTimeSubscribes.keySet()) {
            if (okTimeSubscribes.get(key).isBefore(instant)){
                removeOkSubscribe(key);
            }
        }
        for (String key : errorTimeSubscribes.keySet()) {
            if (errorTimeSubscribes.get(key).isBefore(instant)){
                removeErrorSubscribe(key);
            }
        }
        log.info("[定时任务] 清理过期的SIP订阅信息, okSubscribes : {},okTimeSubscribes : {},errorSubscribes : {},errorTimeSubscribes : {}",okSubscribes.size(),okTimeSubscribes.size(),errorSubscribes.size(),errorTimeSubscribes.size());
    }


    public void addErrorSubscribe(String key, SipSubscribeEvent event) {
        errorSubscribes.put(key, event);
        errorTimeSubscribes.put(key, Instant.now());
        //创建监听
        AbstractMessageListener abstractMessageListener = new AbstractMessageListener(String.format("%s%s", VIDEO_SIP_ERROR_EVENT_SUBSCRIBE_MANAGER, key)) {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                SipSubscribeEvent errprSubscribe = getErrorSubscribe(key);
                if (errprSubscribe != null) {
                    //发送完后移除
                    removeOkSubscribe(key);
                    removeErrorSubscribe(key);
                    Object body = RedisUtils.redisTemplate.getValueSerializer().deserialize(message.getBody());
                    Object deserialize = SerializationUtils.deserialize(Base64.decode((String) body));
                    EventObject event = (EventObject) deserialize;
                    errprSubscribe.response(new EventResult(event));
                }
            }
        };
        redisMessageListenerContainer.addMessageListener(abstractMessageListener,new PatternTopic(abstractMessageListener.getPatternTopicName()));
        errorMessageListener.put(key,abstractMessageListener);
    }

    public void addOkSubscribe(String key, SipSubscribeEvent event) {
        okSubscribes.put(key, event);
        okTimeSubscribes.put(key, Instant.now());
        //创建监听

        AbstractMessageListener abstractMessageListener = new AbstractMessageListener(String.format("%s%s", VIDEO_SIP_OK_EVENT_SUBSCRIBE_MANAGER, key)) {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                SipSubscribeEvent okSubscribe = getOkSubscribe(key);
                if (okSubscribe != null) {
                    //发送完后移除
                    removeOkSubscribe(key);
                    removeErrorSubscribe(key);
                    Object body = RedisUtils.redisTemplate.getValueSerializer().deserialize(message.getBody());
                    Object deserialize = SerializationUtils.deserialize(Base64.decode((String) body));
                    EventObject event = (EventObject) deserialize;
                    okSubscribe.response(new EventResult(event));
                }
            }
        };
        redisMessageListenerContainer.addMessageListener(abstractMessageListener,new PatternTopic(abstractMessageListener.getPatternTopicName()));
        okMessageListener.put(key,abstractMessageListener);
    }

    public SipSubscribeEvent getErrorSubscribe(String key) {
        return errorSubscribes.get(key);
    }

    public void removeErrorSubscribe(String key) {
        if(key == null){
            return;
        }
        errorSubscribes.remove(key);
        errorTimeSubscribes.remove(key);
        AbstractMessageListener abstractMessageListener = errorMessageListener.remove(key);
        if(abstractMessageListener != null){
            redisMessageListenerContainer.removeMessageListener(abstractMessageListener);
        }
    }

    public SipSubscribeEvent getOkSubscribe(String key) {
        return okSubscribes.get(key);
    }

    public void removeOkSubscribe(String key) {
        if(key == null){
            return;
        }
        okSubscribes.remove(key);
        okTimeSubscribes.remove(key);
        AbstractMessageListener abstractMessageListener = okMessageListener.remove(key);
        if(abstractMessageListener != null){
            redisMessageListenerContainer.removeMessageListener(abstractMessageListener);
        }
    }
    public int getErrorSubscribesSize(){
        return errorSubscribes.size();
    }
    public int getOkSubscribesSize(){
        return okSubscribes.size();
    }
}
