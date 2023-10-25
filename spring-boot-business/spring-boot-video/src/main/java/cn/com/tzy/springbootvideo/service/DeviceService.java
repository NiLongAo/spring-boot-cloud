package cn.com.tzy.springbootvideo.service;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.context.request.async.DeferredResult;

public interface DeviceService extends IService<Device>{
    Device findPlatformIdChannelId(String platformId, String channelId);
    Device findDeviceInfoPlatformIdChannelId(String platformId, String channelId);
    PageResult findPage(PageModel param, boolean administrator);
    RestResult<?> findDeviceId(String deviceId);
    RestResult<?> del(String deviceId);
    RestResult<?> updateTransport(String deviceId, Integer streamMode);
    RestResult<?> saveDevice(Device param);
    DeferredResult<RestResult<?>> findDeviceStatus(String deviceId);
    DeferredResult<RestResult<?>> findDeviceAlarm(String deviceId, String startPriority, String endPriority, String alarmMethod, String alarmType, String startTime, String endTime);

    RestResult<?> subscribeInfo(String deviceId);

    RestResult<?> startControl(String deviceId);

    DeferredResult<RestResult<?>> recordControl(String deviceId, String channelId, Integer status);

    DeferredResult<RestResult<?>> guardControl(String deviceId,String channelId, Integer status);

    DeferredResult<RestResult<?>> resetAlarm(String deviceId, String channelId, String alarmMethod, String alarmType);

    DeferredResult<RestResult<?>> iFrame(String deviceId, String channelId);

    DeferredResult<RestResult<?>> homePosition(String deviceId, String channelId, Integer enabled, Integer resetTime, Integer presetIndex);

    DeferredResult<RestResult<?>> zoomIn(String deviceId, String channelId, Integer length, Integer width, Integer midpointx, Integer midpointy, Integer lengthx, Integer lengthy);

    DeferredResult<RestResult<?>> zoomOut(String deviceId, String channelId, Integer length, Integer width, Integer midpointx, Integer midpointy, Integer lengthx, Integer lengthy);

    DeferredResult<RestResult<?>> basicParam(String deviceId, String channelId, String name, String expiration, String heartBeatInterval, String heartBeatCount);

    DeferredResult<RestResult<?>> queryParam(String deviceId, String channelId, String configType);
}
