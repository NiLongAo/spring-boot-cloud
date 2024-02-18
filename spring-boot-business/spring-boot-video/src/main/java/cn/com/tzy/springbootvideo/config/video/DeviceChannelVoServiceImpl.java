package cn.com.tzy.springbootvideo.config.video;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootstartervideobasic.enums.VideoStreamType;
import cn.com.tzy.springbootstartervideobasic.exception.SsrcTransactionNotFoundException;
import cn.com.tzy.springbootstartervideobasic.exception.VideoException;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceChannelVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;
import cn.com.tzy.springbootstartervideocore.demo.InviteInfo;
import cn.com.tzy.springbootstartervideocore.demo.SsrcTransaction;
import cn.com.tzy.springbootstartervideocore.pool.task.DynamicTask;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.redis.impl.SsrcConfigManager;
import cn.com.tzy.springbootstartervideocore.redis.impl.SsrcTransactionManager;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceChannelVoService;
import cn.com.tzy.springbootstartervideocore.sip.SipServer;
import cn.com.tzy.springbootstartervideocore.sip.cmd.SIPCommander;
import cn.com.tzy.springbootvideo.convert.video.DeviceChannelConvert;
import cn.com.tzy.springbootvideo.service.DeviceChannelService;
import cn.com.tzy.springbootvideo.service.PlatformGbChannelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class DeviceChannelVoServiceImpl implements DeviceChannelVoService {

    @Resource
    private DynamicTask dynamicTask;
    @Resource
    private SIPCommander sipCommander;
    @Resource
    private SipServer sipServer;
    @Resource
    private DeviceChannelService deviceChannelService;
    @Resource
    private PlatformGbChannelService platformGbChannelService;

    @Override
    public DeviceChannelVo findLastDevice(String deviceId) {
        DeviceChannel one = deviceChannelService.getOne(new LambdaQueryWrapper<DeviceChannel>().eq(DeviceChannel::getDeviceId, deviceId).orderByDesc(DeviceChannel::getCreateTime).last("limit 1"));
        return DeviceChannelConvert.INSTANCE.convert(one);
    }

    @Override
    public DeviceChannelVo findChannelId(String channelId) {
        List<DeviceChannel> deviceChannels = deviceChannelService.list(new LambdaQueryWrapper<DeviceChannel>().eq(DeviceChannel::getChannelId, channelId));
            try {
                if(deviceChannels.isEmpty()){
                    return null;
                }
                if(deviceChannels.size() > 1){
                    throw new VideoException("设备通道国标重复，请检查数据");
                }
            } catch (VideoException e) {
                throw new RuntimeException(e);
            }
        return DeviceChannelConvert.INSTANCE.convert(deviceChannels.get(0));
    }

    @Override
    public DeviceChannelVo findPlatformIdChannelId(String platformId, String channelId) {
        DeviceChannel  deviceChannel =  deviceChannelService.findPlatformIdChannelId(platformId,channelId);
        return DeviceChannelConvert.INSTANCE.convert(deviceChannel);
    }

    @Override
    public DeviceChannelVo findDeviceIdChannelId(String deviceId, String channelId) {
        DeviceChannel deviceChannel = deviceChannelService.getOne(new LambdaQueryWrapper<DeviceChannel>().eq(DeviceChannel::getDeviceId, deviceId).eq(DeviceChannel::getChannelId, channelId));
        return DeviceChannelConvert.INSTANCE.convert(deviceChannel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deviceChannelOnline(String deviceId, String channelId, boolean online) {
        deviceChannelService.update(new LambdaUpdateWrapper<DeviceChannel>().set(DeviceChannel::getStatus,online?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue()).eq(DeviceChannel::getDeviceId,deviceId).eq(StringUtils.isNotEmpty(channelId),DeviceChannel::getChannelId,channelId));
    }

    @Override
    public List<DeviceChannelVo> queryAllChannels(String deviceId) {
        List<DeviceChannel> deviceChannels = deviceChannelService.list(new LambdaQueryWrapper<DeviceChannel>().eq(DeviceChannel::getDeviceId, deviceId));
        return DeviceChannelConvert.INSTANCE.convertVoList(deviceChannels);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetChannels(String deviceId, List<DeviceChannelVo> channelList) {
        Map<String, DeviceChannel> allDeviceChannelMap = new HashMap<>();
        Map<String, DeviceChannel> channelDeviceMap = new HashMap<>();
        List<DeviceChannel> allDeviceChannelList = deviceChannelService.list(new LambdaQueryWrapper<DeviceChannel>().eq(DeviceChannel::getDeviceId, deviceId));
        if(!allDeviceChannelList.isEmpty()){
            allDeviceChannelMap = allDeviceChannelList.stream().collect(Collectors.toMap(DeviceChannel::getChannelId, o -> o));
        }
        Map<String, List<DeviceChannelVo>> collect = channelList.stream().filter(o->StringUtils.isNotEmpty(o.getDeviceId())).collect(Collectors.groupingBy(DeviceChannelVo::getDeviceId));
        channelList = channelList.stream().peek(o->{o.setSubCount(collect.get(o.getChannelId())== null?0:collect.get(o.getChannelId()).size());}).collect(Collectors.toList());
        List<DeviceChannel> channelDeviceList = DeviceChannelConvert.INSTANCE.convertList(channelList);
        if(!channelDeviceList.isEmpty()){
            channelDeviceMap = channelDeviceList.stream().collect(Collectors.toMap(DeviceChannel::getChannelId, o -> o,(a,b)->b));
        }
        if(channelDeviceMap.isEmpty()){
            log.error("[ 更新设备全部通道信息 ] : 未获取到更新设备");
            return false;
        }
        //要删除的值
        Map<String, DeviceChannel> finalChannelDeviceMap = channelDeviceMap;
        List<String> deleteList =allDeviceChannelMap.keySet().stream().filter(num -> !finalChannelDeviceMap.containsKey(num)).collect(Collectors.toList());
        //要添加的值
        Map<String, DeviceChannel> finalAllDeviceChannelMap = allDeviceChannelMap;
        List<String> addList = channelDeviceMap.keySet().stream().filter(num -> !finalAllDeviceChannelMap.containsKey(num)).collect(Collectors.toList());
        //刪除
        if(!deleteList.isEmpty()){
            deviceChannelService.remove(new LambdaQueryWrapper<DeviceChannel>().eq(DeviceChannel::getDeviceId,deviceId).in(DeviceChannel::getChannelId,deleteList));
        }
        //添加
        List<DeviceChannel> addDeviceChannel = channelDeviceList.stream().filter(o -> addList.contains(o.getChannelId())).collect(Collectors.toList());
        if(!addDeviceChannel.isEmpty()){
            deviceChannelService.saveBatch(addDeviceChannel,300);
            for (String key : addList) {
                channelDeviceMap.remove(key);
            }
        }
        //修改
        if(!channelDeviceMap.isEmpty()){
            Map<String, DeviceChannel> finalAllDeviceChannelMap1 = allDeviceChannelMap;
            List<DeviceChannel> updateList = channelDeviceMap.values().stream().peek(o -> {
                DeviceChannel deviceChannel = finalAllDeviceChannelMap1.get(o.getChannelId());
                o.setId(deviceChannel.getId());
            }).collect(Collectors.toList());
            deviceChannelService.updateBatchById(updateList,300);
        }
        return true;
    }

    @Override
    public List<DeviceChannelVo> queryChannelWithCatalog(String serverGbId) {
        List<DeviceChannel> deviceChannelList = deviceChannelService.queryChannelWithCatalog(serverGbId);
        return DeviceChannelConvert.INSTANCE.convertVoList(deviceChannelList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(DeviceChannelVo deviceChannelVo) {
        boolean save ;
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceChannelVo.getDeviceId());
        if(deviceVo == null){
            log.error("保存设备通道时发现 设备不存在！");
            return ConstEnum.Flag.NO.getValue();
        }
        InviteInfo inviteInfo = RedisService.getInviteStreamManager().getInviteInfoByDeviceAndChannel(VideoStreamType.play, deviceChannelVo.getDeviceId(), deviceChannelVo.getChannelId());
        if(inviteInfo != null){
            deviceChannelVo.setStreamId(inviteInfo.getStream());
        }
        deviceChannelVo.initGps(deviceVo.getGeoCoordSys());
        DeviceChannel convert = DeviceChannelConvert.INSTANCE.convert(deviceChannelVo);
        DeviceChannel deviceChannel = deviceChannelService.getOne(new LambdaQueryWrapper<DeviceChannel>().eq(DeviceChannel::getDeviceId, convert.getDeviceId()).eq(DeviceChannel::getChannelId, convert.getChannelId()));
        if(deviceChannel == null){
            save = deviceChannelService.save(convert);
        }else {
            convert.setId(deviceChannel.getId());
            save = deviceChannelService.updateById(convert);
        }
        deviceChannelService.updateChannelSubCount(deviceChannelVo.getDeviceId(),deviceChannelVo.getChannelId());
        return save?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue();
    }

    @Override
    public void updateMobilePosition(DeviceChannelVo deviceChannelVo) {
        if (deviceChannelVo.getChannelId().equals(deviceChannelVo.getDeviceId())) {
            deviceChannelVo.setChannelId(null);
        }
        if (deviceChannelVo.getGpsTime() == null) {
            deviceChannelVo.setGpsTime(new Date());
        }
        DeviceChannel convert = DeviceChannelConvert.INSTANCE.convert(deviceChannelVo);
        deviceChannelService.update(convert,new LambdaQueryWrapper<DeviceChannel>().eq(DeviceChannel::getDeviceId,convert.getDeviceId()).eq(StringUtils.isNotEmpty(convert.getChannelId()),DeviceChannel::getChannelId,convert.getChannelId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delAll(String deviceId) {
        if(StringUtils.isEmpty(deviceId)){
            return ConstEnum.Flag.NO.getValue();
        }
        platformGbChannelService.delPlatformGbChannel(deviceId,null);
        boolean delete = deviceChannelService.remove(new LambdaQueryWrapper<DeviceChannel>().eq(DeviceChannel::getDeviceId, deviceId));
        return delete?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int del(String deviceId, String channelId) {
        if(StringUtils.isEmpty(deviceId) || StringUtils.isEmpty(channelId) ){
            return ConstEnum.Flag.NO.getValue();
        }
        platformGbChannelService.delPlatformGbChannel(deviceId,channelId);
        boolean delete = deviceChannelService.remove(new LambdaQueryWrapper<DeviceChannel>().eq(DeviceChannel::getDeviceId, deviceId).eq(DeviceChannel::getChannelId,channelId));
        return delete?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue();
    }

    @Override
    public void startPlay(String deviceId, String channelId, String stream) {
        deviceChannelService.update(new LambdaUpdateWrapper<DeviceChannel>().set(DeviceChannel::getStreamId,stream).eq(DeviceChannel::getDeviceId, deviceId).eq(DeviceChannel::getChannelId,channelId));
    }

    @Override
    public void stopPlay(String deviceId, String channelId) {
        deviceChannelService.update(new LambdaUpdateWrapper<DeviceChannel>().set(DeviceChannel::getStreamId,"").eq(DeviceChannel::getDeviceId, deviceId).eq(DeviceChannel::getChannelId,channelId));
    }

    @Override
    public RestResult<?> findAudioPushPath(String deviceId,String channelId) {
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        SsrcTransaction paramOne = ssrcTransactionManager.getParamOne(deviceId, null, null, null,VideoStreamType.audio);
        if(paramOne != null){
            if(channelId.equals(paramOne.getChannelId())){
                return RestResult.result(RespCode.CODE_2.getValue(),"语音通话中,请稍后或关闭语音流");
            }else {
                return RestResult.result(RespCode.CODE_2.getValue(),"每个设备只能一个通道语音对讲");
            }
        }
        DeviceChannelVo deviceChannelVo = VideoService.getDeviceChannelService().findDeviceIdChannelId(deviceId, channelId);
        if(deviceChannelVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取设备通道信息");
        }
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息");
        }
        MediaServerVo mediaServerVo = VideoService.getMediaServerService().findMediaServerForMinimumLoad(deviceVo);
        if(mediaServerVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取流媒体信息");
        }
        String ssrc = ssrcConfigManager.getPlaySsrc(mediaServerVo.getId());
        String streamId = String.format("%08x", Integer.parseInt(ssrc)).toUpperCase();
        ssrcTransactionManager.put(deviceId, channelId,null,"audio",streamId,ssrc,mediaServerVo.getId(),null,VideoStreamType.audio);
        dynamicTask.startCron(String.format("audio_push_stream:%s_%s",deviceId,channelId),15,()->{
            SsrcTransaction param = ssrcTransactionManager.getParamOne(deviceId, channelId, null, null, VideoStreamType.audio);
            if(StringUtils.isEmpty(param.getCallId())){//如何没有callId表示没有接收到Invite请求 则直接关闭
                stopAudioPushStatus(deviceId,channelId);
            }
        });
        String audioPushPath = String.format("%s://%s:%s/%s/index/api/webrtc?app=%s&stream=%s&type=push",mediaServerVo.getSslStatus()== ConstEnum.Flag.NO.getValue()?"http":"https",mediaServerVo.getStreamIp(), mediaServerVo.getSslStatus()== ConstEnum.Flag.NO.getValue()?mediaServerVo.getHttpPort():mediaServerVo.getHttpSslPort(), StringUtils.isNotEmpty(mediaServerVo.getVideoHttpPrefix())?mediaServerVo.getVideoHttpPrefix():"","audio",streamId);
        return RestResult.result(RespCode.CODE_0.getValue(),null,audioPushPath);
    }

    @Override
    public RestResult<?> findAudioPushStatus(String deviceId, String channelId) {
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        SsrcTransaction paramOne = ssrcTransactionManager.getParamOne(deviceId, channelId, null, null,VideoStreamType.audio);
        if(paramOne != null){
            return RestResult.result(RespCode.CODE_0.getValue(),null,true);
        }else {
            return RestResult.result(RespCode.CODE_0.getValue(),null,false);
        }
    }

    @Override
    public RestResult<?> stopAudioPushStatus(String deviceId, String channelId) {
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息");
        }
        dynamicTask.stop(String.format("audio_push_stream:%s_%s",deviceId,channelId));
        try {
            sipCommander.streamByeCmd(sipServer, deviceVo,channelId,null,null,VideoStreamType.audio,null,null);
        } catch (SipException | InvalidArgumentException | ParseException | SsrcTransactionNotFoundException e) {
            log.error("[命令发送失败] 语音流 发送BYE: {}", e.getMessage());
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null);
    }

}
