package cn.com.tzy.springbootentity.param.video;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("平台国标流请求类")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GbStreamPageParam  extends PageModel {

    @ApiModelProperty("平台ID")
    public String platformId;

    @ApiModelProperty("目录ID")
    public String catalogId;

    @ApiModelProperty("流媒体ID")
    public String mediaServerId;
}
