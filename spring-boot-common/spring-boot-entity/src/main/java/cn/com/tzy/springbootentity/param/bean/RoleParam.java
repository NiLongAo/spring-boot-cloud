package cn.com.tzy.springbootentity.param.bean;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@ApiModel("角色信息参数")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RoleParam extends PageModel {

    @ApiModelProperty("编号")
    public Long id;

    @ApiModelProperty("角色名称")
    public String roleName;

    @ApiModelProperty("备注")
    public String memo;
}
