package cn.com.tzy.springbootentity.param.bean;


import cn.com.tzy.springbootcomm.common.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("权限信息参数")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PrivilegeParam extends PageModel {

    @ApiModelProperty("编号")
    public String id;

    @ApiModelProperty("权限名称")
    public String privilegeName;

    @ApiModelProperty("请求路径")
    public String requestUrl;

    @ApiModelProperty("菜单编号")
    public String menuId;

    @ApiModelProperty("备注")
    public String memo;
}
