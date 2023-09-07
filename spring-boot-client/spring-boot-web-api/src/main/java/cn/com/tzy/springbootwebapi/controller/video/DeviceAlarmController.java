package cn.com.tzy.springbootwebapi.controller.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootentity.param.video.DeviceAlarmPageParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.video.DeviceAlarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(tags = "设备报警信息相关接口",position = 4)
@RestController("WebApiVideoDeviceAlarmController")
@RequestMapping(value = "/webapi/video/device/alarm")
public class DeviceAlarmController extends ApiController {

    @Resource
    private DeviceAlarmService deviceAlarmService;

    /**
     * 分页
     */
    @PostMapping("page")
    @ApiOperation(value = "分页", notes = "分页")
    public PageResult page(@Validated @RequestBody DeviceAlarmPageParam param){
        return deviceAlarmService.page(param);
    }



}
