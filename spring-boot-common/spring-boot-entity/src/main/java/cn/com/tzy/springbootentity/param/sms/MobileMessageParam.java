package cn.com.tzy.springbootentity.param.sms;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@ApiModel("短信消息参数")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MobileMessageParam extends PageModel {

    @ApiModelProperty("编号")
    public Long id;

    @ApiModelProperty("发送人id")
    public Integer senderId;

    @ApiModelProperty("模板号")
    public String templateId;

    @ApiModelProperty("类型")
    public Integer type;

    @ApiModelProperty("内容")
    public String content;

    @ApiModelProperty("手机号")
    public String mobile;

    @ApiModelProperty("操作时间")
    public Date handleTime;

    @ApiModelProperty("创建时间")
    public Date createTime;

    @ApiModelProperty("状态")
    public Integer status;

    @ApiModelProperty("响应编号")
    public String msgId;

    @ApiModelProperty("返回状态")
    public String callbackStatus;

    @ApiModelProperty("重发次数")
    public Integer resendNum;

    @ApiModelProperty("变量")
    public String variable;


}