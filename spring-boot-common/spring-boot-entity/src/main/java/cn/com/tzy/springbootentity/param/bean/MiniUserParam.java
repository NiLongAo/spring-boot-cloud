package cn.com.tzy.springbootentity.param.bean;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@ApiModel("微信绑定用户参数")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MiniUserParam extends BaseModel {

    /**
     * 微信小程序主键
     */
    @ApiModelProperty("微信小程序主键")
    @NotNull(message = "未获取微信小程序主键",groups = {add.class})
    public String openId;

    /**
     * 用户主键
     */
    @ApiModelProperty("微信小程序主键")
    @NotNull(message = "未获取用户主键",groups = {add.class})
    public Long userId;

    /**
     * 用户主键
     */
    @ApiModelProperty("微信小程序主键")
    public Long miniId;

}
