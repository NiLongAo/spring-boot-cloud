package cn.com.tzy.springbootentity.param.sys;


import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@ApiModel("系统配置请求类")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ConfigParam extends PageModel {

    @ApiModelProperty("配置名称（枚举）")
    public  String k;

    @ApiModelProperty("配置名称")
    public  String configName;

    @ApiModelProperty("配置值")
    public  String v;
}
