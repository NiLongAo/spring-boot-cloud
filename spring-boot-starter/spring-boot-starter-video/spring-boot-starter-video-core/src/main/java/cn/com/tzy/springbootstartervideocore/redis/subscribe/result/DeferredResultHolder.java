package cn.com.tzy.springbootstartervideocore.redis.subscribe.result;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootstarterredis.pool.AbstractMessageListener;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideocore.demo.DeferredResultVo;
import cn.com.tzy.springbootstartervideocore.demo.VideoRestResult;
import cn.com.tzy.springbootstartervideocore.pool.task.DynamicTask;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异步请求轮训类
 * @param 
 */
@Log4j2
public class DeferredResultHolder {
    private static final Integer millis = 10;
    private static final String VIDEO_DEFERRED_RESULT_HOLDER = VideoConstant.VIDEO_DEFERRED_RESULT_HOLDER;

    /**
     * 播放回调
     */
    public static final String PLAY_CALLBACK = "play_callback_";
    /**
     * 报警回复
     */
    public static final String CALLBACK_CMD_ALARM = "callback_alarm_";
    /**
     * 广播回复
     */
    public static final String CALLBACK_CMD_BROADCAST = "callback_broadcast_";
    /**
     * 设备下载回复
     */
    public static final String CALLBACK_CMD_CONFIGDOWNLOAD = "callback_configdownload_";
    /**
     * 设备控制命令
     */
    public static final String CALLBACK_CMD_DEVICECONTROL = "callback_devicecontrol_";

    /**
     * 设备配置命令
     */
    public static final String CALLBACK_CMD_DEVICECONFIG = "callback_deviceconfig_";
    public static final String CALLBACK_CMD_DEVICEINFO = "callback_deviceinfo_";
    public static final String CALLBACK_CMD_DEVICESTATUS = "callback_devicestatus_";
    /**
     * 点播信息key
     */
    public static final String CALLBACK_CMD_PLAY = "callback_play_";
    /**
     * 停止点播key
     */
    public static final String CALLBACK_CMD_STOP = "callback_stop_";
    /**
     * 预置位查询
     */
    public static final String CALLBACK_CMD_PRESETQUERY = "callback_presetquery_";
    /**
     * 历史录像播放信息key
     */
    public static final String CALLBACK_CMD_PLAYBACK = "callback_playback_";
    /**
     * 历史录像信息key
     */
    public static final String CALLBACK_CMD_RECORDINFO = "callback_recordinfo_";
    /**
     * 历史录像下载信息key
     */
    public static final String CALLBACK_CMD_DOWNLOAD = "callback_download_";
    /**
     * 获取移动位置信息
     */
    public static final String CALLBACK_CMD_MOBILEPOSITION = "callback_mobileposition_";
    /**
     * 获取移动位置信息
     */
    public static final String CALLBACK_STREAM_NONE_READER = "callback_stream_none_reader_";


    private Map<String, Map<String,Date>> resultDateMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, DeferredResult>> resultMap = new ConcurrentHashMap<>();
    private final Map<String, AbstractMessageListener> resultListenerMap = new ConcurrentHashMap<>();

    public DeferredResultHolder(DynamicTask dynamicTask){
        dynamicTask.startCron(VIDEO_DEFERRED_RESULT_HOLDER,millis, this::execute);
    }
    /**
     * 对订阅数据进行过期清理
     */
    public void execute(){
        Date date = new Date();
        for (Map.Entry<String, Map<String, Date>> entry : resultDateMap.entrySet()) {
            for (Map.Entry<String, Date> dateEntry : entry.getValue().entrySet()) {
                if(DateUtil.compare(date,dateEntry.getValue()) > 0){
                    del(entry.getKey(),dateEntry.getKey());
                }
            }
        }
        log.info("[定时任务] 清理查询异步回调, resultDateMap : {},resultMap : {}",resultDateMap.size(),resultMap.size());
    }

    public boolean exist(String key, String id){
        if (key == null) {
            return false;
        }
        Map<String, DeferredResult> deferredResultMap = resultMap.get(key);
        if (id == null) {
            return deferredResultMap != null;
        }else {
            return deferredResultMap != null && deferredResultMap.get(id) != null;
        }
    }

