package cn.com.tzy.springbootentity.param.video;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("设备通道请求类")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceChannelPageParam extends PageModel {

    @ApiModelProperty("设备国标编号")
    public String deviceId;
    @ApiModelProperty("是否在线")
    public Integer online;
}
