package cn.com.tzy.springbootentity.param.video;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@ApiModel("设备报价请求类")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAlarmPageParam extends PageModel {


    @ApiModelProperty("设备国标编号")
    public String deviceId;
    @ApiModelProperty("报警级别, 1为一级警情, 2为二级警情, 3为三级警情, 4为四级警情")
    public Integer alarmPriority;
    @ApiModelProperty("报警方式 , 1为电话报警, 2为设备报警, 3为短信报警, 4为 GPS报警, 5为视频报警, 6为设备故障报警,7其他报警;可以为直接组合如12为电话报警或 设备报警-")
    public Integer alarmMethod;
    @ApiModelProperty("报警方式 , 1为电话报警, 2为设备报警, 3为短信报警, 4为 GPS报警, 5为视频报警, 6为设备故障报警,7其他报警;可以为直接组合如12为电话报警或设备报警")
    public Integer alarmType;
    @ApiModelProperty("开始时间")
    public Date startTime;
    @ApiModelProperty("结束时间")
    public Date endTime;



}
