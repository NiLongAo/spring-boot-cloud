package cn.com.tzy.springbootentity.param.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 *
 */
@ApiModel("职位关联权限表")
public class PositionConnectPrivilegeParam {

    @ApiModelProperty("职位编号")
    public  Long positionId;
    @ApiModelProperty("权限编号集合")
    public List<String> privilegeList;

}
