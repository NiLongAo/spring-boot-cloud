package cn.com.tzy.springbootstartervideocore.redis.subscribe.notify;

import cn.com.tzy.springbootstarterredis.pool.AbstractMessageListener;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.vo.sip.Address;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideocore.demo.DeviceNotifyVo;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.sip.SipServer;
import cn.com.tzy.springbootstartervideocore.sip.cmd.SIPCommander;
import cn.hutool.core.codec.Base64;
import cn.hutool.extra.spring.SpringUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.header.ToHeader;
import java.text.ParseException;

@Log4j2
public class DeviceNotifyHandle extends AbstractMessageListener {

    @Resource
    private DynamicTask dynamicTask;

    /**
     * 设备报警订阅缓存key(服务器共享数据)
     */
    public static final String VIDEO_DEVICE_ALARM_NOTIFY_SUBSCRIBE = VideoConstant.VIDEO_DEVICE_ALARM_NOTIFY_SUBSCRIBE;
    /**
     * 设备目录订阅缓存key(服务器共享数据)
     */
    public static final String VIDEO_DEVICE_CATALOG_NOTIFY_SUBSCRIBE = VideoConstant.VIDEO_DEVICE_CATALOG_NOTIFY_SUBSCRIBE;
    /**
     * 设备移动位置订阅缓存key(服务器共享数据)
     */
    public static final String VIDEO_DEVICE_MOBILE_POSITION_NOTIFY_SUBSCRIBE = VideoConstant.VIDEO_DEVICE_MOBILE_POSITION_NOTIFY_SUBSCRIBE;


    public DeviceNotifyHandle() {
        super(VideoConstant.VIDEO_DEVICE_NOTIFY_SUBSCRIBE);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Object body = RedisUtils.redisTemplate.getValueSerializer().deserialize(message.getBody());
        DeviceNotifyVo event = (DeviceNotifyVo) body;
        if(event == null){
            log.error("[订阅消息]：消息接收异常！");
            return;
        }
        Address device = RedisService.getRegisterServerManager().getDevice(event.getGbId());
        if(device == null){
            log.warn("[订阅消息]：当前设备：{}，未注册在此服务",event.getGbId());
            return;
        }
        if(event.getType().equals(DeviceNotifyVo.TypeEnum.CATALOG.getValue())){
            if(event.getOperate().equals(DeviceNotifyVo.OperateEnum.ADD.getValue())){
                addCatalogSubscribe(event.getGbId());
            }else if(event.getOperate().equals(DeviceNotifyVo.OperateEnum.DEL.getValue())){
                delCatalogSubscribe(event.getGbId());
            }else {
                log.error("[订阅消息]：消息操作类型错误！");
            }
        }else if(event.getType().equals(DeviceNotifyVo.TypeEnum.MOBILE_POSITION.getValue())){
            if(event.getOperate().equals(DeviceNotifyVo.OperateEnum.ADD.getValue())){
                addMobilePositionSubscribe(event.getGbId());
            }else if(event.getOperate().equals(DeviceNotifyVo.OperateEnum.DEL.getValue())){
                delMobilePositionSubscribe(event.getGbId());
            }else {
                log.error("[订阅消息]：消息操作类型错误！");
            }
        }else if(event.getType().equals(DeviceNotifyVo.TypeEnum.ALARM.getValue())){
            if(event.getOperate().equals(DeviceNotifyVo.OperateEnum.ADD.getValue())){
                addAlarmSubscribe(event.getGbId());
            }else if(event.getOperate().equals(DeviceNotifyVo.OperateEnum.DEL.getValue())){
                delAlarmSubscribe(event.getGbId());
            }else {
                log.error("[订阅消息]：消息操作类型错误！");
            }
        }else {
            log.error("[订阅消息]：消息类型错误！");
        }
    }

