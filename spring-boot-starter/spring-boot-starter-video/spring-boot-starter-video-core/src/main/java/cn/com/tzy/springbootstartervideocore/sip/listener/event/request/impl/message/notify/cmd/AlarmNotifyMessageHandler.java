package cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.notify.cmd;

import cn.com.tzy.springbootstartervideocore.properties.SipConfigProperties;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceAlarmVoService;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceChannelVoService;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceMobilePositionVoService;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceVoService;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.SipResponseEvent;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.MessageHandler;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.notify.NotifyMessageHandler;
import cn.com.tzy.springbootstartervideobasic.enums.CmdType;
import cn.com.tzy.springbootstartervideobasic.enums.DeviceAlarmMethod;
import cn.com.tzy.springbootstartervideobasic.vo.video.*;
import cn.com.tzy.springbootstartervideocore.utils.XmlUtils;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.GlobalThreadPool;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Element;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 报警事件的处理，参考：9.4
 */
@Log4j2
public class AlarmNotifyMessageHandler extends SipResponseEvent implements MessageHandler {


    public AlarmNotifyMessageHandler(NotifyMessageHandler handler){
        handler.setMessageHandler(CmdType.ALARM_NOTIFY.getValue(),this);
    }

    @Override
    public void handForDevice(RequestEvent evt, DeviceVo deviceVo, Element element) {
        log.info("[收到报警通知]设备：{}", deviceVo.getDeviceId());
        // 回复200 OK
        try {
            responseAck((SIPRequest) evt.getRequest(), Response.OK,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 报警通知回复: {}", e.getMessage());
        }
        DeviceVoService deviceVoService = VideoService.getDeviceService();
        DeviceAlarmVoService deviceAlarmVoService = VideoService.getDeviceAlarmService();
        DeviceChannelVoService deviceChannelVoService = VideoService.getDeviceChannelService();
        DeviceMobilePositionVoService deviceMobilePositionVoService = VideoService.getDeviceMobilePositionService();
        ThreadPoolExecutor executor = (ThreadPoolExecutor)GlobalThreadPool.getExecutor();
        log.info("[收到报警通知]:获取当前公共线程池状态 ：{}",executor.toString());
        executor.execute(()->{
            try {
                String channelId = XmlUtils.getText(element,"DeviceID");
                DeviceAlarmVo deviceAlarmVo = new DeviceAlarmVo();
                deviceAlarmVo.setCreateTime(new Date());
                deviceAlarmVo.setDeviceId(deviceVo.getDeviceId());
                deviceAlarmVo.setChannelId(channelId);
                deviceAlarmVo.setAlarmPriority(Integer.parseInt(XmlUtils.getText(element, "AlarmPriority")));
                deviceAlarmVo.setAlarmMethod(Integer.parseInt(XmlUtils.getText(element, "AlarmMethod")));
                String alarmTime = XmlUtils.getText(element, "AlarmTime");
                if (alarmTime == null) {
                    return;
                }
                deviceAlarmVo.setAlarmTime(DateUtil.parse(alarmTime,"yyyy-M-d'T'H:m:s"));
                String alarmDescription = XmlUtils.getText(element, "AlarmDescription");
                if (alarmDescription == null) {
                    deviceAlarmVo.setAlarmDescription("");
                } else {
                    deviceAlarmVo.setAlarmDescription(alarmDescription);
                }
                String longitude = XmlUtils.getText(element, "Longitude");
                if (NumberUtil.isNumber(longitude)) {
                    deviceAlarmVo.setLongitude(Double.parseDouble(longitude));
                } else {
                    deviceAlarmVo.setLongitude(0.00);
                }
                String latitude = XmlUtils.getText(element, "Latitude");
                if (NumberUtil.isNumber(latitude)) {
                    deviceAlarmVo.setLatitude(Double.parseDouble(latitude));
                } else {
                    deviceAlarmVo.setLatitude(0.00);
                }

                if (!ObjectUtils.isEmpty(deviceAlarmVo.getAlarmMethod())) {
                    if ( deviceAlarmVo.getAlarmMethod().equals(DeviceAlarmMethod.GPS.getVal())) {
                        DeviceMobilePositionVo mobilePosition = new DeviceMobilePositionVo();
                        mobilePosition.setCreateTime(new Date());
                        mobilePosition.setDeviceId(deviceAlarmVo.getDeviceId());
                        mobilePosition.setTime(deviceAlarmVo.getAlarmTime());
                        mobilePosition.setLongitude(deviceAlarmVo.getLongitude());
                        mobilePosition.setLatitude(deviceAlarmVo.getLatitude());
                        mobilePosition.setReportSource("GPS Alarm");

                        // 更新device channel 的经纬度
                        DeviceChannelVo deviceChannelVo = new DeviceChannelVo();
                        deviceChannelVo.setDeviceId(deviceVo.getDeviceId());
                        deviceChannelVo.setChannelId(channelId);
                        deviceChannelVo.setLongitude(mobilePosition.getLongitude());
                        deviceChannelVo.setLatitude(mobilePosition.getLatitude());
                        deviceChannelVo.setGpsTime(mobilePosition.getTime());
                        deviceChannelVo.initGps(deviceVo.getGeoCoordSys());

                        mobilePosition.setLongitudeWgs84(deviceChannelVo.getLongitudeWgs84());
                        mobilePosition.setLatitudeWgs84(deviceChannelVo.getLatitudeWgs84());
                        mobilePosition.setLongitudeGcj02(deviceChannelVo.getLongitudeGcj02());
                        mobilePosition.setLatitudeGcj02(deviceChannelVo.getLatitudeGcj02());

                        if (videoProperties.getSavePositionHistory()) {
                            deviceMobilePositionVoService.save(mobilePosition);
                        }
                        deviceChannelVoService.updateMobilePosition(deviceChannelVo);
                    }
                }
                if (!ObjectUtils.isEmpty(deviceAlarmVo.getDeviceId())) {
                    if (deviceAlarmVo.getAlarmMethod().equals(DeviceAlarmMethod.Video.getVal())) {
                        deviceAlarmVo.setAlarmType(Integer.parseInt(XmlUtils.getText(XmlUtil.getElement(element,"Info"), "AlarmType")));
                    }
                }
                log.info("[收到报警通知]内容：{}", JSONUtil.toJsonPrettyStr(deviceAlarmVo));
                // 作者自用判断，其他小伙伴需要此消息可以自行修改，但是不要提在pr里
                log.debug("存储报警信息、报警分类");
                // 存储报警信息、报警分类
                SipConfigProperties sipConfigProperties = sipServer.getSipConfigProperties();
                if (sipConfigProperties.getAlarm()) {
                    deviceAlarmVoService.insert(deviceAlarmVo);
                }
                //发送警报消息
                deviceAlarmVoService.sendAlarmMessage(sipServer,sipCommander,sipCommanderForPlatform,videoProperties,deviceAlarmVo);
                //if (deviceService.deviceIsOnline(deviceVo.getDeviceId())) {
                    //设备在线时推送报警消息
                    //publisher.deviceAlarmEventPublish(deviceAlarm);
                //}
            }catch (Exception e) {
                log.error("未处理的异常 ", e);
                log.warn("[收到报警通知] 发现未处理的异常, {}\r\n{}",e.getMessage(), evt.getRequest());
            }
        });
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatformVo parentPlatformVo, Element element) {
        log.info("收到来自平台[{}]的报警通知", parentPlatformVo.getServerGbId());
        DeviceAlarmVoService deviceAlarmVoService = VideoService.getDeviceAlarmService();
        // 回复200 OK
        try {
            responseAck((SIPRequest) evt.getRequest(), Response.OK,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 国标级联 报警通知回复: {}", e.getMessage());
        }
        String channelId = XmlUtils.getText(element,"DeviceID");

        DeviceAlarmVo deviceAlarmVo = new DeviceAlarmVo();
        deviceAlarmVo.setCreateTime(new Date());
        deviceAlarmVo.setDeviceId(parentPlatformVo.getServerGbId());
        deviceAlarmVo.setChannelId(channelId);
        deviceAlarmVo.setAlarmPriority(Integer.parseInt(XmlUtils.getText(element, "AlarmPriority")));
        deviceAlarmVo.setAlarmMethod(Integer.parseInt(XmlUtils.getText(element, "AlarmMethod")));
        String alarmTime = XmlUtils.getText(element, "AlarmTime");
        if (alarmTime == null) {
            return;
        }
        deviceAlarmVo.setAlarmTime(DateUtil.parse(alarmTime,"yyyy-M-d'T'H:m:s"));
        String alarmDescription = XmlUtils.getText(element, "AlarmDescription");
        if (alarmDescription == null) {
            deviceAlarmVo.setAlarmDescription("");
        } else {
            deviceAlarmVo.setAlarmDescription(alarmDescription);
        }
        String longitude = XmlUtils.getText(element, "Longitude");
        if (NumberUtil.isNumber(longitude)) {
            deviceAlarmVo.setLongitude(Double.parseDouble(longitude));
        } else {
            deviceAlarmVo.setLongitude(0.00);
        }
        String latitude = XmlUtils.getText(element, "Latitude");
        if (NumberUtil.isNumber(latitude)) {
            deviceAlarmVo.setLatitude(Double.parseDouble(latitude));
        } else {
            deviceAlarmVo.setLatitude(0.00);
        }
        if (!ObjectUtils.isEmpty(deviceAlarmVo.getAlarmMethod())) {
            if (deviceAlarmVo.getAlarmMethod().equals(DeviceAlarmMethod.Video.getVal())) {
                deviceAlarmVo.setAlarmType(Integer.parseInt(XmlUtils.getText(XmlUtil.getElement(element,"Info"), "AlarmType")));
            }
        }
        log.info("[收到报警通知]内容：{}", JSONUtil.toJsonPrettyStr(deviceAlarmVo));
        // 作者自用判断，其他小伙伴需要此消息可以自行修改，但是不要提在pr里
        log.debug("存储报警信息、报警分类");
        // 存储报警信息、报警分类
        SipConfigProperties sipConfigProperties = sipServer.getSipConfigProperties();
        if (sipConfigProperties.getAlarm()) {
            deviceAlarmVoService.insert(deviceAlarmVo);
        }
        //发送警报消息
        deviceAlarmVoService.sendAlarmMessage(sipServer,sipCommander,sipCommanderForPlatform,videoProperties,deviceAlarmVo);
    }
}
