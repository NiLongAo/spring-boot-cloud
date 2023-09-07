package cn.com.tzy.springbootvideo.service;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootentity.dome.video.DeviceAlarm;
import cn.com.tzy.springbootentity.param.video.DeviceAlarmPageParam;
import com.baomidou.mybatisplus.extension.service.IService;
public interface DeviceAlarmService extends IService<DeviceAlarm>{


    PageResult findPage(DeviceAlarmPageParam param);
}