    private void addCatalogSubscribe(String gbId){
        SipServer sipServer = SpringUtil.getBean(SipServer.class);
        SIPCommander sipCommander = SpringUtil.getBean(SIPCommander.class);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(gbId);
        if(deviceVo == null || deviceVo.getSubscribeCycleForCatalog() < 0){
            return;
        }
        String key = String.format("%s%s",VIDEO_DEVICE_CATALOG_NOTIFY_SUBSCRIBE, deviceVo.getDeviceId());
        if(dynamicTask.contains(key)){
            return;
        }
        dynamicTask.startCron(key, deviceVo.getSubscribeCycleForCatalog(),()->{
            SIPRequest request = null;
            Object req = RedisUtils.get(key);
            if(ObjectUtils.isNotEmpty(req)){
                Object deserialize = SerializationUtils.deserialize(Base64.decode((String) req));
                request = (SIPRequest) deserialize;
            }
            SIPRequest sipRequest = null;
            try {
                sipRequest = sipCommander.catalogSubscribe(sipServer, deviceVo, request, eventResult -> {
                    ResponseEvent event = (ResponseEvent) eventResult.getEvent();
                    // 成功
                    log.info("[目录订阅]成功： {}", deviceVo.getDeviceId());
                    ToHeader toHeader = (ToHeader)event.getResponse().getHeader(ToHeader.NAME);
                    Object o = RedisUtils.get(key);
                    if(ObjectUtils.isEmpty(o)){
                        return;
                    }
                    Object deserialize = SerializationUtils.deserialize(Base64.decode((String) o));
                    SIPRequest rq = (SIPRequest) deserialize;
                    try {
                        rq.getToHeader().setTag(toHeader.getTag());
                        long expire = RedisUtils.getExpire(key);
                        RedisUtils.set(key,SerializationUtils.serialize(rq),expire);
                    } catch (ParseException e) {
                        log.info("[目录订阅]成功： 但为request设置ToTag失败");
                        RedisUtils.del(key);
                    }

                },eventResult -> {
                    RedisUtils.del(key);
                    // 失败
                    log.warn("[目录订阅]失败，信令发送失败： {}-{} ", deviceVo.getDeviceId(), eventResult.getMsg());
                });
            } catch (InvalidArgumentException | SipException | ParseException e) {
                log.error("[命令发送失败] 目录订阅: {}", e.getMessage());
            }
            if (sipRequest != null) {
                RedisUtils.set(key, SerializationUtils.serialize(sipRequest),deviceVo.getSubscribeCycleForCatalog()+VideoConstant.DELAY_TIME);
            }

        });
    }

    private void delCatalogSubscribe(String gbId){
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(gbId);
        if(deviceVo == null){
            return;
        }
        log.info("[移除目录订阅]: {}", deviceVo.getDeviceId());
        String key = String.format("%s%s",VIDEO_DEVICE_CATALOG_NOTIFY_SUBSCRIBE, deviceVo.getDeviceId());
        dynamicTask.stop(key);
        RedisUtils.del(key);
    }

    private void addMobilePositionSubscribe(String gbId){
        SipServer sipServer = SpringUtil.getBean(SipServer.class);
        SIPCommander sipCommander = SpringUtil.getBean(SIPCommander.class);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(gbId);
        if (deviceVo == null || deviceVo.getSubscribeCycleForMobilePosition() < 0) {
            return;
        }
        String key = String.format("%s%s",VIDEO_DEVICE_MOBILE_POSITION_NOTIFY_SUBSCRIBE, deviceVo.getDeviceId());
        if(dynamicTask.contains(key)){
            return;
        }
        dynamicTask.startCron(key, deviceVo.getSubscribeCycleForMobilePosition(),()->{
            SIPRequest request = null;
            Object req = RedisUtils.get(key);
            if(ObjectUtils.isNotEmpty(req)){
                Object deserialize = SerializationUtils.deserialize(Base64.decode((String) req));
                request = (SIPRequest) deserialize;
            }
            SIPRequest sipRequest = null;
            try {
                sipRequest = sipCommander.mobilePositionSubscribe(sipServer, deviceVo,null, request, eventResult -> {
                    // 成功
                    log.info("[移动位置订阅]成功： {}", deviceVo.getDeviceId());
                    Object o = RedisUtils.get(key);
                    if(ObjectUtils.isEmpty(o)){
                        return;
                    }
                    Object deserialize = SerializationUtils.deserialize(Base64.decode((String) o));
                    SIPRequest rq = (SIPRequest) deserialize;
                    ResponseEvent event = (ResponseEvent) eventResult.getEvent();
                    ToHeader toHeader = (ToHeader)event.getResponse().getHeader(ToHeader.NAME);
                    try {
                        rq.getToHeader().setTag(toHeader.getTag());
                        long expire = RedisUtils.getExpire(key);
                        RedisUtils.set(key,SerializationUtils.serialize(rq),expire);
                    } catch (ParseException e) {
                        log.info("[移动位置订阅]成功： 为request设置ToTag失败");
                        RedisUtils.del(key);
                    }
                },eventResult -> {
                    RedisUtils.del(key);
                    // 失败
                    log.warn("[移动位置订阅]失败，信令发送失败： {}-{} ", deviceVo.getDeviceId(), eventResult.getMsg());
                });
            } catch (InvalidArgumentException | SipException | ParseException e) {
                log.error("[命令发送失败] 移动位置订阅: {}", e.getMessage());
            }
            if (sipRequest != null) {
                RedisUtils.set(key,SerializationUtils.serialize(sipRequest),deviceVo.getSubscribeCycleForMobilePosition()+VideoConstant.DELAY_TIME);
            }
        });
    }

