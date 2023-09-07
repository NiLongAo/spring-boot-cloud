package cn.com.tzy.springbootentity.param.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@ApiModel("平台设备通道请求类")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PlatformGbChannelSaveParam {

    @ApiModelProperty("上级平台的国标编号")
    private String platformId;
    @ApiModelProperty("目录ID")
    private String catalogId;
    @ApiModelProperty("是否关联下级目录（删除）")
    private int isSub;
    @ApiModelProperty("处理所有通道")
    private int isAll;
    @ApiModelProperty("设备通道信息")
    private List<String> gbIdList;
}