    /**
     * 释放单个请求
     */
    public void invokeResult(String key,String id,Object data) {
        RedisUtils.redisTemplate.convertAndSend(VIDEO_DEFERRED_RESULT_HOLDER,DeferredResultVo.builder().onAll(ConstEnum.Flag.NO.getValue()).key(key).id(id).data(data).build());
    }

    /**
     * 释放所有的请求
     */
    public void invokeAllResult(String key,Object data) {
        RedisUtils.redisTemplate.convertAndSend(VIDEO_DEFERRED_RESULT_HOLDER,DeferredResultVo.builder().onAll(ConstEnum.Flag.YES.getValue()).key(key).id(null).data(data).build());
    }

    public void put(String key, String id, VideoRestResult result) {
        RedisMessageListenerContainer redisMessageListenerContainer = SpringUtil.getBean(RedisMessageListenerContainer.class);
        Map<String, DeferredResult> deferredResultMap = resultMap.computeIfAbsent(key, o -> new ConcurrentHashMap<>());
        deferredResultMap.put(id, result);
        Map<String, Date> stringDateMap = resultDateMap.computeIfAbsent(key, o -> new ConcurrentHashMap<>());
        stringDateMap.put(id, DateUtil.offsetSecond(new Date(),((int) (result.getTimeoutValue()/1000))));
        //添加订阅
        AbstractMessageListener abstractMessageListener = new AbstractMessageListener(VIDEO_DEFERRED_RESULT_HOLDER){
            @Override
            public  void onMessage(Message message, byte[] pattern) {
                Object body = RedisUtils.redisTemplate.getValueSerializer().deserialize(message.getBody());
                DeferredResultVo vo = (DeferredResultVo) body;
                if(vo == null){
                    return;
                }
                Map<String, DeferredResult> deferredResultMap = resultMap.get(key);
                if (deferredResultMap == null) {
                    return;
                }
                if(vo.getOnAll() == ConstEnum.Flag.YES.getValue()){
                    for (Map.Entry<String, DeferredResult> entry : deferredResultMap.entrySet()) {
                        entry.getValue().setResult(vo.getData());
                        del(key,entry.getKey());
                    }
                }else {
                    DeferredResult deferredResult = deferredResultMap.get(vo.getId());
                    if(deferredResult != null){
                        deferredResult.setResult(vo.getData());
                        del(key,vo.getId());
                    }
                }
            }
        };
        redisMessageListenerContainer.addMessageListener(abstractMessageListener,new PatternTopic(abstractMessageListener.getPatternTopicName()));
        String k = String.format("%s%s:%s",VIDEO_DEFERRED_RESULT_HOLDER,key,id);
        resultListenerMap.put(k,abstractMessageListener);
    }

    public void del(String key, String id) {
        if(StringUtils.isEmpty(id)){
            log.error("[请求回调] : 未获取删除回调 ID");
            return;
        }
        Map<String, DeferredResult> deferredResultMap = resultMap.get(key);
        if (deferredResultMap != null && !deferredResultMap.isEmpty()) {
            deferredResultMap.remove(id);
        }
        if(deferredResultMap == null || deferredResultMap.isEmpty()){
            resultMap.remove(key);
        }
        Map<String, Date> stringDateMap = resultDateMap.get(key);
        if (stringDateMap != null && !stringDateMap.isEmpty()) {
            stringDateMap.remove(id);
        }
        if(stringDateMap == null || stringDateMap.isEmpty()){
            resultDateMap.remove(key);
        }
        String k = String.format("%s%s:%s",VIDEO_DEFERRED_RESULT_HOLDER,key,id);
        RedisMessageListenerContainer redisMessageListenerContainer = SpringUtil.getBean(RedisMessageListenerContainer.class);
        AbstractMessageListener abstractMessageListener = resultListenerMap.remove(k);
        if(abstractMessageListener != null){
            redisMessageListenerContainer.removeMessageListener(abstractMessageListener);
        }
    }
}