    private void delMobilePositionSubscribe(String gbId){
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(gbId);
        if(deviceVo == null){
            return;
        }
        log.info("[移除目录订阅]: {}", deviceVo.getDeviceId());
        String key = String.format("%s%s",VIDEO_DEVICE_MOBILE_POSITION_NOTIFY_SUBSCRIBE, deviceVo.getDeviceId());
        dynamicTask.stop(key);
        RedisUtils.del(key);
    }

    private void addAlarmSubscribe(String gbId){
        SipServer sipServer = SpringUtil.getBean(SipServer.class);
        SIPCommander sipCommander = SpringUtil.getBean(SIPCommander.class);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(gbId);
        if(deviceVo == null || deviceVo.getSubscribeCycleForAlarm() < 0){
            return;
        }
        String key = String.format("%s%s",VIDEO_DEVICE_ALARM_NOTIFY_SUBSCRIBE, deviceVo.getDeviceId());
        if(dynamicTask.contains(key)){
            return;
        }
        dynamicTask.startCron(key, deviceVo.getSubscribeCycleForAlarm(),()->{
            SIPRequest request = null;
            Object req = RedisUtils.get(key);
            if(ObjectUtils.isNotEmpty(req)){
                Object deserialize = SerializationUtils.deserialize(Base64.decode((String) req));
                request = (SIPRequest) deserialize;
            }
            SIPRequest sipRequest = null;
            try {
                sipRequest = sipCommander.alarmSubscribe(sipServer, deviceVo,null, request, eventResult -> {
                    ResponseEvent event = (ResponseEvent) eventResult.getEvent();
                    // 成功
                    log.info("[报警订阅]成功： {}", deviceVo.getDeviceId());
                    ToHeader toHeader = (ToHeader)event.getResponse().getHeader(ToHeader.NAME);
                    Object o = RedisUtils.get(key);
                    if(ObjectUtils.isEmpty(o)){
                        return;
                    }
                    Object deserialize = SerializationUtils.deserialize(Base64.decode((String) o));
                    SIPRequest rq = (SIPRequest) deserialize;
                    try {
                        rq.getToHeader().setTag(toHeader.getTag());
                        long expire = RedisUtils.getExpire(key);
                        RedisUtils.set(key,SerializationUtils.serialize(rq),expire);
                    } catch (ParseException e) {
                        log.info("[报警订阅]成功： 但为request设置ToTag失败");
                        RedisUtils.del(key);
                    }

                },eventResult -> {
                    RedisUtils.del(key);
                    // 失败
                    log.warn("[报警订阅]失败，信令发送失败： {}-{} ", deviceVo.getDeviceId(), eventResult.getMsg());
                });
            } catch (InvalidArgumentException | SipException | ParseException e) {
                log.error("[命令发送失败] 报警订阅: {}", e.getMessage());
            }
            if (sipRequest != null) {
                RedisUtils.set(key, SerializationUtils.serialize(sipRequest),deviceVo.getSubscribeCycleForCatalog()+VideoConstant.DELAY_TIME);
            }

        });
    }
    private void delAlarmSubscribe(String gbId){
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(gbId);
        if(deviceVo == null){
            return;
        }
        log.info("[移除报警订阅]: {}", deviceVo.getDeviceId());
        String key = String.format("%s%s",VIDEO_DEVICE_ALARM_NOTIFY_SUBSCRIBE, deviceVo.getDeviceId());
        dynamicTask.stop(key);
        RedisUtils.del(key);
    }
}
