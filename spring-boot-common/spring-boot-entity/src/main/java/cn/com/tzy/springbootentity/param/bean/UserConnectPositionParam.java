package cn.com.tzy.springbootentity.param.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *
 */
@ApiModel("用户关联职位表")
public class UserConnectPositionParam {

    @NotNull(message = "未获取到用户编号")
    @ApiModelProperty("用户编号")
    public Long userId;


    @ApiModelProperty("部门编号集合")
    public List<Long> positionList ;
}
