package cn.com.tzy.springbootentity.param.sms;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("短信模板")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MobileMessageTemplateParam extends PageModel{

    @ApiModelProperty("编号")
    public Integer id;

    @ApiModelProperty("短信配置id")
    public Integer configId;

    @ApiModelProperty("类型")
    public Integer type;

    @ApiModelProperty("标题")
    public String title;

    @ApiModelProperty("内容")
    public String content;

    @ApiModelProperty("接收人")
    public String receiver;

    @ApiModelProperty("变量")
    public String variable;

    @ApiModelProperty("编号")
    public String code;

}