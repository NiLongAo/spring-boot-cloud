package cn.com.tzy.springbootentity.param.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel("流媒体请求类")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformGbStreamSaveParam {
    @ApiModelProperty("平台国标ID")
    private String platformId;
    @ApiModelProperty("目录ID")
    private String catalogId;
    @ApiModelProperty("是否关联下级目录（删除）")
    private int isSub;
    @ApiModelProperty("关联所有通道")
    private Integer isAll;
    @ApiModelProperty("流国标信息列表")
    private List<String> gbIdList;
}
