package cn.com.tzy.springbootvideo.service.impl;

import cn.com.tzy.spingbootstartermybatis.core.mapper.utils.MyBatisUtils;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootentity.dome.video.Device;
import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootentity.dome.video.PlatformGbChannel;
import cn.com.tzy.springbootstartervideobasic.enums.StreamModeType;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideocore.demo.VideoRestResult;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.result.DeferredResultHolder;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.sip.SipServer;
import cn.com.tzy.springbootstartervideocore.sip.cmd.SIPCommander;
import cn.com.tzy.springbootvideo.convert.video.DeviceConvert;
import cn.com.tzy.springbootvideo.mapper.DeviceMapper;
import cn.com.tzy.springbootvideo.service.DeviceChannelService;
import cn.com.tzy.springbootvideo.service.DeviceService;
import cn.com.tzy.springbootvideo.service.PlatformGbChannelService;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService{

    @Resource
    private SipServer sipServer;
    @Resource
    private SIPCommander sipCommander;
    @Resource
    private DeferredResultHolder deferredResultHolder;
    @Resource
    private DeviceChannelService deviceChannelService;
    @Resource
    private PlatformGbChannelService platformGbChannelService;


    @Override
    public Device findPlatformIdChannelId(String platformId, String channelId) {
        return baseMapper.findPlatformIdChannelId(platformId,channelId);
    }

    @Override
    public Device findDeviceInfoPlatformIdChannelId(String platformId, String channelId) {
        return baseMapper.findDeviceInfoPlatformIdChannelId(platformId,channelId);
    }

    @Override
    public PageResult findPage(PageModel param, boolean administrator) {
        Page<Device> page = MyBatisUtils.buildPage(param);
        return MyBatisUtils.selectPage(baseMapper.findPage(page,param,administrator?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue()));
    }

    @Override
    public RestResult<?> findDeviceId(String deviceId) {
        Device device = baseMapper.selectOne(new LambdaQueryWrapper<Device>().eq(Device::getDeviceId, deviceId));
        if(device == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息");
        }
        int count = deviceChannelService.count(new LambdaQueryWrapper<DeviceChannel>().eq(DeviceChannel::getDeviceId, deviceId));
        device.setChannelCount(count);
        return RestResult.result(RespCode.CODE_0.getValue(),null,device);
    }

    @Override
    public RestResult<?> del(String deviceId) {
        Device device = baseMapper.selectOne(new LambdaQueryWrapper<Device>().eq(Device::getDeviceId, deviceId));
        if(device == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息");
        }
        List<DeviceChannel> list = deviceChannelService.list(new LambdaQueryWrapper<DeviceChannel>().eq(DeviceChannel::getDeviceId, deviceId));
        //1.删除上级通道
        if(!list.isEmpty()){
            platformGbChannelService.remove(new LambdaQueryWrapper<PlatformGbChannel>().in(PlatformGbChannel::getDeviceChannelId,list.stream().map(DeviceChannel::getChannelId).collect(Collectors.toSet())));
        }
        //2.上级设备通道
        VideoService.getDeviceChannelService().delAll(device.getDeviceId());
        //3.删除播放缓存
        RedisService.getInviteStreamManager().clearInviteInfo(deviceId);
        //4.删除定时设备
        RedisService.getDeviceNotifySubscribeManager().removeAlarmSubscribe(DeviceConvert.INSTANCE.convert(device));
        RedisService.getDeviceNotifySubscribeManager().removeCatalogSubscribe(DeviceConvert.INSTANCE.convert(device));
        RedisService.getDeviceNotifySubscribeManager().removeMobilePositionSubscribe(DeviceConvert.INSTANCE.convert(device));
        //5设备下线
        VideoService.getDeviceService().offline(deviceId,"删除设备");
        //6.删除设备
        baseMapper.deleteById(device.getId());
        return RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
    }

    @Override
    public RestResult<?> updateTransport(String deviceId, Integer streamMode) {
        String name = StreamModeType.getName(streamMode);
        if(name == null){
           return  RestResult.result(RespCode.CODE_2.getValue(),"未获取当前传输类型");
        }
        Device device = baseMapper.selectOne(new LambdaQueryWrapper<Device>().eq(Device::getDeviceId, deviceId));
        if(device == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息");
        }
        Device build = Device.builder()
                .id(device.getId())
                .streamMode(streamMode)
                .build();
        baseMapper.updateById(build);
        return RestResult.result(RespCode.CODE_0.getValue(),"编辑成功");
    }

    @Override
    public RestResult<?> saveDevice(Device param) {
        DeviceVo deviceVo = DeviceConvert.INSTANCE.convert(param);
        int save = VideoService.getDeviceService().save(deviceVo);
        if(save > 0){
            Device device = baseMapper.selectOne(new LambdaQueryWrapper<Device>().eq(Device::getDeviceId, deviceVo.getDeviceId()));
            if(device.getOnline()==ConstEnum.Flag.YES.getValue()){
                VideoService.getDeviceService().offline(deviceVo.getDeviceId(),"保存设备");
            }
            return RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
        }else {
            return RestResult.result(RespCode.CODE_2.getValue(),"保存失败");
        }
    }

    @Override
    public DeferredResult<RestResult<?>> findDeviceStatus(String deviceId) {
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s", DeferredResultHolder.CALLBACK_CMD_DEVICESTATUS,deviceId);
        VideoRestResult<RestResult<?>> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"设备不支持"));
        deferredResultHolder.put(key,uuid,result);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息"));
        }
        try {
            sipCommander.deviceStatusQuery(sipServer,deviceVo,null,error->{
                deferredResultHolder.invokeResult(key,uuid,RestResult.result(error.getStatusCode(),error.getMsg()));
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            log.error("[命令发送失败] 获取设备状态: ", e);
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"命令发送失败"));
        }
        return result;
    }

    @Override
    public DeferredResult<RestResult<?>> findDeviceAlarm(String deviceId, String startPriority, String endPriority, String alarmMethod, String alarmType, String startTime, String endTime) {
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s", DeferredResultHolder.CALLBACK_CMD_ALARM,deviceId);
        VideoRestResult<RestResult<?>> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"设备报警查询超时"));
        deferredResultHolder.put(key,uuid,result);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息"));
        }
        try {
            sipCommander.alarmInfoQuery(sipServer,deviceVo,startPriority,endPriority,alarmMethod,alarmType,startTime,endTime,null,error->{
                deferredResultHolder.invokeResult(key,uuid,RestResult.result(error.getStatusCode(),error.getMsg()));
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            log.error("[命令发送失败] 设备报警查询 : ", e);
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"命令发送失败"));
        }
        return result;
    }

    @Override
    public RestResult<?> subscribeInfo(String deviceId) {
        Device device = baseMapper.selectOne(new LambdaQueryWrapper<Device>().eq(Device::getDeviceId, deviceId));
        if(device == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息");
        }
        NotNullMap data = new NotNullMap() {{
            putInteger("alarm", RedisService.getDeviceNotifySubscribeManager().getAlarmSubscribe(device.getDeviceId())?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue());
            putInteger("catalog", RedisService.getDeviceNotifySubscribeManager().getCatalogSubscribe(device.getDeviceId())?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue());
            putInteger("mobilePosition", RedisService.getDeviceNotifySubscribeManager().getMobilePositionSubscribe(device.getDeviceId())?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue());
        }};
        return RestResult.result(RespCode.CODE_0.getValue(),null,data);
    }

    @Override
    public RestResult<?> startControl(String deviceId) {
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息");
        }
        try {
            sipCommander.teleBootCmd(sipServer,deviceVo,null,null);
        } catch (InvalidArgumentException | SipException | ParseException e) {
            log.error("[命令发送失败] 远程启动: ", e);
            return RestResult.result(RespCode.CODE_2.getValue(),"命令发送失败");
        }
        return RestResult.result(RespCode.CODE_2.getValue(),"远程启动成功");
    }

    @Override
    public DeferredResult<RestResult<?>> recordControl(String deviceId, String channelId, Integer status) {
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s_%s",DeferredResultHolder.CALLBACK_CMD_DEVICECONTROL,deviceId,channelId);
        VideoRestResult<RestResult<?>> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"设备不支持"));
        deferredResultHolder.put(key,uuid,result);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息"));
        }
        try {
            sipCommander.recordCmd(sipServer,deviceVo,channelId,status==ConstEnum.Flag.YES.getValue()?"Record":"StopRecord",null,error->{
                deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),error.getMsg()));
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            log.error("[命令发送失败] 录像控制: ", e);
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"命令发送失败"));
        }
        return result;
    }

    @Override
    public DeferredResult<RestResult<?>> guardControl(String deviceId,String channelId, Integer status) {
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s_%s",DeferredResultHolder.CALLBACK_CMD_DEVICECONTROL,deviceId,channelId);
        VideoRestResult<RestResult<?>> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"设备不支持"));

        deferredResultHolder.put(key,uuid,result);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息"));
        }
        try {
            sipCommander.guardCmd(sipServer,deviceVo,channelId,status==ConstEnum.Flag.YES.getValue()?"SetGuard":"ResetGuard",null,error->{
                deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),error.getMsg()));
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            log.error("[命令发送失败] 录像控制: ", e);
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"命令发送失败"));
        }
        return result;
    }

    @Override
    public DeferredResult<RestResult<?>> resetAlarm(String deviceId, String channelId, String alarmMethod, String alarmType) {
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s_%s",DeferredResultHolder.CALLBACK_CMD_DEVICECONTROL,deviceId,channelId);
        VideoRestResult<RestResult<?>> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"设备不支持"));
        deferredResultHolder.put(key,uuid,result);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息"));
        }
        try {
            sipCommander.alarmCmd(sipServer,deviceVo,alarmMethod,alarmType,null,error->{
                deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),error.getMsg()));
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            log.error("[命令发送失败] 报警复位: ", e);
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"命令发送失败"));
        }
        return result;
    }

    @Override
    public DeferredResult<RestResult<?>> iFrame(String deviceId, String channelId) {
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s_%s",DeferredResultHolder.CALLBACK_CMD_DEVICECONTROL,deviceId,channelId);
        VideoRestResult<RestResult<?>> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"设备不支持"));
        deferredResultHolder.put(key,uuid,result);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息"));
        }
        try {
            sipCommander.iFrameCmd(sipServer,deviceVo,channelId,null,error->{
                deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),error.getMsg()));
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            log.error("[命令发送失败] 强制关键帧: ", e);
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"命令发送失败"));
        }
        return result;
    }

    @Override
    public DeferredResult<RestResult<?>> homePosition(String deviceId, String channelId, Integer enabled, Integer resetTime, Integer presetIndex) {
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s_%s",DeferredResultHolder.CALLBACK_CMD_DEVICECONTROL,deviceId,channelId);
        VideoRestResult<RestResult<?>> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"设备不支持"));

        if (deferredResultHolder.exist(key, null)){
            return  result;
        }
        deferredResultHolder.put(key,uuid,result);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息"));
        }
        try {
            sipCommander.homePositionCmd(sipServer,deviceVo,channelId,enabled.toString(),resetTime.toString(),presetIndex.toString(),null,error->{
                deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),error.getMsg()));
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            log.error("[命令发送失败] 看守位控制: ", e);
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"命令发送失败"));
        }
        return result;
    }

    @Override
    public DeferredResult<RestResult<?>> zoomIn(String deviceId, String channelId, Integer length, Integer width, Integer midpointx, Integer midpointy, Integer lengthx, Integer lengthy) {
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s_%s",DeferredResultHolder.CALLBACK_CMD_DEVICECONTROL,deviceId,channelId);
        VideoRestResult<RestResult<?>> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"设备不支持"));

        if (deferredResultHolder.exist(key, null)){
            return  result;
        }
        deferredResultHolder.put(key,uuid,result);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息"));
        }
        StringBuffer cmdXml = new StringBuffer(200);
        cmdXml.append("<DragZoomIn>\r\n");
        cmdXml.append("<Length>" + length+ "</Length>\r\n");
        cmdXml.append("<Width>" + width+ "</Width>\r\n");
        cmdXml.append("<MidPointX>" + midpointx+ "</MidPointX>\r\n");
        cmdXml.append("<MidPointY>" + midpointy+ "</MidPointY>\r\n");
        cmdXml.append("<LengthX>" + lengthx+ "</LengthX>\r\n");
        cmdXml.append("<LengthY>" + lengthy+ "</LengthY>\r\n");
        cmdXml.append("</DragZoomIn>\r\n");
        try {
            sipCommander.dragZoomCmd(sipServer,deviceVo,channelId,cmdXml.toString(),null,error->{
                deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),error.getMsg()));
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            log.error("[命令发送失败] 拉框放大: ", e);
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"命令发送失败"));
        }
        return result;
    }

    @Override
    public DeferredResult<RestResult<?>> zoomOut(String deviceId, String channelId, Integer length, Integer width, Integer midpointx, Integer midpointy, Integer lengthx, Integer lengthy) {
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s_%s",DeferredResultHolder.CALLBACK_CMD_DEVICECONTROL,deviceId,channelId);
        VideoRestResult<RestResult<?>> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"设备不支持"));

        if (deferredResultHolder.exist(key, null)){
            return  result;
        }
        deferredResultHolder.put(key,uuid,result);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息"));
        }
        StringBuffer cmdXml = new StringBuffer(200);
        cmdXml.append("<DragZoomOut>\r\n");
        cmdXml.append("<Length>" + length+ "</Length>\r\n");
        cmdXml.append("<Width>" + width+ "</Width>\r\n");
        cmdXml.append("<MidPointX>" + midpointx+ "</MidPointX>\r\n");
        cmdXml.append("<MidPointY>" + midpointy+ "</MidPointY>\r\n");
        cmdXml.append("<LengthX>" + lengthx+ "</LengthX>\r\n");
        cmdXml.append("<LengthY>" + lengthy+ "</LengthY>\r\n");
        cmdXml.append("</DragZoomOut>\r\n");
        try {
            sipCommander.dragZoomCmd(sipServer,deviceVo,channelId,cmdXml.toString(),null,error->{
                deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),error.getMsg()));
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            log.error("[命令发送失败] 拉框缩小: ", e);
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"命令发送失败"));
        }
        return result;
    }

    @Override
    public DeferredResult<RestResult<?>> basicParam(String deviceId, String channelId, String name, String expiration, String heartBeatInterval, String heartBeatCount) {
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s_%s",DeferredResultHolder.CALLBACK_CMD_DEVICECONFIG,deviceId,channelId);
        VideoRestResult<RestResult<?>> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"设备不支持"));
        if (deferredResultHolder.exist(key, null)){
            return  result;
        }
        deferredResultHolder.put(key,uuid,result);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息"));
        }
        try {
            sipCommander.deviceBasicConfigCmd(sipServer,deviceVo,channelId,name,expiration,heartBeatInterval,heartBeatCount,null,error->{
                deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),error.getMsg()));
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            log.error("[命令发送失败] 基本配置设置命令: ", e);
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"命令发送失败"));
        }
        return result;
    }

    @Override
    public DeferredResult<RestResult<?>> queryParam(String deviceId, String channelId, String configType) {
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s_%s",DeferredResultHolder.CALLBACK_CMD_CONFIGDOWNLOAD,deviceId,channelId);
        VideoRestResult<RestResult<?>> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"设备不支持"));

        deferredResultHolder.put(key,uuid,result);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if(deviceVo == null){
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息"));
        }
        try {
            sipCommander.deviceConfigQuery(sipServer,deviceVo,channelId,configType,null,error->{
                deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),error.getMsg()));
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            log.error("[命令发送失败] 设备配置查询请求: ", e);
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"命令发送失败"));
        }
        return result;
    }
}
