package cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.notify;

import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterredis.pool.AbstractMessageListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@Log4j2
@Component
public class AgentLoginSubscribe {

    private final static String FS_AGENT_LOGIN_SUBSCRIBE_MANAGER = SipConstant.FS_AGENT_LOGIN_SUBSCRIBE_MANAGER;
    public final static String FS_AGENT_LOGIN_EVENT_SUBSCRIBE_MANAGER = SipConstant.FS_AGENT_LOGIN_EVENT_SUBSCRIBE_MANAGER;
    private final Integer millis = 8;
    private final RedisMessageListenerContainer redisMessageListenerContainer;

    private final Map<String, AbstractMessageListener> loginMessageListener = new ConcurrentHashMap<>();

    private final Map<String, Instant> loginTimeSubscribes = new ConcurrentHashMap<>();

    private final Map<String, SipLoginEvent> loginSubscribes = new ConcurrentHashMap<>();



    public AgentLoginSubscribe(DynamicTask dynamicTask, RedisMessageListenerContainer redisMessageListenerContainer){
        dynamicTask.startCron(FS_AGENT_LOGIN_SUBSCRIBE_MANAGER,millis, this::execute);
        this.redisMessageListenerContainer = redisMessageListenerContainer;
    }

    public void execute(){
        Instant instant = Instant.now().minusMillis(TimeUnit.SECONDS.toMillis(millis));
        for (String key : loginTimeSubscribes.keySet()) {
            if (loginTimeSubscribes.get(key).isBefore(instant)){
                removeOkSubscribe(key);
            }
        }
        log.info("[定时任务] 清理过期的SIP订阅信息, loginSubscribes : {},loginSubscribes : {}",loginSubscribes.size(),loginSubscribes.size());
    }
    
    public void addLoginSubscribe(String key, SipLoginEvent event) {
        loginTimeSubscribes.put(key, Instant.now());
        loginSubscribes.put(key,event);
        String redisListenerKey = String.format("%s%s", FS_AGENT_LOGIN_EVENT_SUBSCRIBE_MANAGER, key);
        //创建监听
        AbstractMessageListener abstractMessageListener = loginMessageListener.computeIfAbsent(key, o -> new AbstractMessageListener(redisListenerKey) {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                SipLoginEvent okSubscribe = loginSubscribes.get(key);
                if(okSubscribe != null){
                    okSubscribe.run();
                    return;
                }
                //移除
                removeAllSubscribe(key);
            }
        });
        redisMessageListenerContainer.addMessageListener(abstractMessageListener,new PatternTopic(abstractMessageListener.getPatternTopicName()));
    }

    public  void removeAllSubscribe(String key){
        removeOkSubscribe(key);
    }

    public void removeOkSubscribe(String key) {
        if(key == null){
            return;
        }
        loginSubscribes.remove(key);
        loginTimeSubscribes.remove(key);
        AbstractMessageListener abstractMessageListener = loginMessageListener.remove(key);
        if(abstractMessageListener != null){
            redisMessageListenerContainer.removeMessageListener(abstractMessageListener);
        }
    }
}
