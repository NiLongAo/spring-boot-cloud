package cn.com.tzy.springbootapp.controller.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootentity.param.video.DeviceAlarmPageParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootapp.service.video.DeviceAlarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(tags = "设备报警信息相关接口",position = 4)
@RestController("AppVideoDeviceAlarmController")
@RequestMapping(value = "/app/video/device/alarm")
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
