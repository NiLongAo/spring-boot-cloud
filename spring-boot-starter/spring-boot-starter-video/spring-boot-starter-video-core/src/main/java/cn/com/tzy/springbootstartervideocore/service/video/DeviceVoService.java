package cn.com.tzy.springbootstartervideocore.service.video;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.vo.sip.Address;
import cn.com.tzy.springbootstartervideobasic.vo.sip.SyncStatus;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;
import cn.com.tzy.springbootstartervideocore.demo.SipTransactionInfo;
import cn.com.tzy.springbootstartervideocore.demo.SsrcTransaction;
import cn.com.tzy.springbootstartervideocore.media.client.MediaClient;
import cn.com.tzy.springbootstartervideocore.properties.VideoProperties;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.redis.impl.InviteStreamManager;
import cn.com.tzy.springbootstartervideocore.redis.impl.SipTransactionManager;
import cn.com.tzy.springbootstartervideocore.redis.impl.SsrcConfigManager;
import cn.com.tzy.springbootstartervideocore.redis.impl.SsrcTransactionManager;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.sip.SipServer;
import cn.com.tzy.springbootstartervideocore.sip.cmd.SIPCommander;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Log4j2
public abstract class DeviceVoService {
    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    public abstract List<DeviceVo> getAllOnlineDevice();
    public abstract DeviceVo findDeviceGbId(String deviceGbId);
    public abstract DeviceVo findPlatformIdChannelId(String platformId, String channelId);
    public abstract DeviceVo findDeviceInfoPlatformIdChannelId(String platformId, String channelId);

    public abstract List<DeviceVo> queryDeviceWithAsMessageChannel();

    public abstract int save(DeviceVo deviceVo);
    public abstract void updateStatus(Long id,boolean status);
    public abstract void updateZlm(String deviceGbId,String mediaServerId);

    /**
     * 设备上线
     * @param deviceVo
     */
    public void online(DeviceVo deviceVo, SipServer sipServer, SIPCommander sipCommander, VideoProperties videoProperties, SipTransactionInfo sipTransactionInfo) {
        DeviceChannelVoService deviceChannelVoService = VideoService.getDeviceChannelService();
        SipTransactionManager sipTransactionManager = RedisService.getSipTransactionManager();
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        DynamicTask dynamicTask = SpringUtil.getBean(DynamicTask.class);
        log.info("[设备上线] deviceId：{}->{}:{}", deviceVo.getDeviceId(), deviceVo.getIp(), deviceVo.getPort());
        if ( null  == deviceVo.getKeepaliveIntervalTime() || 0 == deviceVo.getKeepaliveIntervalTime()) {
            // 默认心跳间隔60
            deviceVo.setKeepaliveIntervalTime(60);
        }
        if (sipTransactionInfo != null) {
            sipTransactionManager.putDevice(deviceVo.getDeviceId(),sipTransactionInfo);
        }
        deviceVo.setKeepaliveTime(new Date());
        DeviceVo deviceVoGb = this.findDeviceGbId(deviceVo.getDeviceId());
        //缓存设备注册服务
        RedisService.getRegisterServerManager().putDevice(deviceVo.getDeviceId(),deviceVo.getKeepaliveIntervalTime()+ VideoConstant.DELAY_TIME , Address.builder().gbId(deviceVo.getDeviceId()).ip(nacosDiscoveryProperties.getIp()).port(nacosDiscoveryProperties.getPort()).build());
        if(deviceVoGb == null){
            deviceVo.setOnline(ConstEnum.Flag.YES.getValue());
            deviceVo.setRegisterTime(new Date());
            log.info("[设备上线,首次注册]: {}，查询设备信息以及通道信息", deviceVo.getDeviceId());
            this.save(deviceVo);
            try {
                sipCommander.deviceInfoQuery(sipServer, deviceVo,null,null);
            } catch (InvalidArgumentException | SipException | ParseException e) {
                log.error("[命令发送失败] 查询设备信息: {}", e.getMessage());
            }
            sync(sipServer,sipCommander, deviceVo);
        }else {
            inviteStreamManager.clearInviteInfo(deviceVo.getDeviceId());
            if(deviceVo.getOnline() == ConstEnum.Flag.NO.getValue()){
                log.info("[设备上线,首次注册]: {}，查询设备信息以及通道信息", deviceVo.getDeviceId());
                deviceVo.setOnline(ConstEnum.Flag.YES.getValue());
                deviceVo.setRegisterTime(new Date());
                this.save(deviceVo);
                if(videoProperties.getSyncChannelOnDeviceOnline()){
                    log.info("[设备上线,离线状态下重新注册]: {}，查询设备信息以及通道信息", deviceVo.getDeviceId());
                    try {
                        sipCommander.deviceInfoQuery(sipServer, deviceVo,null,null);
                    } catch (InvalidArgumentException | SipException | ParseException e) {
                        log.error("[命令发送失败] 查询设备信息: {}", e.getMessage());
                    }
                    sync(sipServer,sipCommander, deviceVo);
                }
            }else {
                if (deviceChannelVoService.queryAllChannels(deviceVo.getDeviceId()).size() == 0) {
                    log.info("[设备上线]: {}，通道数为0,查询通道信息", deviceVo.getDeviceId());
                    sync(sipServer,sipCommander, deviceVo);
                }
                this.save(deviceVo);
            }
        }
        // 上线添加订阅
        if (deviceVo.getSubscribeCycleForCatalog() > 0) {
            // 查询在线设备那些开启了订阅，为设备开启定时的目录订阅
            RedisService.getDeviceNotifySubscribeManager().addCatalogSubscribe(deviceVo);
        }
        if (deviceVo.getSubscribeCycleForMobilePosition() > 0) {
            RedisService.getDeviceNotifySubscribeManager().addMobilePositionSubscribe(deviceVo);
        }
        if (deviceVo.getSubscribeCycleForAlarm() > 0) {
            RedisService.getDeviceNotifySubscribeManager().addAlarmSubscribe(deviceVo);
        }
        String key = String.format("%s_%s", VideoConstant.REGISTER_EXPIRE_TASK_KEY_PREFIX, deviceVo.getDeviceId());
        //设置设备过期任务
        dynamicTask.startDelay(key, deviceVo.getKeepaliveIntervalTime()+ VideoConstant.DELAY_TIME * 2 ,()->offline(deviceVo.getDeviceId()));
    }

