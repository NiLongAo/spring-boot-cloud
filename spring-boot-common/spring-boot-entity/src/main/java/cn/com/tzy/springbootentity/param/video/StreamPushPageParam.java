package cn.com.tzy.springbootentity.param.video;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("推流请求类")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StreamPushPageParam extends PageModel {
    @ApiModelProperty("是否正在推流")
    public Integer pushing;

    @ApiModelProperty("流媒体ID")
    public String mediaServerId;
    
}
