package cn.com.tzy.springbootentity.param.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@ApiModel("字典类型表请求参数")
public class DictionaryTypeParam {

    @ApiModelProperty("编号")
    public String id;

    @ApiModelProperty("编码")
    @NotEmpty(message = "未获取到字典编码")
    public String code;

    @ApiModelProperty("状态")
    @NotNull(message = "未获取到状态信息")
    public Integer status;

    @ApiModelProperty("字典类型名称")
    @NotEmpty(message = "未获取到字典类型名称")
    public String name;
}
