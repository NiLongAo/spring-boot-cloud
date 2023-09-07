package cn.com.tzy.springbootentity.param.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 *
 */
@ApiModel("角色关联权限表")
public class RoleConnectPrivilegeParam {
    @ApiModelProperty("角色编号")
    public  Long roleId;
    @ApiModelProperty("权限编号集合")
    public List<String> privilegeList;
}
