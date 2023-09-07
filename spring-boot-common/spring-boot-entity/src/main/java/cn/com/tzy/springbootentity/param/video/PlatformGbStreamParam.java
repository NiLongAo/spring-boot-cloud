package cn.com.tzy.springbootentity.param.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("流媒体请求类")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformGbStreamParam {
    @ApiModelProperty("平台国标ID")
    private String platformId;
    @ApiModelProperty("目录ID")
    private String catalogId;
    @ApiModelProperty("是否关联下级目录")
    private int isSub;
}
