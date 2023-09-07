package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootentity.param.video.DeviceAlarmPageParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootvideo.service.DeviceAlarmService;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 设备报警信息相关接口
 */
@Log4j2
@RestController("ApiDeviceAlarmController")
@RequestMapping(value = "/api/device/alarm")
public class DeviceAlarmController extends ApiController {

    @Resource
    private DeviceAlarmService deviceAlarmService;

    /**
     * 分页
     */
    @PostMapping("page")
    public PageResult page(@Validated @RequestBody DeviceAlarmPageParam param){
        return deviceAlarmService.findPage(param);
    }



}
