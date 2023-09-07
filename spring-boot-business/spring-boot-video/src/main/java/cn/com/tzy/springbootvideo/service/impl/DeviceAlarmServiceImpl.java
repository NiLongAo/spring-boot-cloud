package cn.com.tzy.springbootvideo.service.impl;

import cn.com.tzy.spingbootstartermybatis.core.mapper.utils.MyBatisUtils;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootentity.dome.video.ParentPlatform;
import cn.com.tzy.springbootentity.param.video.DeviceAlarmPageParam;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.com.tzy.springbootvideo.mapper.DeviceAlarmMapper;
import cn.com.tzy.springbootentity.dome.video.DeviceAlarm;
import cn.com.tzy.springbootvideo.service.DeviceAlarmService;
@Service
public class DeviceAlarmServiceImpl extends ServiceImpl<DeviceAlarmMapper, DeviceAlarm> implements DeviceAlarmService{

    @Override
    public PageResult findPage(DeviceAlarmPageParam param) {
        Page<DeviceAlarm> page = MyBatisUtils.buildPage(param);
        LambdaQueryWrapper<DeviceAlarm> wrapper = new LambdaQueryWrapper<DeviceAlarm>()
                .eq(StringUtils.isNotEmpty(param.deviceId),DeviceAlarm::getDeviceId,param.deviceId)
                .eq(param.alarmPriority != null,DeviceAlarm::getDeviceId,param.alarmPriority)
                .eq(param.alarmMethod != null,DeviceAlarm::getDeviceId,param.alarmMethod)
                .eq(param.alarmType != null,DeviceAlarm::getDeviceId,param.alarmType)
                .between(param.startTime!= null && param.endTime != null,DeviceAlarm::getDeviceId,param.startTime,param.endTime)
                ;
        return MyBatisUtils.selectPage(baseMapper, page, wrapper);

    }
}
