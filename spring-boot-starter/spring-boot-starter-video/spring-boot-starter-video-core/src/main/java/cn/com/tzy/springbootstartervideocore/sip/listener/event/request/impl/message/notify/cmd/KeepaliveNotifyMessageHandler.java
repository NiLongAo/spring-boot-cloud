package cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.notify.cmd;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
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
import cn.com.tzy.springbootstartervideocore.utils.SipUtils;
import cn.hutool.core.date.DateTime;
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
        // 回复200 OK
        try {
            responseAck(request, Response.OK,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 心跳回复: {}", e.getMessage());
        }
        Address remoteAddressInfo = SipUtils.getRemoteAddressFromRequest(request, videoProperties.getSipUseSourceIpAsRemoteAddress());
        if (!deviceVo.getIp().equalsIgnoreCase(remoteAddressInfo.getIp()) || deviceVo.getPort() != remoteAddressInfo.getPort()) {
            log.info("[心跳] 设备{}地址变化, 远程地址为: {}:{}", deviceVo.getDeviceId(), remoteAddressInfo.getIp(), remoteAddressInfo.getPort());
            deviceVo.setPort(remoteAddressInfo.getPort());
            deviceVo.setHostAddress(remoteAddressInfo.getIp().concat(":").concat(String.valueOf(remoteAddressInfo.getPort())));
            deviceVo.setIp(remoteAddressInfo.getIp());
            deviceVo.setLocalIp(request.getLocalAddress().getHostAddress());
            // 设备地址变化会引起目录订阅任务失效，需要重新添加
            if (RedisService.getDeviceNotifySubscribeManager().getCatalogSubscribe(deviceVo.getDeviceId())) {
                RedisService.getDeviceNotifySubscribeManager().removeCatalogSubscribe(deviceVo);
                RedisService.getDeviceNotifySubscribeManager().addCatalogSubscribe(deviceVo);
            }
            if(RedisService.getDeviceNotifySubscribeManager().getMobilePositionSubscribe(deviceVo.getDeviceId())){
                RedisService.getDeviceNotifySubscribeManager().removeMobilePositionSubscribe(deviceVo);
                RedisService.getDeviceNotifySubscribeManager().addMobilePositionSubscribe(deviceVo);
            }
            if(RedisService.getDeviceNotifySubscribeManager().getAlarmSubscribe(deviceVo.getDeviceId())){
                RedisService.getDeviceNotifySubscribeManager().removeAlarmSubscribe(deviceVo);
                RedisService.getDeviceNotifySubscribeManager().addAlarmSubscribe(deviceVo);
            }
        }
        if (deviceVo.getKeepaliveTime() == null) {
            deviceVo.setHeartBeatInterval(60);
        }
        deviceVo.setKeepaliveTime(date);
        if (deviceVo.getOnline() == ConstEnum.Flag.YES.getValue()) {
            if (deviceVo.getSubscribeCycleForCatalog() > 0 && !RedisService.getDeviceNotifySubscribeManager().getCatalogSubscribe(deviceVo.getDeviceId())) {
                // 查询在线设备那些开启了订阅，为设备开启定时的目录订阅
                RedisService.getDeviceNotifySubscribeManager().addCatalogSubscribe(deviceVo);
            }
            if (deviceVo.getSubscribeCycleForMobilePosition() > 0 && !RedisService.getDeviceNotifySubscribeManager().getMobilePositionSubscribe(deviceVo.getDeviceId())) {
                RedisService.getDeviceNotifySubscribeManager().addMobilePositionSubscribe(deviceVo);
            }
            if (deviceVo.getSubscribeCycleForAlarm() > 0 && !RedisService.getDeviceNotifySubscribeManager().getAlarmSubscribe(deviceVo.getDeviceId())) {
                RedisService.getDeviceNotifySubscribeManager().addAlarmSubscribe(deviceVo);
            }
            deviceVoService.save(deviceVo);
        }else{
            deviceVoService.online(deviceVo,sipServer,sipCommander,videoProperties,null,"设备心跳");
        }
        // 刷新过期任务,如果三次心跳失败，则设置设备离线
        dynamicTask.startDelay(String.format("%s_%s", VideoConstant.REGISTER_EXPIRE_TASK_KEY_PREFIX, deviceVo.getDeviceId()), deviceVo.getHeartBeatInterval()*Math.max(deviceVo.getHeartBeatCount(),2),()-> deviceVoService.offline(deviceVo.getDeviceId(),"设备心跳-设备过期任务"));
        //缓存设备注册服务
        RedisService.getRegisterServerManager().putDevice(deviceVo.getDeviceId(),deviceVo.getHeartBeatInterval()+ VideoConstant.DELAY_TIME * 2 ,Address.builder().gbId(deviceVo.getDeviceId()).ip(nacosDiscoveryProperties.getIp()).port(nacosDiscoveryProperties.getPort()).build());
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
