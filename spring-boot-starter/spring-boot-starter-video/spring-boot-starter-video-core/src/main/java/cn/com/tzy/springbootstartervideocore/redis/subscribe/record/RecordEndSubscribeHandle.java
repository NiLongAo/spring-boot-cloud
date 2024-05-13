package cn.com.tzy.springbootstartervideocore.redis.subscribe.record;

import cn.com.tzy.springbootstarterredis.pool.AbstractMessageListener;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.vo.sip.RecordInfo;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 录像查询结束后回调事件
 * redis 订阅方式
 */
@Log4j2
public class RecordEndSubscribeHandle {

    private final RedisMessageListenerContainer redisMessageListenerContainer;

    private final String VIDEO_RECORD_END_SUBSCRIBE_MANAGER = VideoConstant.VIDEO_RECORD_END_SUBSCRIBE_MANAGER;

    private final Integer millis = 15;
    private final Map<String, Instant> handlerMapSubscribes = new ConcurrentHashMap<>();
    private final Map<String, RecordEndSubscribeEvent> handlerMap = new ConcurrentHashMap<>();

    private final Map<String, AbstractMessageListener> handlerListenerMap = new ConcurrentHashMap<>();

    public RecordEndSubscribeHandle(DynamicTask dynamicTask,RedisMessageListenerContainer redisMessageListenerContainer){
        dynamicTask.startCron(VIDEO_RECORD_END_SUBSCRIBE_MANAGER,millis, this::execute);
        this.redisMessageListenerContainer = redisMessageListenerContainer;
    }

    public void execute(){
        Instant instant = Instant.now().minusMillis(TimeUnit.SECONDS.toMillis(millis));
        for (String key : handlerMap.keySet()) {
            if (handlerMapSubscribes.get(key).isBefore(instant)){
                delEndEventHandler(key);
            }
        }
        log.info("[定时任务] 清理过期 录像查询结束后回调事件, handlerMap : {},handlerMapSubscribes : {}",handlerMap.size(),handlerMapSubscribes.size());
    }
    public void handlerEvent(RecordInfo recordInfo) {
        String key = String.format("%s%s:%s", VIDEO_RECORD_END_SUBSCRIBE_MANAGER, recordInfo.getDeviceId(), recordInfo.getChannelId());
        log.info("录像查询完成事件触发，deviceId：{}, channelId: {}, 录像数量{}/{}条", recordInfo.getDeviceId(), recordInfo.getChannelId(), recordInfo.getCount(),recordInfo.getSumNum());
        if (handlerMap.size() > 0) {
            RedisUtils.redisTemplate.convertAndSend(key,recordInfo);
        }
    }

    /**
     * 添加
     * @param device
     * @param channelId
     * @param recordEndSubscribeEvent
     */
    public void addEndEventHandler(String device, String channelId, RecordEndSubscribeEvent recordEndSubscribeEvent) {
        String key = String.format("%s%s:%s", VIDEO_RECORD_END_SUBSCRIBE_MANAGER, device, channelId);
        handlerMapSubscribes.put(key, Instant.now());
        handlerMap.put(key, recordEndSubscribeEvent);
        //创建监听
        AbstractMessageListener abstractMessageListener = new AbstractMessageListener(key) {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                RecordEndSubscribeEvent endSubscribeEvent = handlerMap.get(key);
                if (endSubscribeEvent != null) {
                    Object body = RedisUtils.redisTemplate.getValueSerializer().deserialize(message.getBody());
                    RecordInfo recordInfo = (RecordInfo) body;
                    endSubscribeEvent.handler(recordInfo);
                    //发送完后移除
                    int count = recordInfo.getCount();
                    int sumNum = recordInfo.getSumNum();
                    if (count == sumNum){
                        delEndEventHandler(key);
                    }
                }
            }
        };
        redisMessageListenerContainer.addMessageListener(abstractMessageListener,new PatternTopic(abstractMessageListener.getPatternTopicName()));
        handlerListenerMap.put(key,abstractMessageListener);
    }

    private void delEndEventHandler(String key){
        handlerMapSubscribes.remove(key);
        handlerMap.remove(key);
        AbstractMessageListener abstractMessageListener = handlerListenerMap.remove(key);
        if(abstractMessageListener != null){
            redisMessageListenerContainer.removeMessageListener(abstractMessageListener);
        }
    }

}
