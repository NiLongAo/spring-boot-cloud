package cn.com.tzy.springbootstartervideocore.redis.subscribe.media;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootstarterredis.pool.AbstractMessageListener;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.enums.HookType;
import cn.com.tzy.springbootstartervideobasic.vo.media.HookKey;
import cn.com.tzy.springbootstartervideocore.demo.MediaHookVo;
import cn.com.tzy.springbootstartervideocore.pool.task.DynamicTask;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 流媒体服务回调事件 的事件订阅
 * redis 订阅方式
 */
@Log4j2
public class MediaHookSubscribe {
    private final static String MEDIA_HOOK_SUBSCRIBE_MANAGER = VideoConstant.MEDIA_HOOK_SUBSCRIBE_MANAGER;
    private final static Integer millis = 20;

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final Map<HookType, Map<HookKey, HookEvent>> allSubscribes = new HashMap<>();
    private final Map<String, AbstractMessageListener> allListenerMap = new HashMap<>();

    public MediaHookSubscribe(DynamicTask dynamicTask,RedisMessageListenerContainer redisMessageListenerContainer){
        dynamicTask.startCron(MEDIA_HOOK_SUBSCRIBE_MANAGER,millis, this::execute);
        this.redisMessageListenerContainer = redisMessageListenerContainer;
    }
    /**
     * 对订阅数据进行过期清理
     */
    public void execute(){
        Date dateTime =new Date();
        for (HookType hookType : allSubscribes.keySet()) {
            Map<HookKey, HookEvent> hookSubscribeEventMap = allSubscribes.get(hookType);
            if (hookSubscribeEventMap.size() > 0) {
                for (HookKey hookSubscribe : hookSubscribeEventMap.keySet()) {
                    if (dateTime.compareTo(hookSubscribe.getExpires()) > 0) {
                        // 过期的
                        removeSubscribe(hookSubscribe);
                    }
                }
            }
        }
        log.info("[定时任务] 对订阅数据进行过期清理, allSubscribes : {},",allSubscribes.size());
    }
    /**
     * 获取某个类型的所有的订阅
     * @param type
     * @return
     */
    public List<HookEvent> getSubscribes(HookType type) {
        Map<HookKey, HookEvent> eventMap = allSubscribes.get(type);
        if (eventMap == null) {
            return null;
        }
        List<HookEvent> result = new ArrayList<>();
        for (HookKey key : eventMap.keySet()) {
            result.add(eventMap.get(key));
        }
        return result;
    }

    public void sendNotify(MediaHookVo vo) {
        Map<String, Object> map = HookKeyFactory.buildContent(vo);
        String key = String.format("%s:%s:%s",MEDIA_HOOK_SUBSCRIBE_MANAGER,vo.getType(), JSONUtil.toJsonStr(map));
        RedisUtils.redisTemplate.convertAndSend(key,vo);
    }

    public void addSubscribe(HookKey hookSubscribe, HookEvent event) {
        String key = String.format("%s:%s:%s",MEDIA_HOOK_SUBSCRIBE_MANAGER,hookSubscribe.getHookType(), JSONUtil.toJsonStr(hookSubscribe.getContent()));
        AbstractMessageListener abstractMessageListener = new AbstractMessageListener(key){
            @Override
            public void onMessage(Message message, byte[] pattern) {
                Object body = RedisUtils.redisTemplate.getValueSerializer().deserialize(message.getBody());
                MediaHookVo event = (MediaHookVo) body;
                if(event == null){
                    return;
                }
                Map<String, Object> hookResponse = BeanUtil.beanToMap(event.getHookVo());
                Map<HookKey, HookEvent> eventMap = allSubscribes.get(event.getType());
                if (eventMap == null) {
                    return;
                }
                if(event.getOnAll() == ConstEnum.Flag.YES.getValue()){
                    for (HookEvent value : eventMap.values()) {
                        value.response(event.getMediaServerVo(),event.getHookVo());
                    }
                    return;
                }
                for (Map.Entry<HookKey, HookEvent> entry : eventMap.entrySet()) {
                    if (isExist(entry.getKey(),hookResponse)) {
                        entry.getValue().response(event.getMediaServerVo(),event.getHookVo());
                    }
                }
            }
        };
        redisMessageListenerContainer.addMessageListener(abstractMessageListener,new PatternTopic(abstractMessageListener.getPatternTopicName()));
        allListenerMap.put(key,abstractMessageListener);
        allSubscribes.computeIfAbsent(hookSubscribe.getHookType(), k -> new HashMap<>()).put(hookSubscribe, event);
    }

    public void removeSubscribe(HookKey hookSubscribe) {
        Map<HookKey, HookEvent> eventMap = allSubscribes.get(hookSubscribe.getHookType());
        if (eventMap == null || eventMap.isEmpty()) {
            return;
        }
        List<Map.Entry<HookKey, HookEvent>> entriesToRemove = new ArrayList<>();
        for (Map.Entry<HookKey, HookEvent> entry : eventMap.entrySet()) {
            if (isExist(entry.getKey(),hookSubscribe.getContent())){
                entriesToRemove.add(entry);
            }
        }
        if (CollectionUtils.isEmpty(entriesToRemove)) {
            return;
        }
        for (Map.Entry<HookKey, HookEvent> entry : entriesToRemove) {
            String key = String.format("%s:%s:%s",MEDIA_HOOK_SUBSCRIBE_MANAGER,entry.getKey().getHookType(), JSONUtil.toJsonStr(entry.getKey().getContent()));
            AbstractMessageListener abstractMessageListener = allListenerMap.remove(key);
            eventMap.remove(entry.getKey());
            if(abstractMessageListener != null){
                redisMessageListenerContainer.removeMessageListener(abstractMessageListener,new PatternTopic(abstractMessageListener.getPatternTopicName()));
            }
        }
        if (eventMap.isEmpty()) {
            allSubscribes.remove(hookSubscribe.getHookType());
        }
    }
    public HookKey getHookKey(HookKey hookSubscribe){
        Map<HookKey, HookEvent> eventMap = allSubscribes.get(hookSubscribe.getHookType());
        if (eventMap == null || eventMap.isEmpty()) {
            return null;
        }
        HookKey hookKey = null;
        for (Map.Entry<HookKey, HookEvent> entry : eventMap.entrySet()) {
            if (isExist(entry.getKey(),hookSubscribe.getContent())){
                hookKey =entry.getKey();
                break;
            }
        }
        return hookKey;
    }
    private boolean isExist(HookKey key, Map<String,Object> map){
        boolean is = false;
        Map<String, Object> content = key.getContent();
        if(content == null || content.isEmpty()){
            return true;
        }
        for (String s : key.getContent().keySet()) {
            Object val = key.getContent().get(s);
            if (val== null) {
                continue;
            }
            if(val.equals(map.get(s))){
                is = true;
            }else {
                is = false;
            }
            if(!is){
                break;
            }
        }
        return is;
    }
}
