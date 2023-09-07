package cn.com.tzy.springbootvideo.service;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.DeviceMobilePosition;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.context.request.async.DeferredResult;

public interface DeviceMobilePositionService extends IService<DeviceMobilePosition>{


    RestResult<?> findHistoryMobilePositions(String deviceId, String channelId, String start, String end);

    RestResult<?> findLatestMobilePositions(String deviceId);

    DeferredResult<RestResult<?>> findRealtime(String deviceId,String channelId);

    RestResult<?> subscribe(String deviceId, Integer expires, Integer interval);

    RestResult<?> transform(String deviceId);
}
