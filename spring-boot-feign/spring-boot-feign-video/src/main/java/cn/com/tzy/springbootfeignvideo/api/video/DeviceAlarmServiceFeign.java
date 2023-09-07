package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootentity.param.video.DeviceAlarmPageParam;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 设备报警信息相关接口
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/device/alarm",configuration = FeignConfiguration.class)
public interface DeviceAlarmServiceFeign {

    /**
     * 分页
     */
    @RequestMapping(value = "/page",method = RequestMethod.POST)
    PageResult page(@Validated @RequestBody DeviceAlarmPageParam param);

}
