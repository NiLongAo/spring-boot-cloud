package cn.com.tzy.springbootentity.param.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("平台设备通道请求类")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PlatformGbChannelParam {

    @ApiModelProperty("上级平台的国标编号")
    private  String platformId;

    @ApiModelProperty("目录ID")
    private  String catalogId;

    @ApiModelProperty("是否查询下级目录")
    private int isSub;
}
