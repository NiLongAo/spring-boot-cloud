package cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message;

import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.EventResult;
import cn.com.tzy.springbootstarterredis.pool.AbstractMessageListener;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.hutool.core.codec.Base64;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class AgentSubscribeHandle {

    private final static String VIDEO_AGENT_EVENT_SUBSCRIBE_MANAGER = SipConstant.VIDEO_AGENT_EVENT_SUBSCRIBE_MANAGER;

    public final static String VIDEO_AGENT_ERROR_EVENT_SUBSCRIBE_MANAGER = SipConstant.VIDEO_AGENT_ERROR_EVENT_SUBSCRIBE_MANAGER;

    public final static String VIDEO_AGENT_OK_EVENT_SUBSCRIBE_MANAGER = SipConstant.VIDEO_AGENT_OK_EVENT_SUBSCRIBE_MANAGER;
    private final Integer millis = 15;

    private final RedisMessageListenerContainer redisMessageListenerContainer;

    private final Map<String, AbstractMessageListener> errorMessageListener = new ConcurrentHashMap<>();

    private final Map<String, AbstractMessageListener> okMessageListener = new ConcurrentHashMap<>();

    private final Map<String, Instant> okTimeSubscribes = new ConcurrentHashMap<>();

    private final Map<String, Instant> errorTimeSubscribes = new ConcurrentHashMap<>();

    private final Map<String, List<SipSubscribeEvent>> errorSubscribes = new ConcurrentHashMap<>();

    private final Map<String, List<SipSubscribeEvent>> okSubscribes = new ConcurrentHashMap<>();



    public AgentSubscribeHandle(DynamicTask dynamicTask, RedisMessageListenerContainer redisMessageListenerContainer){
        dynamicTask.startCron(VIDEO_AGENT_EVENT_SUBSCRIBE_MANAGER,millis, this::execute);
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
        errorTimeSubscribes.put(key, Instant.now());
        errorSubscribes.computeIfAbsent(key, o -> new ArrayList<SipSubscribeEvent>()).add(event);
        String redisListenerKey = String.format("%s%s", VIDEO_AGENT_ERROR_EVENT_SUBSCRIBE_MANAGER, key);
        //创建监听
        AbstractMessageListener abstractMessageListener = errorMessageListener.computeIfAbsent(key, o -> new AbstractMessageListener(redisListenerKey) {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                List<SipSubscribeEvent> errprSubscribe = getErrorSubscribe(key);
                if(errprSubscribe == null || errprSubscribe.isEmpty()){
                    //移除
                    removeAllSubscribe(key);
                    return;
                }
                Object body = RedisUtils.redisTemplate.getValueSerializer().deserialize(message.getBody());
                Object deserialize = SerializationUtils.deserialize(Base64.decode((String) body));
                EventObject event = (EventObject) deserialize;
                for (SipSubscribeEvent sipSubscribeEvent : errprSubscribe) {
                    sipSubscribeEvent.response(new EventResult(event));
                }
                //移除
                removeAllSubscribe(key);
            }
        });
        redisMessageListenerContainer.addMessageListener(abstractMessageListener,new PatternTopic(abstractMessageListener.getPatternTopicName()));
    }

    public void addOkSubscribe(String key, SipSubscribeEvent event) {
        okTimeSubscribes.put(key, Instant.now());
        okSubscribes.computeIfAbsent(key, o -> new ArrayList<SipSubscribeEvent>()).add(event);
        String redisListenerKey = String.format("%s%s", VIDEO_AGENT_OK_EVENT_SUBSCRIBE_MANAGER, key);
        //创建监听
        AbstractMessageListener abstractMessageListener = okMessageListener.computeIfAbsent(key, o -> new AbstractMessageListener(redisListenerKey) {
            @Override
            public void onMessage(Message message, byte[] pattern) {

                List<SipSubscribeEvent> okSubscribe = getOkSubscribe(key);
                if(okSubscribe == null || okSubscribe.isEmpty()){
                    //移除
                    removeAllSubscribe(key);
                    return;
                }
                Object body = RedisUtils.redisTemplate.getValueSerializer().deserialize(message.getBody());
                Object deserialize = SerializationUtils.deserialize(Base64.decode((String) body));
                EventObject event = (EventObject) deserialize;
                for (SipSubscribeEvent sipSubscribeEvent : okSubscribe) {
                    sipSubscribeEvent.response(new EventResult(event));
                }
                //移除
                removeAllSubscribe(key);
            }
        });
        redisMessageListenerContainer.addMessageListener(abstractMessageListener,new PatternTopic(abstractMessageListener.getPatternTopicName()));
    }

    public List<SipSubscribeEvent> getErrorSubscribe(String key) {
        return errorSubscribes.get(key);
    }

    public  void removeAllSubscribe(String key){
        removeErrorSubscribe(key);
        removeOkSubscribe(key);
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

    public List<SipSubscribeEvent> getOkSubscribe(String key) {
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
