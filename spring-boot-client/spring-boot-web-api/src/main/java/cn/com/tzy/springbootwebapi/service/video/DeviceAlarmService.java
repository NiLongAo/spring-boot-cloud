package cn.com.tzy.springbootwebapi.service.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootentity.param.video.DeviceAlarmPageParam;
import cn.com.tzy.springbootfeignvideo.api.video.DeviceAlarmServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 设备报警信息相关接口
 */
@Service
public class DeviceAlarmService {
    @Resource
    private DeviceAlarmServiceFeign deviceAlarmServiceFeign;

    /**
     * 分页
     */
    public PageResult page(DeviceAlarmPageParam param){
        return deviceAlarmServiceFeign.page(param);
    }

}
