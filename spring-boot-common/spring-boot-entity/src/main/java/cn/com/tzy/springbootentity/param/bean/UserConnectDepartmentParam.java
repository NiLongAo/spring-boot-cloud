package cn.com.tzy.springbootentity.param.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.List;


@ApiModel("用户关联部门表")
public class UserConnectDepartmentParam{

    @NotNull(message = "未获取到用户编号")
    @ApiModelProperty("用户编号")
    public Long userId;

    @ApiModelProperty("角色编号集合")
    public List<Long> departmentList;
}
