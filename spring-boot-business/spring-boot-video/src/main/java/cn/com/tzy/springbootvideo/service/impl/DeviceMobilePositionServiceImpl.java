package cn.com.tzy.springbootvideo.service.impl;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.DeviceMobilePosition;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceChannelVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideocore.demo.DeviceNotifyVo;
import cn.com.tzy.springbootstartervideocore.demo.VideoRestResult;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.result.DeferredResultHolder;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.sip.SipServer;
import cn.com.tzy.springbootstartervideocore.sip.cmd.SIPCommander;
import cn.com.tzy.springbootvideo.mapper.DeviceMobilePositionMapper;
import cn.com.tzy.springbootvideo.service.DeviceMobilePositionService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class DeviceMobilePositionServiceImpl extends ServiceImpl<DeviceMobilePositionMapper, DeviceMobilePosition> implements DeviceMobilePositionService{

    @Resource
    private SIPCommander sipCommander;
    @Resource
    private SipServer sipServer;
    @Resource
    private DeferredResultHolder deferredResultHolder;

    @Override
    public RestResult<?> findHistoryMobilePositions(String deviceId, String channelId, String start, String end) {
        List<DeviceMobilePosition> deviceMobilePositionList = baseMapper.selectList(new LambdaQueryWrapper<DeviceMobilePosition>()
                .eq(DeviceMobilePosition::getDeviceId, deviceId)
                .eq(StringUtils.isNotEmpty(channelId), DeviceMobilePosition::getChannelId, channelId)
                .between(StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end), DeviceMobilePosition::getTime, DateUtil.parse(start), DateUtil.parse(end))
        );

        return RestResult.result(RespCode.CODE_0.getValue(),null,deviceMobilePositionList);
    }

    @Override
    public RestResult<?> findLatestMobilePositions(String deviceId) {
        DeviceMobilePosition deviceMobilePosition = baseMapper.selectOne(new LambdaQueryWrapper<DeviceMobilePosition>()
                        .eq(DeviceMobilePosition::getDeviceId, deviceId).orderByDesc(DeviceMobilePosition::getTime).last("limit 1"));
        return  RestResult.result(RespCode.CODE_0.getValue(),null,deviceMobilePosition);
    }

    @Override
    public DeferredResult<RestResult<?>> findRealtime(String deviceId,String channelId) {
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s",DeferredResultHolder.CALLBACK_CMD_MOBILEPOSITION,deviceId);
        VideoRestResult<RestResult<?>> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"请求超时"));
        deferredResultHolder.put(key,uuid,result);
        try {
            sipCommander.mobilePostitionQuery(sipServer,deviceVo,channelId,null, error -> {
                deferredResultHolder.invokeResult(key,uuid,String.format("获取移动位置信息失败，错误码： %s, %s", error.getStatusCode(), error.getMsg()));
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            log.error("[命令发送失败] 获取移动位置信息: {}", e.getMessage());
            deferredResultHolder.invokeResult(key,uuid,"命令发送失败");
        }
        return result;
    }

    @Override
    public RestResult<?> subscribe(String deviceId, Integer type, Integer expires) {
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(type== DeviceNotifyVo.TypeEnum.CATALOG.getValue()){
            if(expires > 0){
                deviceVo.setSubscribeCycleForCatalog(expires);
                VideoService.getDeviceService().save(deviceVo);
                RedisService.getDeviceNotifySubscribeManager().addCatalogSubscribe(deviceVo,"web页面订阅");
            }else {
                RedisService.getDeviceNotifySubscribeManager().removeCatalogSubscribe(deviceVo);
            }
        }else if(type== DeviceNotifyVo.TypeEnum.MOBILE_POSITION.getValue()){
            if(expires > 0){
                deviceVo.setSubscribeCycleForMobilePosition(expires);
                VideoService.getDeviceService().save(deviceVo);
                RedisService.getDeviceNotifySubscribeManager().addMobilePositionSubscribe(deviceVo,"web页面订阅");
            }else {
                RedisService.getDeviceNotifySubscribeManager().removeMobilePositionSubscribe(deviceVo);
            }
        }else if(type== DeviceNotifyVo.TypeEnum.ALARM.getValue()){
            if(expires > 0){
                deviceVo.setSubscribeCycleForAlarm(expires);
                VideoService.getDeviceService().save(deviceVo);
                RedisService.getDeviceNotifySubscribeManager().addAlarmSubscribe(deviceVo,"web页面订阅");
            }else {
                RedisService.getDeviceNotifySubscribeManager().removeAlarmSubscribe(deviceVo);
            }
        }else {
            return RestResult.result(RespCode.CODE_2.getValue(),"订阅类型错误");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"订阅成功");
    }

    @Override
    public RestResult<?> transform(String deviceId) {
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息");
        }
        List<DeviceChannelVo> deviceChannelVos = VideoService.getDeviceChannelService().queryAllChannels(deviceVo.getDeviceId());
        if(deviceChannelVos.isEmpty()){
            return RestResult.result(RespCode.CODE_0.getValue(),"处理成功");
        }
        List<DeviceChannelVo> collect = deviceChannelVos.stream().map(o -> o.initGps(deviceVo.getGeoCoordSys())).collect(Collectors.toList());
        for (DeviceChannelVo deviceChannelVo : collect) {
            VideoService.getDeviceChannelService().save(deviceChannelVo);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"处理成功");
    }
}