    /**
     * 设备下线
     * @param deviceId
     */
    public void offline(String deviceId) {
        DynamicTask dynamicTask = SpringUtil.getBean(DynamicTask.class);
        MediaServerVoService mediaServerVoService = VideoService.getMediaServerService();
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        log.info("[设备下线]， device：{}", deviceId);
        DeviceVo deviceVo = this.findDeviceGbId(deviceId);
        if (deviceVo == null) {
            log.warn("[设备下线]：未获取设备信息 deviceId ：{}",deviceId);
            return;
        }
        String key = String.format("%s_%s", VideoConstant.REGISTER_EXPIRE_TASK_KEY_PREFIX, deviceVo.getDeviceId());
        dynamicTask.stop(key);
        this.updateStatus(deviceVo.getId(),false);
        VideoService.getDeviceChannelService().deviceChannelOnline(deviceVo.getDeviceId(),null,false);
        // 离线释放所有ssrc
        List<SsrcTransaction> ssrcTransactions = ssrcTransactionManager.getParamAll(deviceId, null, null, null,null);
        if (ssrcTransactions != null && ssrcTransactions.size() > 0) {
            for (SsrcTransaction ssrcTransaction : ssrcTransactions) {
                MediaServerVo mediaServerVo = mediaServerVoService.findOnLineMediaServerId(ssrcTransaction.getMediaServerId());
                if(mediaServerVo != null){
                    ssrcConfigManager.releaseSsrc(ssrcTransaction.getMediaServerId(), ssrcTransaction.getSsrc());
                    MediaClient.closeRtpServer(mediaServerVo, ssrcTransaction.getStream());
                    ssrcTransactionManager.remove(deviceId, ssrcTransaction.getChannelId(), ssrcTransaction.getStream());
                }
            }
        }
        // 移除订阅
        RedisService.getDeviceNotifySubscribeManager().removeCatalogSubscribe(deviceVo);
        RedisService.getDeviceNotifySubscribeManager().removeMobilePositionSubscribe(deviceVo);
        RedisService.getDeviceNotifySubscribeManager().removeAlarmSubscribe(deviceVo);
        RedisService.getRegisterServerManager().delDevice(deviceId);
    }

    /**
     * 异步同步通道
     * @param deviceVo
     */
    public boolean sync(SipServer sipServer, SIPCommander sipCommander, DeviceVo deviceVo){
        if (RedisService.getCatalogDataManager().isSyncRunning(deviceVo.getDeviceId())) {
            log.info("开启同步时发现同步已经存在");
            return true;
        }
        int sn = (int)((Math.random()*9+1)*100000);
        RedisService.getCatalogDataManager().addReady(deviceVo, sn);
        try {
            sipCommander.catalogQuery(sipServer, deviceVo, sn,null, error -> {
                String errorMsg = String.format("同步通道失败，错误码： %s, %s", error.getStatusCode(), error.getMsg());
                RedisService.getCatalogDataManager().setChannelSyncEnd(deviceVo.getDeviceId(), errorMsg);
            });
            return true;
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[同步通道], 信令发送失败：{}", e.getMessage() );
            String errorMsg = String.format("同步通道失败，信令发送失败： %s", e.getMessage());
            RedisService.getCatalogDataManager().setChannelSyncEnd(deviceVo.getDeviceId(), errorMsg);
            return false;
        }
    }

    /**
     * 获取设备同步状态
     * @param deviceId
     */
    public SyncStatus getChannelSyncStatus(String deviceId){
        return RedisService.getCatalogDataManager().getSyncStatus(deviceId);
    }

}
