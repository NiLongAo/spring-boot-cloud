package cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.notify.cmd;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.enums.CmdType;
import cn.com.tzy.springbootstartervideobasic.vo.sip.Address;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceVoService;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.SipResponseEvent;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.MessageHandler;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.notify.NotifyMessageHandler;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstartervideocore.utils.SipUtils;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Response;
import java.text.ParseException;

/**
 * 状态信息(心跳)报送
 */
@Log4j2
public class KeepaliveNotifyMessageHandler extends SipResponseEvent implements MessageHandler {

    @Resource
    private DynamicTask dynamicTask;
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    public KeepaliveNotifyMessageHandler(NotifyMessageHandler handler){
        handler.setMessageHandler(CmdType.KEEPALIVE_NOTIFY.getValue(),this);
    }

    @Override
    public void handForDevice(RequestEvent evt, DeviceVo deviceVo, Element element) {
        if (deviceVo == null) {
            // 未注册的设备不做处理
            return;
        }
        DateTime date = DateUtil.date();
        DeviceVoService deviceVoService = VideoService.getDeviceService();
        SIPRequest request = (SIPRequest) evt.getRequest();
        log.info("[收到心跳]， device: {}", deviceVo.getDeviceId());
        if(deviceVo.getOnline() == 0 || deviceVo.expire()){
            if(deviceVo.getOnline() == 0){
                log.info("设备离线，重新注册");
            }else {
                log.info("设备注册过期，重新注册");
            }
            // 注册时间过期，需重新注册
            try {
                responseAck(request, Response.UNAUTHORIZED,"注册过期");
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] 心跳回复: {}", e.getMessage());
            }
            if(deviceVo.getOnline() == ConstEnum.Flag.YES.getValue()){
                deviceVoService.offline(deviceVo.getDeviceId());
            }
            return;
        }
        // 回复200 OK
        try {
            responseAck(request, Response.OK,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 心跳回复: {}", e.getMessage());
        }
        long between = 60L;
        if(deviceVo.getKeepaliveTime() != null){
            between = DateUtil.between(deviceVo.getKeepaliveTime(), date, DateUnit.SECOND);
            if (between <= 3){
                log.info("[收到心跳] 心跳发送过于频繁，已忽略 device: {}, callId: {}", deviceVo.getDeviceId(), request.getCallIdHeader().getCallId());
                return;
            }
        }
        // 刷新过期任务,如果三次心跳失败，则设置设备离线
        dynamicTask.startDelay(String.format("%s_%s", VideoConstant.REGISTER_EXPIRE_TASK_KEY_PREFIX, deviceVo.getDeviceId()), deviceVo.getKeepaliveIntervalTime()*3,()-> deviceVoService.offline(deviceVo.getDeviceId()));
        Address remoteAddressInfo = SipUtils.getRemoteAddressFromRequest(request, videoProperties.getSipUseSourceIpAsRemoteAddress());
        if (!deviceVo.getIp().equalsIgnoreCase(remoteAddressInfo.getIp()) || deviceVo.getPort() != remoteAddressInfo.getPort()) {
            log.info("[心跳] 设备{}地址变化, 远程地址为: {}:{}", deviceVo.getDeviceId(), remoteAddressInfo.getIp(), remoteAddressInfo.getPort());
            deviceVo.setPort(remoteAddressInfo.getPort());
            deviceVo.setHostAddress(remoteAddressInfo.getIp().concat(":").concat(String.valueOf(remoteAddressInfo.getPort())));
            deviceVo.setIp(remoteAddressInfo.getIp());
            // 设备地址变化会引起目录订阅任务失效，需要重新添加
            if (RedisService.getDeviceNotifySubscribeManager().getCatalogSubscribe(deviceVo.getDeviceId())) {
                RedisService.getDeviceNotifySubscribeManager().removeCatalogSubscribe(deviceVo);
                RedisService.getDeviceNotifySubscribeManager().addCatalogSubscribe(deviceVo);
            }
            if(RedisService.getDeviceNotifySubscribeManager().getMobilePositionSubscribe(deviceVo.getDeviceId())){
                RedisService.getDeviceNotifySubscribeManager().removeMobilePositionSubscribe(deviceVo);
                RedisService.getDeviceNotifySubscribeManager().addMobilePositionSubscribe(deviceVo);
            }
            if( RedisService.getDeviceNotifySubscribeManager().getAlarmSubscribe(deviceVo.getDeviceId())){
                RedisService.getDeviceNotifySubscribeManager().removeAlarmSubscribe(deviceVo);
                RedisService.getDeviceNotifySubscribeManager().addAlarmSubscribe(deviceVo);
            }
        }
        if (deviceVo.getKeepaliveTime() == null) {
            deviceVo.setKeepaliveIntervalTime(60);
        }else {
            if (between > 10) {
                deviceVo.setKeepaliveIntervalTime((int)between);
            }
        }
        deviceVo.setKeepaliveTime(date);
        if (deviceVo.getOnline() == ConstEnum.Flag.YES.getValue()) {
            deviceVoService.save(deviceVo);
        }else{
            deviceVoService.online(deviceVo,sipServer,sipCommander,videoProperties,null);
        }
        //缓存设备注册服务
        RedisService.getRegisterServerManager().putDevice(deviceVo.getDeviceId(),deviceVo.getKeepaliveIntervalTime()+ VideoConstant.DELAY_TIME ,Address.builder().gbId(deviceVo.getDeviceId()).ip(nacosDiscoveryProperties.getIp()).port(nacosDiscoveryProperties.getPort()).build());
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatformVo parentPlatformVo, Element element) {
        // 个别平台保活不回复200OK会判定离线
        try {
            responseAck((SIPRequest) evt.getRequest(), Response.OK,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 心跳回复: {}", e.getMessage());
        }
    }
}
