package cn.com.tzy.springbootentity.param.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;


@ApiModel("部门关联权限表")
public class DepartmentConnectPrivilegeParam {

    @ApiModelProperty("部门编号")
    public  Long departmentId;
    @ApiModelProperty("权限编号集合")
    public List<String> privilegeList;

}
