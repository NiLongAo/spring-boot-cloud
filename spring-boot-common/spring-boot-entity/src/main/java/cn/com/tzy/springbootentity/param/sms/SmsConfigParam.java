package cn.com.tzy.springbootentity.param.sms;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("短信配置参数")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SmsConfigParam extends PageModel {

    @ApiModelProperty("编号")
    public Integer id;

    @ApiModelProperty("类型")
    public Integer smsType;

    @ApiModelProperty("配置名称")
    public String configName;

    @ApiModelProperty("账号")
    public String account;

    @ApiModelProperty("密码")
    public String password;

    @ApiModelProperty("余额")
    public String balance;

    @ApiModelProperty("是否启用")
    public Integer isActive;

    @ApiModelProperty("签名")
    public String sign;

    @ApiModelProperty("签名位置")
    public Integer signPlace;




}