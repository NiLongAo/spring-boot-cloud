package cn.com.tzy.springbootentity.param.sms;

import cn.com.tzy.springbootstartersmsbasic.demo.SendModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ApiModel("发送短信参数")
@Getter
public class SendParam implements SendModel {

    @ApiModelProperty("操作类型")
    @NotNull(message = "发送类型不能为空")
    private Integer type;

    @ApiModelProperty("手机号")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机格式错误")
    private String mobile;


    @ApiModelProperty("租户编号")
    private Long tenantId;

    @Override
    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
