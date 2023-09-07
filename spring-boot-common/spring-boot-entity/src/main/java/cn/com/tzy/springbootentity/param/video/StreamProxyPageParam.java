package cn.com.tzy.springbootentity.param.video;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("拉流请求类")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StreamProxyPageParam extends PageModel {
    @ApiModelProperty("是否在线")
    public Integer online;
}
